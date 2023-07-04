<template>
  <div class="container">
    <b-row>
      <b-col md="12">
        <!-- CONTROLES Y FILTROS -->
        <b-form-group class="mb-0">
          <b-row>
            <b-button
              variant="danger"
              class="mr-2"
              @click="reiniciar_filtros()"
            >
              Reiniciar filtros
            </b-button>
            <b-dropdown
              text="Filtrar por estatus"
              class="mr-2"
              style="min-width: 300px"
              variant="primary"
            >
              <b-dropdown-form>
                <b-form-checkbox-group
                  id="checkbox-group-2"
                  v-model="estatus_filter"
                  name="flavour-2"
                  stacked
                  @change="$refs.table_solicitudes.refresh()"
                >
                  <b-form-checkbox
                    v-for="estatu_item in estatus"
                    :key="'estatus' + estatu_item.id"
                    :value="estatu_item.id"
                  >
                    {{ estatu_item.nombreStatus }}
                  </b-form-checkbox>
                </b-form-checkbox-group>
              </b-dropdown-form>
            </b-dropdown>
            <b-dropdown
              text="Filtrar por tiempo"
              class="mr-2"
              variant="primary"
            >
              <b-dropdown-item-button @click="set_fecha_rango(null)">
                Todas
              </b-dropdown-item-button>
              <b-dropdown-item-button @click="set_fecha_rango(0)">
                Hoy
              </b-dropdown-item-button>
              <b-dropdown-item-button @click="set_fecha_rango(86400000)">
                Hace 1 dia
              </b-dropdown-item-button>
              <b-dropdown-item-button @click="set_fecha_rango(86400000 * 7)">
                Hace 1 semana
              </b-dropdown-item-button>
              <b-dropdown-item-button @click="set_fecha_rango(86400000 * 30)">
                Hace 1 mes
              </b-dropdown-item-button>
            </b-dropdown>
            <b-button
              variant="primary"
              @click="$refs.table_solicitudes.refresh()"
            >
              Refrescar
            </b-button>
          </b-row>
          <b-row>
            <b-input-group class="my-3 w-25 ml-auto">
              <b-form-input
                id="filter-input"
                v-model="buscar"
                type="search"
                placeholder="Buscar"
              ></b-form-input>
              <b-input-group-append class="mr-3">
                <b-button
                  style="background-color: #19325b; color: white"
                  @click="search"
                >
                  Ir
                </b-button>
              </b-input-group-append>
            </b-input-group>
          </b-row>
        </b-form-group>

        <!-- TABLA PRINCIPAL  -->
        <b-table
          ref="table_solicitudes"
          striped
          hover
          :items="fetch_solicitudes"
          :fields="campos"
          :busy.sync="loading"
          :sort-by.sync="sortBy"
          :sort-desc.sync="sortDesc"
          :per-page="perPage"
          :current-page="current_page"
          show-empty
        >
          <template #table-busy>
            <div class="text-center text-danger my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong>Cargando...</strong>
            </div>
          </template>
          <template #empty>
            <div class="text-center my-2">No hay resultados</div>
          </template>
          <template v-slot:cell(id)="{ item }">
            {{ new Date(item.fecha).getFullYear() + "-" + item.id }}
          </template>
          <template v-slot:cell(fecha)="{ item }">
            {{ new Date(item.fecha).toLocaleString("es-CO") }}
          </template>
          <template v-slot:cell(accion)="{ item }">
            <b-dropdown text="Acciones" class="m-md-2">
              <b-dropdown-item-button @click="opcion_detalles(item)">
                Detalles
              </b-dropdown-item-button>
              <b-dropdown-item-button
                v-if="isAdmin && item.status.id != 9"
                @click="opcion_estatus(item)"
              >
                Cambiar Estatus
              </b-dropdown-item-button>
              <b-dropdown-item-button
                v-if="isAdmin && item.status.id != 9"
                @click="opcion_estatus_remitido_por_el_usuario(item)"
              >
                Remitido por el usuario
              </b-dropdown-item-button>
              <b-dropdown-item-button
                v-if="isAdmin && item.status.id != 9"
                @click="opcion_estatus_ignorar(item)"
              >
                Ignorar
              </b-dropdown-item-button>
              <template v-if="item.status.id != 10">
                <b-dropdown-item-button
                  v-if="item.status.id == 9"
                  @click="opcion_descombinar(item)"
                >
                  Descombinar
                </b-dropdown-item-button>
                <b-dropdown-item-button
                  v-if="item.status.id != 9"
                  @click="opcion_reenviar(item)"
                >
                  Reenviar
                </b-dropdown-item-button>
                <b-dropdown-item-button
                  v-if="item.status.id != 6 && item.status.id != 9"
                  @click="opcion_asignacion(item)"
                >
                  {{ item.correo_asignacion ? "Reasignar" : "Asignar" }}
                </b-dropdown-item-button>
                <b-dropdown-item-button
                  v-if="
                    item.status.id != 2 &&
                    item.status.id != 9 &&
                    item.status.id != 6
                  "
                  @click="opcion_respuesta(item, 7)"
                >
                  Solicitar información
                </b-dropdown-item-button>
                <b-dropdown-item-button
                  v-if="item.status.id != 2 && item.status.id != 9"
                  @click="opcion_respuesta(item, 3)"
                >
                  Resolución y Cierre
                </b-dropdown-item-button>
              </template>
            </b-dropdown>
          </template>
        </b-table>

        <!-- PAGINACION -->
        <div class="d-flex justify-content-end align-items-center">
          <label class="mr-2">por pagina:</label>
          <b-form-select v-model="perPage" style="width: min-content">
            <b-form-select-option :value="10">10</b-form-select-option>
            <b-form-select-option :value="20">20</b-form-select-option>
            <b-form-select-option :value="30">30</b-form-select-option>
            <b-form-select-option :value="50">50</b-form-select-option>
            <b-form-select-option :value="100">100</b-form-select-option>
          </b-form-select>
          <b-pagination
            v-model="current_page"
            :per-page="perPage"
            :total-rows="total_rows"
            :prev-text="'Anterior'"
            :next-text="'Siguiente'"
            :class="'pagination m-0 mb-0'"
            align="right"
          ></b-pagination>
        </div>
      </b-col>
    </b-row>

    <!--  ----------------------- MODALS ----------------------- -->
    <!-- Modal detalles -->
    <b-modal
      id="modal-detalles"
      :key="'modal-detalles'"
      :title="'Detalles de la solicitud'"
      :ok-title="'Cerrar'"
      scrollable
      size="lg"
      :modal-class="maximizar ? 'modal-fullscreen' : ''"
    >
      <template #modal-header="{ close }">
        <h5 class="modal-title">Detalles de la Solicitud</h5>
        <div>
          <button type="button" class="close" @click="close()">
            <span aria-hidden="true">&times;</span>
          </button>
          <button type="button" class="close" @click="maximizar = !maximizar">
            <b-icon icon="arrows-angle-expand" font-scale="0.75"></b-icon>
          </button>
        </div>
      </template>
      <SolicitudDetalles
        ref="modal_detalles"
        :solicitud="solicitud_seleccionada"
      ></SolicitudDetalles>
    </b-modal>

    <!-- Modal cambio de estatus -->
    <b-modal
      id="modal-reenviar"
      :key="'modal-reenviar'"
      :title="'Reenviar'"
      :ok-title="'reenviar'"
      scrollable
      size="lg"
      @ok="reenviar_action"
    >
      <ReenviarForm
        ref="modal_reenviar"
        :solicitud="solicitud_seleccionada"
      ></ReenviarForm>
    </b-modal>

    <!-- Modal cambio de estatus -->
    <b-modal
      id="modal-estatus"
      :key="'modal-estatus'"
      :title="'Cambiar estatus'"
      :ok-title="'cambiar'"
      scrollable
      size="lg"
      @ok="cambiar_estatus_action"
    >
      <SolicitudCambiarEstatus
        ref="modal_cambiar_estatus"
        :solicitud="solicitud_seleccionada"
        :estatus="estatus"
        @success="$refs.table_solicitudes.refresh()"
      ></SolicitudCambiarEstatus>
    </b-modal>

    <!-- Modal asignacion -->
    <b-modal
      id="modal-asignacion"
      :key="'modal-asignacion'"
      :title="'Asignacion de solicitud'"
      :ok-title="'Asignar'"
      scrollable
      size="lg"
      @ok.prevent="asignacion_action"
    >
      <AsignacionForm
        ref="modal_asignacion"
        v-model="solicitud_seleccionada"
        @success="$refs.table_solicitudes.refresh()"
      ></AsignacionForm>
    </b-modal>

    <!-- Modal descombinar -->
    <b-modal
      id="modal-descombinacion"
      :title="'Descombinar solicitud'"
      :ok-title="'Descombinar'"
      scrollable
      :ok-disabled="!solicitud_seleccionada"
      @ok="descombinar_action"
    >
      <p>¿Esta seguro que desea descombinar la solicitud?:</p>
    </b-modal>

    <!-- Modal cambiar a ignorar -->
    <b-modal
      id="modal-ignorar"
      :title="'Cambiar estatus'"
      :ok-title="'Cambiar'"
      scrollable
      :ok-disabled="!solicitud_seleccionada"
      @ok="cambiar_estatus_action_standalone(10)"
    >
      <p>
        ¿Esta seguro que desea cambiar el estatus de la solicitud a ignorar?
      </p>
    </b-modal>

    <!-- Modal cambiar a remitido por el usuario -->
    <b-modal
      id="modal-remitido-por-el-usuario"
      :title="'Cambiar estatus'"
      :ok-title="'Cambiar'"
      scrollable
      :ok-disabled="!solicitud_seleccionada"
      @ok="cambiar_estatus_action_standalone(11)"
    >
      <p>
        ¿Esta seguro que desea cambiar el estatus de la solicitud a Remitido por
        el usuario?
      </p>
    </b-modal>
  </div>
