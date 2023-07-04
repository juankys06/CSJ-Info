<template>
  <div class="consulta">
    <Loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></Loading>
    <h1 style="margin-top: -2%; text-align: center">Consulta</h1>
    <div class="numero" style="margin-left: 10%">
      <label class="col-md-2 col-form-label">Nro de Solicitud</label>
      <div class="form-group row align-items-center">
        <div class="col-md-2">
          <input
            id="numero"
            v-model="numero"
            type="text"
            class="form-control"
            required
          />
        </div>

        <button class="btn btn-primary" @click="BuscarSolicitud()">
          Buscar
        </button>
      </div>
    </div>
    <div>
      <table
        v-if="consulta.length != 0"
        id="regTable"
        class="table table-striped table-hover"
        style="margin: auto; width: 70%"
      >
        <thead>
          <tr>
            <th scope="col">Nro</th>
            <th scope="col">Fecha</th>
            <th scope="col">Estatus</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td scope="row">
              {{
                new Date(consulta.createDateTime).getFullYear() +
                "-" +
                consulta.id
              }}
            </td>
            <td>
              {{
                new Date(consulta.updateDateTime).getDate() +
                "/" +
                (new Date(consulta.updateDateTime).getMonth() + 1) +
                "/" +
                new Date(consulta.updateDateTime).getFullYear() +
                " " +
                (
                  "0" + (new Date(consulta.updateDateTime).getHours() % 12) ||
                  12
                ).slice(-2) +
                ":" +
                ("0" + new Date(consulta.updateDateTime).getMinutes()).slice(
                  -2,
                ) +
                " " +
                (new Date(consulta.updateDateTime).getHours() >= 12
                  ? "PM"
                  : "AM")
              }}
            </td>
            <td v-if="consulta.status.id == 5" style="background: red">
              {{ estatus[consulta.status.id - 1].nombreStatus }}
            </td>
            <td v-else>{{ estatus[consulta.status.id - 1].nombreStatus }}</td>
            <td>
              <div class="btn-group">
                <button
                  type="button"
                  class="btn btn-default dropdown-toggle"
                  data-toggle="dropdown"
                >
                  Acciones
                </button>
                <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                  <a
                    href="#Detalles"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="detalles(consulta.id)"
                    >Detalles</a
                  >
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div
      id="Detalles"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div
        class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable"
        role="document"
      >
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Detalles de la consulta</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div class="modal-body">
            <div v-if="auditoria.length != 0" class="table-responsive">
              <table
                v-if="pag"
                id="regTable"
                class="table table-striped table-hover"
                style="margin: auto"
              >
                <thead>
                  <tr>
                    <th scope="col">Nro</th>
                    <th scope="col">Evento</th>
                    <th scope="col">Fecha</th>
                    <th scope="col">dependencia(s)</th>
                    <th scope="col">Teléfono(s)</th>
                    <th scope="col">Correo(s)</th>
                    <th scope="col">Departamento / Municipio</th>
                    <th scope="col">Estatus</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(auditoria_item, index) in auditoria
                      .slice()
                      .reverse()"
                    :key="auditoria_item.id + '_' + index"
                  >
                    <td scope="row">
                      {{
                        new Date(
                          auditoria_item.create_date_time,
                        ).getFullYear() +
                        "-" +
                        auditoria_item.id_solicitud
                      }}
                    </td>
                    <td>
                      <p v-if="auditoria_item.revtype == 0">
                        Creada la solicitud
                      </p>
                      <p v-if="auditoria_item.revtype == 1">Actualizada</p>
                    </td>
                    <td>
                      {{
                        new Date(auditoria_item.updateDateTime).getDate() +
                        "/" +
                        (new Date(auditoria_item.updateDateTime).getMonth() +
                          1) +
                        "/" +
                        new Date(auditoria_item.updateDateTime).getFullYear() +
                        " " +
                        (
                          "0" +
                            (new Date(
                              auditoria_item.updateDateTime,
                            ).getHours() %
                              12) || 12
                        ).slice(-2) +
                        ":" +
                        (
                          "0" +
                          new Date(auditoria_item.updateDateTime).getMinutes()
                        ).slice(-2) +
                        " " +
                        (new Date(auditoria_item.updateDateTime).getHours() >=
                        12
                          ? "PM"
                          : "AM")
                      }}
                    </td>
                    <template
                      v-if="auditoria_item.edm && auditoria_item.edm.length > 0"
                    >
                      <td>
                        <ul>
                          <li
                            v-for="(
                              entidad_item, index_entidad
                            ) in auditoria_item.edm"
                            :key="
                              index_entidad +
                              'entidad_nombre' +
                              entidad_item.codigo
                            "
                          >
                            {{ entidad_item.entidad.nombre }}
                          </li>
                        </ul>
                      </td>
                      <td>
                        <ul>
                          <li
                            v-for="(
                              entidad_item, index_entidad
                            ) in auditoria_item.edm"
                            :key="
                              index_entidad +
                              'entidad_telefonos' +
                              entidad_item.codigo
                            "
                          >
                            {{ entidad_item.telefono_asignacion }}
                          </li>
                        </ul>
                      </td>
                      <td>
                        <ul>
                          <li
                            v-for="(
                              entidad_item, index_entidad
                            ) in auditoria_item.edm"
                            :key="
                              index_entidad +
                              'entidad_correos' +
                              entidad_item.codigo
                            "
                          >
                            {{ entidad_item.correo_asignacion }}
                          </li>
                        </ul>
                      </td>
                      <td>
                        <ul>
                          <li
                            v-for="(
                              entidad_item, index_entidad
                            ) in auditoria_item.edm"
                            :key="
                              index_entidad + 'entidad_dm' + entidad_item.codigo
                            "
                          >
                            {{
                              entidad_item.departamento
                                ? entidad_item.departamento.nombre
                                : entidad_item.municipio
                                ? entidad_item.municipio.nombre
                                : "-"
                            }}
                          </li>
                        </ul>
                      </td>
                    </template>
                    <template v-else>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                    </template>
                    <td v-if="consulta.status.id == 5" style="background: red">
                      {{ estatus[auditoria_item.status_id - 1].nombreStatus }}
                    </td>
                    <td v-else>
                      {{ estatus[auditoria_item.status_id - 1].nombreStatus }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-default"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
//import Swal from 'sweetalert2'
import axios from "axios";
import Vue from "vue";
import Paginate from "vuejs-paginate";
Vue.component("paginate", Paginate);
export default {
  components: {
    Loading,
  },
  data() {
    return {
      consulta: [],
      entidad: [],
      detallesEntidad: [],
      departamentoDetalle: [],
      auditoria: [],
      estatus: [],
      i: 0,
      parPage: 10,
      currentPage: 1,
      pag: true,
      numero: "",
      isLoading: false,
      entidadDepartamentoMunicipio: [],
    };
  },
  /* computed: {
     getItems: function() {
      let current = this.currentPage * this.parPage;
      let start = current - this.parPage;
      return this.consulta.slice(start, current);
     },
     getPageCount: function() {
      return Math.ceil(this.consulta.length / this.parPage);
     }
   },*/
  mounted() {
    let vm = this;
    var today = new Date();

    this.numero = today.getFullYear() + "-";

    axios.get(process.env.VUE_APP_URL + "/status").then(function (response) {
      vm.estatus = response.data;
    });
    axios
      .get(process.env.VUE_APP_URL + "/entidadDepartamentoMunicipio")
      .then(function (response) {
        vm.entidadDepartamentoMunicipio = response.data;
      });
  },
  methods: {
    detalles(auditoriaId) {
      this.isLoading = true;
      axios
        .get(
          process.env.VUE_APP_URL +
            "/auditoria/modulo/" +
            auditoriaId +
            "?modulo=Solicitud",
        )
        .then((response) => {
          this.isLoading = false;
          this.auditoria = response.data;
          this.auditoria.map((auditoria_item) => {
            auditoria_item.edm = auditoria_item.edm
              ? JSON.parse(auditoria_item.edm)
              : [];
          });
        });
    },

    clickCallback: function (pageNum) {
      this.currentPage = Number(pageNum);
    },
    BuscarSolicitud() {
      this.isLoading = true;
      let vm = this;
      let numeroId = this.numero.split("-");

      axios
        .get(process.env.VUE_APP_URL + "/solicitudes/" + numeroId[1])
        .then(function (response) {
          vm.isLoading = false;
          vm.consulta = response.data;
        })
        .catch(function (error) {
          vm.isLoading = false;
          alert(error.response.data.message);
        });
    },
  },
};
</script>
<style>
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: 0.4s;
  transition: 0.4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: 0.4s;
  transition: 0.4s;
}

input:checked + .slider {
  background-color: #2196f3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196f3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}

.ck-editor__editable {
  min-height: 200px;
}

.pagination {
  margin: 2% 65%;
}

.page-item.active {
  border-radius: 0px;
  position: static !important;
  background-color: #ed6528 !important;
}
/* .modal-content {
  width: auto;
} */

label {
  word-break: break-all;
}
</style>
