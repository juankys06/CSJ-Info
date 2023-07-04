<template>
  <div class="consulta">
    <loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></loading>

    <div :key="actual">
      <div class="row" style="width: 70%; margin-inline: auto">
        <b-col md="12">
          <b-form-group class="mb-3">
            <b-row>
              <b-col md="2">
                <label>Fecha desde:</label>
                <b-form-input v-model="filtro_desde" type="date" />
              </b-col>
              <b-col md="2">
                <label>Fecha hasta</label>
                <b-form-input v-model="filtro_hasta" type="date" />
              </b-col>
              <b-col md="4">
                <label>Estatus</label>
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
              </b-col>
              <b-col md="4">
                <label>Dependencias</label>
                <b-form-select v-model="entidad_id">
                  <b-form-select-option :value="''">Todas</b-form-select-option>
                  <b-form-select-option
                    v-for="entidad in entidades"
                    :key="entidad.codigo"
                    :value="entidad.codigo"
                  >
                    {{ entidad.nombre }}
                  </b-form-select-option>
                </b-form-select>
              </b-col>
            </b-row>
            <b-row>
              <b-col md="2">
                <b-button
                  variant="danger"
                  class="mr-2"
                  @click="reiniciar_filtros()"
                >
                  Reiniciar filtros
                </b-button>
              </b-col>
              <b-col md="10">
                <b-button variant="primary" @click="Buscar()">
                  Buscar
                </b-button>
              </b-col>
            </b-row>
          </b-form-group>
        </b-col>
      </div>

      <table
        id="regTable"
        class="table table-striped table-hover"
        style="margin: auto; width: 70%"
      >
        <thead>
          <tr>
            <th scope="col">Nro de solicitud</th>
            <th scope="col">Fecha</th>
            <th scope="col">Estatus</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(reporte, index) in getItems" :key="reporte.id">
            <td scope="row">
              {{
                new Date(reporte.createDateTime).getFullYear() +
                "-" +
                reporte.id
              }}
            </td>
            <td>
              {{
                new Date(reporte.createDateTime).getDate() +
                "/" +
                (new Date(reporte.createDateTime).getMonth() + 1) +
                "/" +
                new Date(reporte.createDateTime).getFullYear() +
                " " +
                (
                  "0" + (new Date(reporte.createDateTime).getHours() % 12) || 12
                ).slice(-2) +
                ":" +
                ("0" + new Date(reporte.createDateTime).getMinutes()).slice(
                  -2,
                ) +
                " " +
                (new Date(reporte.createDateTime).getHours() >= 12
                  ? "PM"
                  : "AM")
              }}
            </td>
            <td>{{ reporte.status.nombreStatus }}</td>
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
                    @click="detalles(index)"
                    >Detalles</a
                  >
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <paginate
        v-if="pag"
        :page-count="getPageCount"
        :page-range="3"
        :margin-pages="2"
        :click-handler="clickCallback"
        :prev-text="'Anterior'"
        :next-text="'Siguiente'"
        :container-class="'pagination'"
        :page-class="'page-item'"
        :page-link-class="'page-link'"
        :prev-class="'page-link'"
        :next-class="'page-link'"
      >
      </paginate>
      <div style="text-align: center">
        <button class="btn btn-danger col-md-2" @click="download()">
          Descargar PDF
        </button>
        &nbsp;
        <button class="btn btn-success col-md-2" @click="onexport()">
          Descargar EXCEL
        </button>
      </div>
    </div>
    <div
      v-if="reporte_encuesta.length != 0"
      class="encuestas"
      style="margin: auto; width: 50%"
    >
      <div class="form-group">
        <label class="col-md-12 control-label" style="text-align: center"
          ><b>{{
            reporte_encuesta.nombre +
            " - Nro de Muestas:" +
            reporte_encuesta.muestra
          }}</b></label
        >

        <div
          v-for="(preguntas, index) in reporte_encuesta.preguntas"
          :key="index"
        >
          <label class="col-md-12 control-label"
            ><b>{{ "Pregunta " + (index + 1) + ": " + preguntas.nombre }}</b>
            <button
              style="float: right"
              class="btn btn-primary"
              @click="grafica(preguntas)"
            >
              Ver Grafica
            </button>
          </label>

          <div
            v-for="(respuestas, indexRes) in preguntas.respuestas"
            :key="indexRes"
            class="custom-control"
            style="display: inline-block; padding: 0px 40px 0px 50px"
          >
            <label>{{ respuestas.nombre }}</label>
            <br />
            <label>{{ (respuestas.valor * 100).toPrecision(4) + "%" }}</label>
          </div>
        </div>
      </div>
      <div style="text-align: center">
        <button class="btn btn-success col-md-6" @click="onexportEncuestas()">
          Descargar EXCEL
        </button>
      </div>
    </div>
    <div
      id="modalGrafica"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ "Grafica de Preguta: " + nombrePregunta }}
            </h5>
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
            <Chart v-if="x" :height="200" :chart-data="dataGrafica" />
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

    <div
      v-if="bandera"
      id="Detalles"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Detalles de la Solicitud</h5>
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
            <div class="form-group row align-items-center">
              <div
                v-for="(solicitud2, index) in JSON.parse(
                  reporte[pos].contenido,
                )"
                :key="solicitud2.id"
                class="col-md-12 control-label"
              >
                <p v-if="index != 'entidad'">
                  <b>{{ index + ": " }}</b>
                </p>
                <div
                  style="
                    overflow: auto;
                    white-space: pre-line;
                    word-break: break-all;
                  "
                  v-html="procesar_contenido(solicitud2)"
                ></div>
              </div>

              <div
                v-if="
                  reporte[pos].entidadDepartamentoMunicipio &&
                  reporte[pos].entidadDepartamentoMunicipio.length > 0
                "
              >
                <label class="col-md-12 control-label"
                  ><b>Asignaciones:</b>
                </label>
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th scope="col">Dependencia</th>
                      <th scope="col">Departamento</th>
                      <th scope="col">Municipio</th>
                      <th scope="col">Correo(s)</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="entidad in reporte[pos]
                        .entidadDepartamentoMunicipio"
                      :key="'asignacion' + entidad.entidad.codigo"
                    >
                      <td>{{ entidad.entidad.nombre }}</td>
                      <td>
                        {{
                          entidad.departamento
                            ? entidad.departamento.nombre
                            : "-"
                        }}
                      </td>
                      <td>
                        {{ entidad.municipio ? entidad.municipio.nombre : "-" }}
                      </td>
                      <td>{{ entidad.correo_asignacion }}</td>
                      <td>{{ entidad.telefono_asignacion }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <label class="col-md-12 control-label"
                ><b>Status:</b> {{ reporte[pos].status.nombreStatus }}
              </label>

              <div v-if="reporte[pos].archivos.length != 0">
                <label
                  v-for="(archivos, index) in reporte[pos].archivos"
                  :key="archivos.id"
                  class="col-md-12 control-label"
                  ><b>{{ "Archivo Nro" + (index + 1) + ": " }}</b
                  ><a :href="archivos.url">{{ archivos.url }}</a></label
                >
              </div>

              <div v-if="reporte[pos].replicas.length != 0">
                <label
                  v-for="respuestas in reporte[pos].replicas"
                  :key="respuestas.id"
                  class="col-md-12 control-label"
                  ><b>Respuesta</b> <span v-html="respuestas.respuesta"></span
                ></label>
              </div>

              <label
                v-if="reporte[pos].correo_asignacion != null"
                class="col-md-12 control-label"
                ><b>Asignado a:</b> {{ reporte[pos].correo_asignacion }}
              </label>

              <label
                v-if="reporte[pos].telefono_asignacion != null"
                class="col-md-12 control-label"
                ><b>Teléfono(s):</b> {{ reporte[pos].telefono_asignacion }}
              </label>
            </div>
            <hr size="1px" color="gray" />
            <div class="form-group row align-items-center">
              <label class="col-md-12 control-label"
                ><b>Nro de Solicitud:</b>
                {{
                  new Date(reporte[pos].createDateTime).getFullYear() +
                  "-" +
                  reporte[pos].id
                }}
              </label>

              <label
                v-if="
                  reporte[pos].correo_asignacion != null &&
                  $route.name == 'auditoria'
                "
                class="col-md-12 control-label"
                ><b>Asignado a:</b> {{ reporte[pos].correo_asignacion }}
              </label>

              <label
                v-if="
                  reporte[pos].telefono_asignacion != null &&
                  $route.name == 'auditoria'
                "
                class="col-md-12 control-label"
                ><b>Teléfono(s):</b> {{ reporte[pos].telefono_asignacion }}
              </label>

              <label class="col-md-12 control-label"
                ><b>Fecha de Actualizacion:</b>
                {{
                  new Date(reporte[pos].updateDateTime).getDate() +
                  "/" +
                  (new Date(reporte[pos].updateDateTime).getMonth() + 1) +
                  "/" +
                  new Date(reporte[pos].updateDateTime).getFullYear() +
                  " " +
                  (
                    "0" +
                      (new Date(reporte[pos].updateDateTime).getHours() % 12) ||
                    12
                  ).slice(-2) +
                  ":" +
                  (
                    "0" + new Date(reporte[pos].updateDateTime).getMinutes()
                  ).slice(-2) +
                  " " +
                  (new Date(reporte[pos].updateDateTime).getHours() >= 12
                    ? "PM"
                    : "AM")
                }}
              </label>

              <label
                v-if="reporte[pos].usuario != 'null'"
                class="col-md-12 control-label"
                ><b>Actualizado por:</b> {{ reporte[pos].usuario }}
              </label>
              <label
                v-if="reporte[pos].usuario == 'null'"
                class="col-md-12 control-label"
                ><b>Actualizado por:</b>No Actualizado</label
              >
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

    <div style="display: none">
      <table id="content-report" class="table">
        <thead>
          <tr>
            <th colspan="5">Solicitud de información</th>
            <th colspan="3">Total solicitudes: {{ reporte.length }}</th>
          </tr>
          <tr>
            <th>Solicitud</th>
            <th>Correo solicitante</th>
            <th>Correo(s) asignado(s)</th>
            <th>Teléfono(s)</th>
            <th>Fecha</th>
            <th>Estatus</th>
            <th>Asunto</th>
            <th>Dependencia(s) asignada(s)</th>
            <th>Fecha de respuesta</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(solicitud, index) in reporte"
            :key="'tabla' + solicitud.id"
          >
            <td>{{ solicitud.id }}</td>
            <td>{{ solicitud.correo_solicitante }}</td>
            <td>
              <template v-if="solicitud.correo_asignacion">
                <div
                  v-for="correo_asignado in solicitud.correo_asignacion.split(
                    ',',
                  )"
                  :key="'tablaentidad' + index + correo_asignado + solicitud.id"
                >
                  {{ correo_asignado }}<br />
                </div>
              </template>
            </td>
            <td>
              <template v-if="solicitud.telefono_asignacion">
                <div
                  v-for="telefono_asignado in solicitud.telefono_asignacion.split(
                    ',',
                  )"
                  :key="
                    'tablaentidadTelf' +
                    index +
                    telefono_asignado +
                    solicitud.id
                  "
                >
                  {{ telefono_asignado }}<br />
                </div>
              </template>
            </td>
            <td>
              {{ new Date(solicitud.createDateTime).toLocaleString("es-CO") }}
            </td>
            <td>{{ solicitud.status.nombreStatus }}</td>
            <td>{{ solicitud.asunto }}</td>
            <td>
              <template v-if="solicitud.entidadDepartamentoMunicipio.length">
                <div
                  v-for="edm in filtrar_entidad(
                    solicitud.entidadDepartamentoMunicipio,
                  )"
                  :key="'tablaentidad' + edm.entidad.codigo + solicitud.id"
                >
                  - {{ edm.entidad ? edm.entidad.nombre : "" }}<br />
                </div>
              </template>
            </td>
            <td>
              {{
                estatus_response.includes(solicitud.status.id)
                  ? new Date(solicitud.updateDateTime).toLocaleString("es-CO")
                  : "-"
              }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Swal from "sweetalert2";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";
import XLSX from "xlsx";
import axios from "axios";
import Vue from "vue";
import Paginate from "vuejs-paginate";
import Chart from "../components/Chart.vue";
Vue.component("paginate", Paginate);
export default {
  props: ["Id", "tipo"],
  data() {
    return {
      consulta: [],
      entidades: [],
      Excel: [],
      auditoria: [],
      formulario: [],
      seleccion: [],
      estatus: [],
      i: 0,
      pos: 0,
      parPage: 10,
      currentPage: 1,
      pag: true,
      reporte: [],
      bandera: false,
      tipo: 999,
      status_id: "",
      entidad_id: "",
      pregunta_id: "",
      encuesta_id: "",
      formulario_id: "",
      filtro_desde: "",
      filtro_hasta: "",
      estatus_filter: [1, 2, 3, 4, 6],
      estatus_response: [2, 3, 4, 6, 7, 8, 11, 12],
      estatus_banned: [5, 8],
      encuestas: [],
      preguntas: [],
      reporte_pregunta: [],
      reporte_encuesta: [],
      nombrePregunta: "",
      isLoading: false,
      dataGrafica: null,
      seccionPag: "",
      x: false,
      charkey: 0,
      actual: 0,
    };
  },
  components: {
    Loading,
    Chart,
  },
  methods: {
    filtrar_entidad(edms) {
      return edms.filter((edm) => edm.entidad != null);
    },

    procesar_contenido(contenido) {
      return typeof contenido == "string"
        ? contenido.replaceAll("||,||", "\r\n\r\n <hr> \r\n\r\n ")
        : contenido;
    },

    Ordenar() {
      this.isLoading = true;
      this.entidad_id = "";
      this.status_id = "";
      axios
        .get(process.env.VUE_APP_URL + "/solicitudes?formulario=" + this.Id)
        .then((response) => {
          this.isLoading = false;
          this.reporte = response.data;
          this.reporte.map((solicitud) => {
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
          });
        })
        .catch((error) => {
          this.isLoading = false;
          Swal.fire({
            title: "Error!",
            text: error.response.data.message,
            width: 350,
            padding: "0.5em",
            icon: "error",
          });
        });
    },

    grafica(pregunta) {
      this.nombrePregunta = pregunta.nombre;
      var labels = [],
        data = [],
        colores = [
          "#FFF333",
          "#334FFF",
          "#F50505",
          "#38F505",
          "#05F5DF",
          "#0F0F10",
          "#0F0F10",
        ],
        color = [];
      for (var i = 0; i < pregunta.respuestas.length; i++) {
        labels[i] = pregunta.respuestas[i].nombre;
        data[i] = (pregunta.respuestas[i].valor * 100).toPrecision(4);
        color[i] = colores[i];
      }
      this.dataGrafica = {
        labels: labels,
        datasets: [
          {
            backgroundColor: color,
            data: data,
          },
        ],
      };
      this.x = true;
      jQuery("#modalGrafica").modal("show");
    },
    download() {
      const doc = new jsPDF("l", "mm", "a3");
      autoTable(doc, { html: "#content-report" });
      doc.save("Solicitud de Infomación.pdf");
    },
    exportPDF() {
      //var vm = this

      var doc = new jsPDF("p", "pt");
      var cont = 20;
      //doc.text('Reportes', 10, 40);
      for (var i = 0; i < this.reporte.length; i++) {
        doc.text("Numero de Solicitud: " + this.reporte[i].id, 20, cont);
        doc.text(
          " Asunto: Asunto: Asunto: Asunto: Asunto: Asunto: Asunto:  Asunto: Asunto: Asunto: Asunto: Asunto: Asunto: " +
            this.reporte[i].asunto,
          20,
          cont + 20,
          { width: 70 },
        );
        cont = cont + 40;
      }

      doc.save("Reporte.pdf");
    },
    detalles(index) {
      this.pos = index + this.seccionPag;
      this.bandera = true;
    },

    clickCallback: function (pageNum) {
      this.currentPage = Number(pageNum);
    },
    ReportesFormulario() {
      let vm = this;
      this.fecha.desde = this.fecha.desde + " 00:00";
      this.fecha.hasta = this.fecha.hasta + " 23:59";
      let formData = new FormData();
      vm.isLoading = true;
      formData.append("desde", this.fecha.desde);
      formData.append("hasta", this.fecha.hasta);
      formData.append("formulario", this.Id);
      axios
        .post(process.env.VUE_APP_URL + "/solicitudes/fecha", formData)
        .then(function (response) {
          vm.isLoading = false;
          if (response.data.length > 0) {
            vm.reporte = response.data;
            vm.reporte.map((solicitud) => {
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
            });
            vm.actual++;
          } else {
            Swal.fire({
              title: "No Existe Reporte en esta fecha",
              text: "Intente de nuevo con otra fecha",
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
          }
        })
        .catch(function (error) {
          vm.isLoading = false;
          Swal.fire({
            title: "Error!",
            text: error.response.data.message,
            width: 350,
            padding: "0.5em",
            icon: "error",
          });
        });
    },

    reiniciar_filtros() {
      this.estatus_filter = [1, 2, 3, 4, 6];
      let date = new Date();
      this.filtro_desde = new Date(date.getFullYear(), date.getMonth(), 1)
        .toISOString()
        .substring(0, 10);
      this.filtro_hasta = new Date(date.getFullYear(), date.getMonth() + 1, 0)
        .toISOString()
        .substring(0, 10);
      this.entidad_id = "";
      this.Buscar();
    },

    Buscar() {
      this.isLoading = true;
      this.bandera = false;
      let desde = "";
      let hasta = "";
      if (this.filtro_desde) {
        desde = this.filtro_desde + " 00:00";
      }

      if (this.filtro_hasta) {
        hasta = this.filtro_hasta + " 23:59";
      }

      axios
        .get(
          process.env.VUE_APP_URL +
            "/reporte/solicitudes?formulario=" +
            this.Id +
            "&entidad=" +
            this.entidad_id +
            "&status=" +
            this.estatus_filter.join(",") +
            "&desde=" +
            desde +
            "&hasta=" +
            hasta,
        )
        .then((e) => {
          this.reporte = e.data;
          if (this.reporte.length > 0) {
            this.reporte.map((solicitud) => {
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
            });
            this.isLoading = false;
          } else {
            this.isLoading = false;
            this.$bvModal.msgBoxOk("No hay solicitudes", {
              title: "ERROR",
              size: "sm",
              buttonSize: "sm",
              okVariant: "danger",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            });
          }
        })
        .catch((error) => {
          console.log(error);
          this.isLoading = false;
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

    Reportes(tipoReporte) {
      let vm = this;
      this.isLoading = true;
      if (tipoReporte == 1) {
        axios
          .get(
            process.env.VUE_APP_URL +
              "/solicitudes?formulario=" +
              vm.Id +
              "&status=" +
              this.status_id,
          )
          .then(function (response) {
            vm.isLoading = false;
            if (response.data.length > 0) {
              vm.reporte = response.data;
              vm.reporte.map((solicitud) => {
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
              });
            } else {
              Swal.fire({
                title: "Lo siento!",
                text: "No Existe Reporte con este Estado",
                width: 350,
                padding: "0.5em",
                icon: "error",
              });
            }
          })
          .catch(function (error) {
            vm.isLoading = false;
            Swal.fire({
              title: "Error!",
              text: error.response.data.message,
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
          });
      }
      if (tipoReporte == 2) {
        axios
          .get(
            process.env.VUE_APP_URL +
              "/entidad/solicitudes?formulario=" +
              vm.Id +
              "&entidad=" +
              this.entidad_id,
          )
          .then(function (response) {
            vm.isLoading = false;
            if (true) {
            }

            if (response.data.length > 0) {
              vm.reporte = response.data;
              vm.reporte.map((solicitud) => {
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
              });
            } else {
              Swal.fire({
                title: "Lo siento!",
                text: "No Existe Reporte con esta dependencia",
                width: 350,
                padding: "0.5em",
                icon: "error",
              });
            }
          })
          .catch(function (error) {
            vm.isLoading = false;
            Swal.fire({
              title: "Error!",
              text: error.response.data.message,
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
          });
      }
      if (this.tipo == 2) {
        this.fecha.desde = this.fecha.desde + " 00:00";
        this.fecha.hasta = this.fecha.hasta + " 00:00";
        let formData = new FormData();

        formData.append("desde", this.fecha.desde);
        formData.append("hasta", this.fecha.hasta);

        axios
          .post(process.env.VUE_APP_URL + "/solicitudes/fecha", formData)
          .then(function (response) {
            vm.isLoading = false;
            vm.reporte = response.data;
            vm.reporte.map((solicitud) => {
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
            });

            console.log(vm.reporte);
          })
          .catch(function (error) {
            vm.isLoading = false;
            Swal.fire({
              title: "Error!",
              text: error.response.data.message,
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
          });
      }

      //if (this.tipo==3) {}
      if (this.tipo == 3) {
        axios
          .get(
            process.env.VUE_APP_URL + "/reportes?encuesta=" + this.encuesta_id,
          )
          .then(function (response) {
            vm.isLoading = false;
            vm.reporte_encuesta = response.data;
          })
          .catch(function (error) {
            vm.isLoading = false;
            alert(error.response.data.message);
          });
      }

      if (this.tipo == 4) {
        axios
          .get(
            process.env.VUE_APP_URL +
              "/solicitudes?formulario=" +
              this.formulario_id,
          )
          .then(function (response) {
            vm.isLoading = false;
            vm.reporte = response.data;
            vm.reporte.map((solicitud) => {
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
            });
          })
          .catch(function (error) {
            vm.isLoading = false;
            Swal.fire({
              title: "Error!",
              text: error.response.data.message,
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
          });
      }
    },
    onexport() {
      let me = this;
      let fechas = "";
      let json1 = [],
        json2 = [],
        json3 = [];

      for (var i = 0; i < me.reporte.length; i++) {
        fechas =
          new Date(me.reporte[i].createDateTime).getDate() +
          "/" +
          (new Date(me.reporte[i].createDateTime).getMonth() + 1) +
          "/" +
          new Date(me.reporte[i].createDateTime).getFullYear() +
          " " +
          new Date(me.reporte[i].createDateTime).getHours() +
          ":" +
          new Date(me.reporte[i].createDateTime).getMinutes();

        json1 = {
          "Numero de solicitud": me.reporte[i].id,
          "Correo del Solicitante": me.reporte[i].correo_solicitante,
          "Correo de Asignacion": me.reporte[i].correo_asignacion,
          "Teléfono(s)": me.reporte[i].telefono_asignacion,
          "Fecha de Creación": fechas,
          Estatus: me.reporte[i].status.nombreStatus,
          Asunto: me.reporte[i].asunto,
        };
        if (
          me.reporte[i].entidadDepartamentoMunicipio &&
          me.reporte[i].entidadDepartamentoMunicipio.length > 0
        ) {
          let entidades = "|";
          me.reporte[i].entidadDepartamentoMunicipio.forEach((entidad) => {
            if (entidad.entidad && entidad.entidad.nombre) {
              entidades = entidades + entidad.entidad.nombre + "|";
            }
          });

          json2 = {
            Entidad: entidades,
          };
        }

        let fecha_update =
          new Date(me.reporte[i].updateDateTime).getDate() +
          "/" +
          (new Date(me.reporte[i].updateDateTime).getMonth() + 1) +
          "/" +
          new Date(me.reporte[i].updateDateTime).getFullYear() +
          " " +
          new Date(me.reporte[i].updateDateTime).getHours() +
          ":" +
          new Date(me.reporte[i].updateDateTime).getMinutes();

        for (var j = 0; j < me.reporte[i].replicas.length; j++) {
          json3 = {
            Respuesta: jQuery(me.reporte[i].replicas[j].respuesta).text(),
            "Asunto de Respuesta": me.reporte[i].replicas[j].asunto,
          };
        }

        let obj_unidos = Object.assign(json1, json2, json3, {
          "Fecha de respuesta": this.estatus_response.includes(
            me.reporte[i].status.id,
          )
            ? fecha_update
            : "-",
        });
        me.Excel[i] = obj_unidos;

        json3 = [];
      }

      var Excel_Reportes = XLSX.utils.json_to_sheet(this.Excel);

      var wb = XLSX.utils.book_new();

      XLSX.utils.book_append_sheet(wb, Excel_Reportes, "Reportes");
      XLSX.writeFile(wb, "Reportes.xlsx");
    },
    onexportEncuestas() {
      let me = this;
      let cont = 0,
        fecha = "";
      let json1 = [],
        json2 = [],
        json3 = [],
        json4 = [];

      for (
        var i = 0;
        i < Object.keys(me.reporte_encuesta.preguntas).length;
        i++
      ) {
        json1 = { Pregunta: me.reporte_encuesta.preguntas[i].nombre };
        for (
          var j = 0;
          j < Object.keys(me.reporte_encuesta.preguntas[i].respuestas).length;
          j++
        ) {
          json2 = {
            Respuestas: me.reporte_encuesta.preguntas[i].respuestas[j].nombre,
            Porcentaje: me.reporte_encuesta.preguntas[i].respuestas[j].valor,
          };
          let obj_unidos = Object.assign(json1, json2);
          me.Excel[cont] = obj_unidos;
          cont++;
          json1 = [];
          json2 = [];
        }
      }
      var Excel_Reportes = XLSX.utils.json_to_sheet(this.Excel);
      var wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, Excel_Reportes, "Reporte General");
      this.Excel = [];
      cont = 0;
      for (var i = 0; i < Object.keys(me.seleccion).length; i++) {
        for (
          var j = 0;
          j < Object.keys(me.seleccion[i].encuesta.preguntas).length;
          j++
        ) {
          if (
            me.seleccion[i].preguntaId ==
            me.seleccion[i].encuesta.preguntas[j].id
          ) {
            json1 = { Pregunta: me.seleccion[i].encuesta.preguntas[j].texto };
            for (
              var k = 0;
              k <
              Object.keys(me.seleccion[i].encuesta.preguntas[j].respuestas)
                .length;
              k++
            ) {
              if (
                me.seleccion[i].respuestaId ==
                me.seleccion[i].encuesta.preguntas[j].respuestas[k].id
              ) {
                json2 = {
                  Respuesta:
                    me.seleccion[i].encuesta.preguntas[j].respuestas[k].texto,
                };
              }
            }
          }
        }
        fecha =
          new Date(me.seleccion[i].create_date_time).getDate() +
          "/" +
          (new Date(me.seleccion[i].create_date_time).getMonth() + 1) +
          "/" +
          new Date(me.seleccion[i].create_date_time).getFullYear() +
          " " +
          (
            "0" +
              (new Date(me.seleccion[i].create_date_time).getHours() % 12) || 12
          ).slice(-2) +
          ":" +
          ("0" + new Date(me.seleccion[i].create_date_time).getMinutes()).slice(
            -2,
          ) +
          " " +
          (new Date(me.seleccion[i].create_date_time).getHours() >= 12
            ? "PM"
            : "AM");

        json3 = { Usuario: me.seleccion[i].userAgent };
        json4 = { Fecha: fecha };
        let obj_unidos = Object.assign(json1, json2, json3, json4);
        me.Excel[cont] = obj_unidos;
        cont++;
        json1 = [];
        json2 = [];
        json3 = [];
      }
      var Excel_Reportes = XLSX.utils.json_to_sheet(this.Excel);
      XLSX.utils.book_append_sheet(wb, Excel_Reportes, "Reporte detallado");
      XLSX.writeFile(wb, me.reporte_encuesta.nombre + ".xlsx");
    },
  },
  computed: {
    getItems: function () {
      let current = this.currentPage * this.parPage;
      let start = current - this.parPage;
      this.seccionPag = start;
      return this.reporte.slice(start, current);
    },
    getPageCount: function () {
      return Math.ceil(this.reporte.length / this.parPage);
    },
  },
  mounted() {
    if (!this.tipo || !this.Id) {
      this.$router.push({ name: "formularioSeleccion" });
    }
    let vm = this;

    vm.isLoading = true;
    axios.get(process.env.VUE_APP_URL + "/entidadN").then(function (response) {
      vm.entidades = response.data;
    });

    axios.get(process.env.VUE_APP_URL + "/status").then((response) => {
      this.estatus = response.data.filter(
        (estatus) => !this.estatus_banned.includes(estatus.id),
      );
    });

    axios.get(process.env.VUE_APP_URL + "/encuestas").then(function (response) {
      vm.encuestas = response.data;
    });

    axios
      .get(process.env.VUE_APP_URL + "/formularios")
      .then(function (response) {
        vm.formulario = response.data;
      });

    if (vm.tipo == "formulario") {
      this.reiniciar_filtros();
      this.Buscar();
    }

    if (vm.tipo == "encuesta") {
      axios
        .get(process.env.VUE_APP_URL + "/reportes?encuesta=" + vm.Id)
        .then(function (response) {
          vm.isLoading = false;
          vm.reporte_encuesta = response.data;
        })
        .catch(function (error) {
          vm.isLoading = false;
          alert(error.response.data.message);
        });

      axios
        .get(process.env.VUE_APP_URL + "/seleccion?encuesta_id=" + vm.Id)
        .then(function (response) {
          vm.seleccion = response.data;
        })
        .catch(function (error) {
          alert(error.response.data.message);
        });
    }
  },
};
</script>
<style>
.pagination {
  margin: 2% 65%;
}

.page-item.active {
  border-radius: 0px;
  position: static !important;
  background-color: #ed6528 !important;
}

#pie-chart {
  height: 400px !important;
}
</style>