</template>

<script>
import axios from "axios";
import Login from "@/router/auth.vue";
import AsignacionForm from "./AsignacionForm.vue";
import SolicitudDetalles from "./SolicitudDetalles.vue";
import SolicitudCambiarEstatus from "./SolicitudCambiarEstatus.vue";
import ReenviarForm from "./ReenviarForm.vue";

export default {
  name: "ListSolicitudes",
  components: {
    AsignacionForm,
    SolicitudDetalles,
    SolicitudCambiarEstatus,
    ReenviarForm,
  },
  props: {
    formulario: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      loading: false,
      user: Login.methods.user,
      maximizar: false,
      current_page: 1,
      perPage: 10,
      solicitudes: [],
      filtro_desde: "",
      filtro_hasta: "",
      estatus: [],
      sortBy: null,
      sortDesc: false,
      estatus_banned: [5, 8],
      estatus_filter: [1],
      solicitud_seleccionada: null,
      buscar: "",
      total_rows: 1,
      asignar_fields: {
        id: null,
        entidades: [],
        CC: "",
        contenido: null,
        correo_solicitante: "",
        correo_asignacion: "",
        telefono_asignacion: "",
      },
      colors_mapper: {
        "En espera": "danger",
        "Remitido por competencia": "warning",
        "Gestionado y cierre": "success",
        "Remitido por competencia con Resolución y cierre": "success",
        "Remitido automatico": "warning",
        "Solicitando mas informacion": "info",
        "Respondida con mas informacion": "danger",
        "Remitido por el usuario": "warning",
        Reenviado: "info",
        Combinada: "primary",
        Ignorar: "dark",
      },
      campos: [
        {
          key: "id",
          label: "#",
          sortable: true,
        },
        {
          key: "asunto",
          label: "Asunto",
          sortable: true,
        },
        {
          key: "correo_solicitante",
          label: "Correo solicitante",
          sortable: false,
        },
        {
          key: "fecha",
          label: "Fecha",
          sortable: true,
        },
        {
          key: "status.nombreStatus",
          label: "Estatus",
          sortable: true,
        },
        {
          key: "accion",
          label: "Acciones",
        },
      ],
    };
  },

  computed: {
    isAdmin() {
      return this.user.permisos.includes("Administrador");
    },
  },

  mounted() {
    if (this.formulario) {
      this.fetch_estatus();
    } else {
      this.$router.push({
        name: "formularioSeleccion",
        query: { combinar: 1 },
      });
    }
  },

  methods: {
    set_solicitud_seleccionada(solicitud) {
      this.solicitud_seleccionada = { ...solicitud };
    },

    set_fecha_rango(value) {
      if (value != null) {
        const options = {
          hour: "2-digit",
          minute: "2-digit",
          second: "2-digit",
        };
        let today = new Date(new Date().setHours(23, 59, 59, 0));
        let desde_date = new Date(Date.now() - value);
        this.filtro_desde =
          new Date(
            desde_date.getTime() - desde_date.getTimezoneOffset() * 60 * 1000,
          )
            .toISOString()
            .substring(0, 10) + " 00:00:00";

        this.filtro_hasta =
          new Date(today.getTime() - today.getTimezoneOffset() * 60 * 1000)
            .toISOString()
            .substring(0, 10) +
          " " +
          today.toLocaleTimeString("es-ES", options);
      } else {
        this.filtro_desde = "";
        this.filtro_hasta = "";
      }

      this.current_page = 1;
      this.$refs.table_solicitudes.refresh();
    },

    opcion_respuesta(solicitud, statusId = 3) {
      this.$router.push({
        name: "respuesta",
        params: {
          solicitud: solicitud,
          statusId: statusId,
          formulario_url: this.$route.params.formulario_url,
        },
      });
    },

    /* ------------- Detalles modal ----------- */
    opcion_detalles(solicitud) {
      this.set_solicitud_seleccionada(solicitud);
      this.$bvModal.show("modal-detalles");
    },
    /* ------------- Detalles modal END ----------- */

    /* ------------- Asignacion modal ----------- */
    opcion_asignacion(solicitud) {
      this.set_solicitud_seleccionada(solicitud);
      this.$bvModal.show("modal-asignacion");
      this.$nextTick().then(() => {
        document
          .querySelector(".modal-content[tabindex]")
          ?.removeAttribute("tabindex");
      });
    },

    async asignacion_action() {
      try {
        const result = await this.$refs.modal_asignacion.AsignarSolicitud();
        if (result !== undefined) {
          await this.$bvModal
            .msgBoxOk("Asignacion realizada correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.$bvModal.hide("modal-asignacion");
              this.$refs.table_solicitudes.refresh();
            });
        }
      } catch (error) {
        this.$bvModal.msgBoxOk(error.response?.data?.message, {
          title: "ERROR",
          size: "sm",
          buttonSize: "sm",
          okVariant: "danger",
          headerClass: "p-2 border-bottom-0",
          footerClass: "p-2 border-top-0",
          centered: true,
        });
      }
    },
    /* ------------- Asignacion modal END ----------- */

    /* ------------- Descombinar modal ----------- */

    opcion_descombinar(solicitud) {
      this.set_solicitud_seleccionada(solicitud);
      this.$bvModal.show("modal-descombinacion");
    },

    descombinar_action() {
      axios
        .put(
          process.env.VUE_APP_URL +
            "/solicitudes/descombinar/" +
            this.solicitud_seleccionada.id,
        )
        .then(() => {
          this.$bvModal
            .msgBoxOk("Solicitudes descombinada correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.$refs.table_solicitudes.refresh();
            });
        })
        .catch(() => {
          this.$bvModal.msgBoxOk("No se pudo descombinar la solicitud", {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },

    /* ------------- Descombinar modal END ----------- */

    /* ------------- Descombinar modal ----------- */

    opcion_reenviar(solicitud) {
      this.set_solicitud_seleccionada(solicitud);
      this.$bvModal.show("modal-reenviar");
      this.$nextTick().then(() => {
        document
          .querySelector(".modal-content[tabindex]")
          ?.removeAttribute("tabindex");
      });
    },

    reenviar_action() {
      this.$refs.modal_reenviar
        .Reenviar()
        .then(() => {
          this.$bvModal
            .msgBoxOk("Informacion reenviada", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.$refs.table_solicitudes.refresh();
            });
        })
        .catch((error) => {
          console.log(error);
          return this.$bvModal.msgBoxOk(error.response.data.message, {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },

    /* ------------- Descombinar modal END ----------- */

    /* ------------- Cambiar estatus modal ----------- */
    opcion_estatus(solicitud) {
      this.set_solicitud_seleccionada(solicitud);
      this.$bvModal.show("modal-estatus");
      this.$nextTick().then(() => {
        document
          .querySelector(".modal-content[tabindex]")
          ?.removeAttribute("tabindex");
      });
    },

    opcion_estatus_remitido_por_el_usuario(solicitud) {
      this.set_solicitud_seleccionada(solicitud);
      this.$bvModal.show("modal-remitido-por-el-usuario");
      this.$nextTick().then(() => {
        document
          .querySelector(".modal-content[tabindex]")
          ?.removeAttribute("tabindex");
      });
    },

    opcion_estatus_ignorar(solicitud) {
      this.set_solicitud_seleccionada(solicitud);
      this.$bvModal.show("modal-ignorar");
      this.$nextTick().then(() => {
        document
          .querySelector(".modal-content[tabindex]")
          ?.removeAttribute("tabindex");
      });
    },

    cambiar_estatus_action_standalone(status_id) {
      return axios
        .put(
          process.env.VUE_APP_URL +
            "/status/" +
            status_id +
            "/solicitudes/" +
            this.solicitud_seleccionada.id,
        )
        .then(() => {
          this.$bvModal
            .msgBoxOk("Estatus cambiado correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.$refs.table_solicitudes.refresh();
            });
        })
        .catch((error) => {
          console.log(error);
          return this.$bvModal.msgBoxOk(error.response.data.message, {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },

    cambiar_estatus_action() {
      this.$refs.modal_cambiar_estatus
        .CambiarEstatus()
        .then(() => {
          this.$bvModal
            .msgBoxOk("Estatus cambiado correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.$refs.table_solicitudes.refresh();
            });
        })
        .catch((error) => {
          console.log(error);
          return this.$bvModal.msgBoxOk(error.response.data.message, {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },
    /* ------------- Cambiar estatus modal END ----------- */

    // Se recalcula el numero total de filas para la paginacion.
    onFiltered() {
      this.current_page = 1;
    },

    reiniciar_filtros() {
      this.estatus_filter = [1];
      this.filtro_desde = "";
      this.filtro_hasta = "";
      this.buscar = "";
      this.sortBy = null;
      this.current_page = 1;
      this.$refs.table_solicitudes.refresh();
    },

    // Se obtienen los posibles estatus
    fetch_estatus() {
      axios.get(process.env.VUE_APP_URL + "/status").then((response) => {
        this.estatus = response.data.filter(
          (estatus) => !this.estatus_banned.includes(estatus.id),
        );
      });
    },

    // Busqueda
    search() {
      this.current_page = 1;
      this.$refs.table_solicitudes.refresh();
    },

    // Se obtienen las solicitudes.
    fetch_solicitudes(ctx, callback) {
      this.loading = true;
      axios
        .get(process.env.VUE_APP_URL + "/solicitudes", {
          params: {
            page: ctx.currentPage - 1,
            size: ctx.perPage,
            sort: ctx.sortBy,
            direction: ctx.sortDesc ? "desc" : "asc",
            desde: this.filtro_desde,
            hasta: this.filtro_hasta,
            status: this.estatus_filter.join(","),
            buscar: this.buscar.trim(),
            formulario: this.formulario.id,
          },
        })
        .then((e) => {
          this.solicitudes = e.data.content;
          this.solicitudes.map((solicitud) => {
            solicitud.entidadDepartamentoMunicipio =
              solicitud.entidadDepartamentoMunicipio
                ? JSON.parse(solicitud.entidadDepartamentoMunicipio)
                : [];
            solicitud.solicitudes.forEach((solicitud_child) => {
              solicitud_child.entidadDepartamentoMunicipio =
                solicitud_child.entidadDepartamentoMunicipio
                  ? JSON.parse(solicitud_child.entidadDepartamentoMunicipio)
                  : [];
            });
            solicitud["_cellVariants"] = {
              "status.nombreStatus":
                this.colors_mapper[solicitud.status.nombreStatus],
            };
          });
          this.current_page = ctx.currentPage;
          this.perPage = ctx.perPage;
          this.total_rows = e.data.totalElements;
          this.loading = false;
          callback(this.solicitudes);
        })
        .catch(() => {
          this.loading = false;
          this.$bvModal.msgBoxOk("Error al listar las solicitudes", {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
          callback([]);
        });

      return null;
    },
  },
};
</script>

<style>
.modal-fullscreen .modal-dialog {
  max-width: 100%;
  margin-left: 17px;
  margin-top: 0px;
  margin-bottom: 0px;
  min-height: 100vh;
}

.modal-fullscreen .modal-dialog .modal-content {
  max-height: none;
}
</style>
