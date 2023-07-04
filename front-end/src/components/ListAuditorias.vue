<template>
  <div>
    <div class="row" style="width: 70%; margin-inline: auto">
      <b-col md="12">
        <b-form-group class="mb-3">
          <b-row>
            <b-col md="3">
              <label>Fecha desde:</label>
              <b-form-input v-model="fecha_desde" type="date" />
            </b-col>
            <b-col md="3">
              <label>Fecha hasta</label>
              <b-form-input v-model="fecha_hasta" type="date" />
            </b-col>
            <b-col md="3">
              <label>Usuario:</label>
              <b-form-input
                id="filter-input"
                v-model="search_usuarioModified"
                type="search"
              ></b-form-input>
            </b-col>
            <b-col md="3">
              <label>Nro. Solicitud:</label>
              <b-form-input
                id="filter-input"
                v-model="search_idSolicitud"
                type="search"
              ></b-form-input>
            </b-col>
          </b-row>
          <b-row>
            <b-col md="2">
              <b-button
                style="background-color: #19325b; color: white"
                @click="$refs.auditoria_table.refresh()"
              >
                Ir
              </b-button>
            </b-col>
            <b-col md="2">
              <b-button
                variant="danger"
                class="mr-2"
                @click="reiniciar_filtros()"
              >
                Reiniciar filtros
              </b-button>
            </b-col>
          </b-row>
        </b-form-group>
      </b-col>
      <b-table
        ref="auditoria_table"
        striped
        hover
        :items="fetch_auditorias"
        :fields="campos"
        :busy.sync="loading"
        :per-page="perPage"
        :current-page="current_page"
        @row-clicked="myRowClickHandler"
      >
        <template #table-busy>
          <div class="text-center text-danger my-2">
            <b-spinner class="align-middle"></b-spinner>
            <strong>Cargando...</strong>
          </div>
        </template>
        <template v-slot:cell(updateDateTime)="{ item }">
          {{ new Date(item.updateDateTime).toLocaleString("es-CO") }}
        </template>
        <template v-slot:cell(modulo)="{ item }">
          <span v-if="item.codigo">Dependencia</span>
          <span v-if="item.id_formulario">Formulario</span>
          <span v-if="item.id_solicitud">Solicitud</span>
          <span v-if="item.id_encuesta">Encuesta</span>
          <span v-if="item.id_roles">Roles</span>
          <span v-if="item.id_permiso">Permisos</span>
          <span v-if="item.id_user">Usuarios</span>
        </template>
        <template v-slot:cell(revtype)="{ item }">
          <span v-if="item.revtype == 0">Agregar</span>
          <span v-if="item.revtype == 1">Actualizar</span>
          <span v-if="item.revtype == 2">Eliminar</span>
        </template>
        <template v-slot:cell(id_solicitud)="{ item }">
          <span v-if="item.id_solicitud">{{
            new Date(item.create_date_time).getFullYear() +
            "-" +
            item.id_solicitud
          }}</span>
        </template>
      </b-table>
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
          :total-rows="totalRows"
          :prev-text="'Anterior'"
          :next-text="'Siguiente'"
          :class="'pagination m-0 mb-0'"
          align="right"
        ></b-pagination>
      </div>
    </div>
  </div>
</template>
<script>
import axios from "axios";

export default {
  name: "ListAuditorias",
  data() {
    return {
      auditorias: {
        content: [],
      },
      perPage: 10,
      totalRows: 0,
      current_page: 1,
      loading: false,
      fecha_desde: "",
      fecha_hasta: "",
      search_usuarioModified: "",
      search_idSolicitud: "",
      campos: [
        {
          key: "id",
          label: "#",
          sortable: true,
        },
        {
          key: "updateDateTime",
          label: "Fecha",
          sortable: true,
        },
        {
          key: "usuarioModified",
          label: "Usuario",
          sortable: true,
        },
        {
          key: "modulo",
          label: "Modulo",
        },
        {
          key: "revtype",
          label: "Evento",
          sortable: true,
        },
        {
          key: "id_solicitud",
          label: "Nro Solicitud",
        },
      ],
    };
  },
  methods: {
    fetch_auditorias(ctx, callback) {
      this.loading = true;
      console.log(ctx);
      axios
        .get(process.env.VUE_APP_URL + "/auditoria", {
          params: {
            page: ctx.currentPage - 1,
            size: ctx.perPage,
            sort: ctx.sortBy,
            direction: ctx.sortDesc ? "desc" : "asc",
            updateDateTimeStart: this.fecha_desde,
            updateDateTimeEnd: this.fecha_hasta,
            usuarioModified: this.search_usuarioModified,
            filterField: this.search_field,
            filterValue: this.search_Value,
            idSolicitud: this.search_idSolicitud,
          },
        })
        .then((result) => {
          this.totalRows = result.data.totalElements;
          this.currentPage = ctx.currentPage;
          this.perPage = ctx.perPage;
          callback(result.data.content);
        })
        .catch((error) => {
          console.log(error);
          callback([]);
        })
        .finally(() => {
          this.loading = false;
        });
      return null;
    },

    reiniciar_filtros() {
      this.fecha_desde = "";
      this.fecha_hasta = "";
      this.search_usuarioModified = "";
      this.search_idSolicitud = "";
      this.$refs.auditoria_table.refresh();
    },
  },
};
</script>
<style scoped></style>
