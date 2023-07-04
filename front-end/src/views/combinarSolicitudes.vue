<template>
  <div class="container">
    <b-row>
      <b-col md="12">
        <b-form-group class="mb-0">
          <b-row>
            <b-input-group class="my-3">
              <b-button
                v-b-modal.modal-combinacion
                type="button"
                class="mx-3"
                style="background-color: #19325b; color: white"
                :disabled="solicitudes_seleccionadas.length < 2"
              >
                Combinar
              </b-button>
              <b-form-input
                id="filter-input"
                v-model="buscar"
                type="search"
                placeholder="Buscar"
              ></b-form-input>
              <b-input-group-append class="mr-3">
                <b-button
                  style="background-color: #19325b; color: white"
                  :disabled="!buscar"
                  @click="buscar = ''"
                >
                  Limpiar
                </b-button>
              </b-input-group-append>
            </b-input-group>
          </b-row>
        </b-form-group>
        <b-table
          striped
          hover
          :items="solicitudes"
          :fields="campos"
          :filter="buscar"
          :busy.sync="loading"
          :filter-included-fields="[
            'id',
            'asunto',
            'correo_solicitante',
            'nombreStatus',
          ]"
          :per-page="10"
          :current-page="current_page"
          :tbody-tr-class="tbodyRowClass"
          @row-clicked="fila_seleccionada"
          @filtered="onFiltered"
        >
          <template #table-busy>
            <div class="text-center text-danger my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong>Cargando...</strong>
            </div>
          </template>
          <template v-slot:cell(seleccion)="{ item, field: { key } }">
            <b-checkbox v-model="item[key]"></b-checkbox>
          </template>
        </b-table>
        <b-pagination
          v-model="current_page"
          :per-page="10"
          :total-rows="total_rows"
          :prev-text="'Anterior'"
          :next-text="'Siguiente'"
          :class="'pagination'"
          align="right"
        ></b-pagination>
      </b-col>
    </b-row>
    <b-modal
      id="modal-combinacion"
      :title="'Combinar solicitudes'"
      :ok-title="'Combinar'"
      scrollable
      :ok-disabled="!solicitud_principal"
      @ok="combinar_request"
      @hidden="cerrar_modal"
    >
      <p>¿Esta seguro que desea combinar las siguientes solicitudes?:</p>
      <b-list-group>
        <b-list-group-item
          v-for="solicitud in solicitudes_seleccionadas"
          :key="'combinar' + solicitud.id"
          class="d-flex justify-content-between align-items-center list-group-item-light"
        >
          {{ solicitud.asunto }}
          <b-badge variant="primary" pill>{{ solicitud.id }}</b-badge>
        </b-list-group-item>
      </b-list-group>
      <p class="my-3">
        Si es asi, debe seleccionar la solicitud padre a la cual se relacionaran
        las demás solicitudes como hijas.
      </p>

      <b-form-select v-model="solicitud_principal">
        <template #first>
          <b-form-select-option :value="null" disabled
            >-- Seleccionar solicitud padre --</b-form-select-option
          >
        </template>
        <b-form-select-option
          v-for="seleccionada in solicitudes_seleccionadas"
          :key="'select' + seleccionada.id"
          :value="seleccionada.id"
        >
          {{ seleccionada.id }} -
          {{ seleccionada.asunto }}
        </b-form-select-option>
      </b-form-select>
    </b-modal>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "CombinarSolicitudes",
  props: ["formulario"],
  data() {
    return {
      loading: false,
      current_page: 1,
      solicitudes: [],
      solicitud_principal: null,
      buscar: "",
      total_rows: 1,
      campos: [
        {
          key: "seleccion",
          label: "Selección",
        },
        {
          key: "id",
          label: "#",
        },
        {
          key: "asunto",
          label: "Asunto",
        },
        {
          key: "correo_solicitante",
          label: "Correo solicitante",
        },
        {
          key: "status.nombreStatus",
          label: "Estatus",
        },
      ],
    };
  },

  computed: {
    solicitudes_seleccionadas() {
      return this.solicitudes.filter((item) => item.seleccion);
    },
  },

  mounted() {
    if (this.formulario) {
      this.fetch_solicitudes();
    } else {
      this.$router.push({
        name: "formularioSeleccion",
        query: { combinar: 1 },
      });
    }
  },

  methods: {
    cerrar_modal() {
      this.solicitud_principal = null;
    },

    // Combianr request
    combinar_request() {
      axios
        .post(process.env.VUE_APP_URL + "/solicitudes/combinar", {
          solicitud_base_id: this.solicitud_principal,
          solicitudes: this.solicitudes_seleccionadas.filter(
            (solicitud) => solicitud.id != this.solicitud_principal,
          ),
        })
        .then(() => {
          this.$bvModal
            .msgBoxOk("Solicitudes combinadas correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.fetch_solicitudes();
            });
        })
        .catch(() => {
          this.$bvModal.msgBoxOk("No se pudo combinar las solicitudes", {
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
    // Clase de la fila seleccionada
    tbodyRowClass(item) {
      if (item?.seleccion) {
        return ["b-table-row-selected", "table-primary", "cursor-pointer"];
      } else {
        return ["cursor-pointer"];
      }
    },

    // Se altera la fila seleccionada con el flag respectivo
    fila_seleccionada(fila) {
      if (fila.seleccion) {
        this.$set(fila, "seleccion", false);
      } else {
        this.$set(fila, "seleccion", true);
      }
    },

    // Se recalcula el numero total de filas para la paginacion.
    onFiltered(filteredItems) {
      this.total_rows = filteredItems.length;
    },

    // Se obtienen las solicitudes.
    fetch_solicitudes() {
      this.loading = true;
      axios
        .get(
          process.env.VUE_APP_URL +
            "/solicitudes?formulario=" +
            this.formulario.id,
        )
        .then((e) => {
          this.solicitudes = e.data.filter(
            (solicitud) => solicitud.status.id != 9,
          );
          this.solicitudes.map(
            (solicitud) =>
              (solicitud["nombreStatus"] = solicitud.status.nombreStatus),
          );
          this.total_rows = this.solicitudes.length;
          this.loading = false;
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
        });
    },
  },
};
</script>

<style></style>
