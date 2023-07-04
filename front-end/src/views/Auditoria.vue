<template>
  <div class="auditorias">
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
          <b-row class="mt-2">
            <div class="ml-auto mr-3">
              <b-button
                variant="danger"
                class="mr-2"
                @click="reiniciar_filtros()"
              >
                Reiniciar filtros
              </b-button>
              <b-button
                style="background-color: #19325b; color: white"
                @click="$refs.auditoria_table.refresh()"
              >
                Buscar
              </b-button>
            </div>
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
        :current-page="currentPage"
        :sort-by="'id'"
        sort-desc
        show-empty
        @row-clicked="detalles"
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
      <div class="d-flex justify-content-end align-items-center ml-auto">
        <label class="mr-2">por pagina:</label>
        <b-form-select v-model="perPage" style="width: min-content">
          <b-form-select-option :value="10">10</b-form-select-option>
          <b-form-select-option :value="20">20</b-form-select-option>
          <b-form-select-option :value="30">30</b-form-select-option>
          <b-form-select-option :value="50">50</b-form-select-option>
          <b-form-select-option :value="100">100</b-form-select-option>
        </b-form-select>
        <b-pagination
          v-model="currentPage"
          :per-page="perPage"
          :total-rows="totalRows"
          :prev-text="'Anterior'"
          :next-text="'Siguiente'"
          :class="'pagination m-0 mb-0'"
          align="right"
        ></b-pagination>
      </div>
      <b-col md="12">
        <div style="text-align: center" class="mt-4">
          <!--<button class="btn btn-danger col-md-2" @click="download()">Descargar PDF</button> &nbsp; -->
          <button
            class="btn btn-success col-md-2"
            :disabled="exportLoading"
            @click="onexport()"
          >
            <template v-if="exportLoading">
              <b-spinner small variant="primary" label="Spinning"></b-spinner>
            </template>
            <template v-else> Descargar EXCEL </template>
          </button>
        </div>
      </b-col>
    </div>

    <b-modal
      id="tablaList"
      :key="'tablaList'"
      :title="body ? 'Listado de la Auditoria' : 'Detalles de la Auditoria'"
      scrollable
      size="lg"
      :ok-title="'atras'"
      :cancel-title="'cerrar'"
      @ok.prevent="body = true"
      @cancel="body = true"
      @close="body = true"
      @show="body = true"
    >
      <template v-if="body">
        <b-table
          ref="auditoria_table_interna"
          striped
          hover
          :items="fetch_auditoriasPerRow"
          :fields="camposPerRow"
          :busy.sync="loadingPerRow"
          :per-page="perPagePerRow"
          :current-page="currentPagePerRow"
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
          <template v-slot:cell(acciones)="{ item, index }">
            <div class="btn-group">
              <button
                type="button"
                class="btn btn-default dropdown-toggle"
                data-toggle="dropdown"
              >
                Acciones
              </button>
              <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <a class="dropdown-item" @click="detallesEsp(item.id, index)"
                  >Detalles</a
                >
              </div>
            </div>
          </template>
        </b-table>
        <div class="d-flex justify-content-end align-items-center ml-auto">
          <label class="mr-2">por pagina:</label>
          <b-form-select v-model="perPagePerRow" style="width: min-content">
            <b-form-select-option :value="10">10</b-form-select-option>
            <b-form-select-option :value="20">20</b-form-select-option>
            <b-form-select-option :value="30">30</b-form-select-option>
            <b-form-select-option :value="50">50</b-form-select-option>
            <b-form-select-option :value="100">100</b-form-select-option>
          </b-form-select>
          <b-pagination
            v-model="currentPagePerRow"
            :per-page="perPagePerRow"
            :total-rows="totalRowsPerRow"
            :prev-text="'Anterior'"
            :next-text="'Siguiente'"
            :class="'pagination m-0 mb-0'"
            align="right"
          ></b-pagination>
        </div>
      </template>

      <template v-if="!body">
        <div
          v-if="solicitud.length != 0"
          class="form-group row align-items-center"
        >
          <!--######SOLICITUDES######-->
          <div v-if="solicitud.id_solicitud && solicitud.revtype == 0">
            <label class="col-md-12 control-label"
              ><b>Nro de Solicitud:</b>
              {{
                new Date(solicitud.create_date_time).getFullYear() +
                "-" +
                solicitud.id_solicitud
              }}
            </label>

            <label
              v-for="(solicitud2, index) in JSON.parse(
                solicitud.contenido_solicitud,
              )"
              :key="'solicitud2' + solicitud2.Asunto"
              class="col-md-12 control-label"
              ><p v-if="index != 'entidad'">
                <b>{{ index + ": " }}</b
                >{{ solicitud2 }}
              </p></label
            >
            {{ solicitud2 }}
            <div
              v-for="entidad in entidadDepartamentoMunicipio"
              :key="'entidad' + entidad.id_edm"
            >
              <div v-if="entidad.id_edm == solicitud.edm_id">
                <label class="col-md-12 control-label">
                  <b>Dependencia:</b> {{ entidad.entidad.nombre }}
                </label>
                <label class="col-md-12 control-label">
                  <b>Tipo de Dependencia:</b> {{ entidad.entidad.tipo }}
                </label>
                <label
                  v-if="entidad.entidad.tipo == 'Departamento'"
                  class="col-md-12 control-label"
                >
                  <b>Departamento:</b> {{ entidad.departamento.nombre }}
                </label>
                <label
                  v-if="entidad.entidad.tipo == 'Municipio'"
                  class="col-md-12 control-label"
                >
                  <b>Municipio:</b> {{ entidad.municipio.nombre }}
                </label>
              </div>
            </div>

            <div v-for="estatus in estatus" :key="'estatus' + estatus.id">
              <label
                v-if="estatus.id == solicitud.status_id"
                class="col-md-12 control-label"
                ><b>Status:</b> {{ estatus.nombreStatus }}
              </label>
            </div>

            <label
              v-if="
                solicitud.correo_asignacion != null &&
                $route.name == 'auditoria'
              "
              class="col-md-12 control-label"
              ><b>Asignado a:</b> {{ solicitud.correo_asignacion }}
            </label>

            <label
              v-if="
                solicitud.telefono_asignacion != null &&
                $route.name == 'auditoria'
              "
              class="col-md-12 control-label"
              ><b>Teléfonos:</b> {{ solicitud.telefono_asignacion }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Solicitud
            </label>
          </div>

          <div v-if="solicitud.id_solicitud && solicitud.revtype != 0">
            <h4>Antes</h4>
            <label class="col-md-12 control-label"
              ><b>Nro de Solicitud:</b>
              {{
                new Date(solicitud.create_date_time).getFullYear() +
                "-" +
                auditoriaId[indexId].id_solicitud
              }}
            </label>

            <label
              v-for="(solicitud2, index) in JSON.parse(
                auditoriaId[indexId].contenido_solicitud,
              )"
              :key="'solicitud22' + solicitud2.Asunto"
              class="col-md-12 control-label"
              ><p v-if="index != 'entidad'">
                <b>{{ index + ": " }}</b
                >{{ solicitud2 }}
              </p></label
            >

            <div
              v-if="
                JSON.parse(auditoriaId[indexId].edm) &&
                JSON.parse(auditoriaId[indexId].edm).length > 0
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
                    <th scope="col">Teléfono(s)</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="entidad in JSON.parse(auditoriaId[indexId].edm)"
                    :key="'asignacion' + entidad.entidad.codigo"
                  >
                    <td>{{ entidad.entidad.nombre }}</td>
                    <td>
                      {{
                        entidad.departamento ? entidad.departamento.nombre : "-"
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

            <div v-for="estatus in estatus" :key="'estatus2' + estatus.id">
              <label
                v-if="estatus.id == auditoriaId[indexId].status_id"
                class="col-md-12 control-label"
                ><b>Status:</b> {{ estatus.nombreStatus }}
              </label>
            </div>

            <div>
              <label class="col-md-12 control-label"
                ><b>Padre:</b> {{ auditoriaId[indexId].parent_id }}
              </label>
            </div>

            <label
              v-if="
                auditoriaId[indexId].correo_asignacion != null &&
                $route.name == 'auditoria'
              "
              class="col-md-12 control-label"
              ><b>Asignado a:</b>
              {{ auditoriaId[indexId].correo_asignacion }}
            </label>

            <label
              v-if="
                auditoriaId[indexId].telefono_asignacion != null &&
                $route.name == 'auditoria'
              "
              class="col-md-12 control-label"
              ><b>Teléfonos:</b>
              {{ auditoriaId[indexId].telefono_asignacion }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Solicitud
            </label>
            <h4>Despues</h4>
            <label class="col-md-12 control-label"
              ><b>Nro de Solicitud:</b>
              {{
                new Date(solicitud.create_date_time).getFullYear() +
                "-" +
                solicitud.id_solicitud
              }}
            </label>

            <label
              v-for="(solicitud2, index) in JSON.parse(
                solicitud.contenido_solicitud,
              )"
              :key="'solicitud222' + solicitud2.Asunto"
              class="col-md-12 control-label"
              ><p v-if="index != 'entidad'">
                <b>{{ index + ": " }}</b
                >{{ solicitud2 }}
              </p></label
            >

            <div
              v-if="
                JSON.parse(solicitud.edm) &&
                JSON.parse(solicitud.edm).length > 0
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
                    <th scope="col">Teléfono(s)</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="entidad in JSON.parse(solicitud.edm)"
                    :key="'asignacion' + entidad.entidad.codigo"
                  >
                    <td>{{ entidad.entidad.nombre }}</td>
                    <td>
                      {{
                        entidad.departamento ? entidad.departamento.nombre : "-"
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

            <div v-for="estatus in estatus" :key="'estatus4' + estatus.id">
              <label
                v-if="estatus.id == solicitud.status_id"
                class="col-md-12 control-label"
                ><b>Status:</b> {{ estatus.nombreStatus }}
              </label>
            </div>

            <div>
              <label class="col-md-12 control-label"
                ><b>Padre:</b> {{ solicitud.parent_id }}
              </label>
            </div>

            <label
              v-if="
                solicitud.correo_asignacion != null &&
                $route.name == 'auditoria'
              "
              class="col-md-12 control-label"
              ><b>Remitido por competencia a:</b>
              {{ solicitud.correo_asignacion }}
            </label>

            <label
              v-if="
                solicitud.telefono_asignacion != null &&
                $route.name == 'auditoria'
              "
              class="col-md-12 control-label"
              ><b>Teléfono(s):</b>
              {{ solicitud.telefono_asignacion }}
            </label>

            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Solicitud
            </label>
          </div>

          <!--######ENTIDAD######-->
          <div v-if="solicitud.codigo && solicitud.revtype == 0">
            <label class="col-md-12 control-label"
              ><b>Codigo:</b>{{ solicitud.codigo }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ solicitud.nombre }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Dependencia
            </label>
          </div>

          <div v-if="solicitud.codigo && solicitud.revtype != 0">
            <h4>Antes</h4>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ auditoriaId[indexId].nombre }}
            </label>
            <h4>Despues</h4>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ solicitud.nombre }}
            </label>
          </div>

          <!--######ROLES######-->
          <div v-if="solicitud.id_roles && solicitud.revtype == 0">
            <label class="col-md-12 control-label"
              ><b>Identificador:</b>{{ solicitud.id_roles }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ solicitud.descripcion_roles }}
            </label>
            <label class="col-md-12 control-label"><b>Modulo:</b>Roles </label>
          </div>

          <div v-if="solicitud.id_roles && solicitud.revtype != 0">
            <h4>Antes</h4>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ auditoriaId[indexId].descripcion_roles }}
            </label>
            <label class="col-md-12 control-label"><b>Modulo:</b>Roles </label>
            <h4>Despues</h4>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ solicitud.descripcion_roles }}
            </label>
            <label class="col-md-12 control-label"><b>Modulo:</b>Roles </label>
          </div>

          <!--######FORMULARIOS######-->
          <div v-if="solicitud.id_formulario && solicitud.revtype == 0">
            <label class="col-md-12 control-label"
              ><b>Identificador:</b>{{ solicitud.id_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ solicitud.descripcion_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Campos del Formulario:</b>
            </label>
            <label
              v-for="(solicitud2, index) in JSON.parse(
                solicitud.contenido_formulario,
              ).schema.fields"
              :key="'solicitud333' + solicitud2.id + index"
              class="col-md-12 control-label"
              ><p v-if="index != 'entidad'">
                <b>{{ "Campo" + index + ": " }}</b
                >{{ solicitud2.label }}
              </p></label
            >
            <label class="col-md-12 control-label"
              ><b>Url:</b>{{ url + "/formulario/" + solicitud.url_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Email:</b>{{ solicitud.email }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Formulario
            </label>
          </div>

          <div v-if="solicitud.id_formulario && solicitud.revtype != 0">
            <h4>Antes</h4>
            <label class="col-md-12 control-label"
              ><b>Identificador:</b>{{ auditoriaId[indexId].id_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b
              >{{ auditoriaId[indexId].descripcion_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Campos del Formulario:</b>
            </label>
            <label
              v-for="(solicitud2, index) in JSON.parse(
                auditoriaId[indexId].contenido_formulario,
              ).schema.fields"
              :key="'solicitud555' + solicitud2.id + index"
              class="col-md-12 control-label"
              ><p v-if="index != 'entidad'">
                <b>{{ "Campo" + index + ": " }}</b
                >{{ solicitud2.label }}
              </p></label
            >
            <label class="col-md-12 control-label"
              ><b>Url:</b
              >{{ url + "/formulario/" + auditoriaId[indexId].url_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Email:</b>{{ auditoriaId[indexId].email }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Formulario
            </label>
            <h4>Despues</h4>
            <label class="col-md-12 control-label"
              ><b>Identificador:</b>{{ solicitud.id_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Descripcion:</b>{{ solicitud.descripcion_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Campos del Formulario:</b>
            </label>
            <label
              v-for="(solicitud2, index) in JSON.parse(
                solicitud.contenido_formulario,
              ).schema.fields"
              :key="'solicitud666' + solicitud2 + index"
              class="col-md-12 control-label"
              ><p v-if="index != 'entidad'">
                <b>{{ "Campo" + index + ": " }}</b
                >{{ solicitud2.label }}
              </p></label
            >
            <label class="col-md-12 control-label"
              ><b>Url:</b>{{ url + "/formulario/" + solicitud.url_formulario }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Email:</b>{{ solicitud.email }}
            </label>
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Formulario
            </label>
          </div>

          <!--######PERMISOS######-->
          <div v-if="solicitud.id_permiso">
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Permiso
            </label>
          </div>

          <!--######ENCUESTAS######-->
          <div v-if="solicitud.id_encuesta">
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Encuesta
            </label>
          </div>

          <!--######USUARIOS######-->
          <div v-if="solicitud.id_user">
            <label class="col-md-12 control-label"
              ><b>Modulo:</b>Usuarios
            </label>
          </div>

          <hr style="width: -webkit-fill-available" />

          <label class="col-md-12 control-label"
            ><b>Fecha:</b
            >{{
              new Date(solicitud.updateDateTime).getDate() +
              "/" +
              (new Date(solicitud.updateDateTime).getMonth() + 1) +
              "/" +
              new Date(solicitud.updateDateTime).getFullYear() +
              " " +
              (
                "0" + (new Date(solicitud.updateDateTime).getHours() % 12) || 12
              ).slice(-2) +
              ":" +
              ("0" + new Date(solicitud.updateDateTime).getMinutes()).slice(
                -2,
              ) +
              " " +
              (new Date(solicitud.updateDateTime).getHours() >= 12
                ? "PM"
                : "AM")
            }}
          </label>
          <label v-if="solicitud.revtype == 0" class="col-md-12 control-label"
            ><b>Evento:</b>Agregar</label
          >
          <label v-if="solicitud.revtype == 1" class="col-md-12 control-label"
            ><b>Evento:</b>Actualizar</label
          >
          <label v-if="solicitud.revtype == 2" class="col-md-12 control-label"
            ><b>Evento:</b>Eliminar</label
          >

          <label
            v-if="solicitud.usuarioModified != 'null' && solicitud.revtype != 0"
            class="col-md-12 control-label"
            ><b>Actualizado por:</b> {{ solicitud.usuarioModified }}
          </label>
          <label
            v-if="solicitud.usuarioModified == 'null'"
            class="col-md-12 control-label"
            ><b>Actualizado por:</b>No Actualizado</label
          >
        </div>
      </template>
    </b-modal>
  </div>
</template>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Swal from "sweetalert2";
import axios from "axios";
import jsPDF from "jspdf";
import XLSX from "xlsx";
import Vue from "vue";
import Paginate from "vuejs-paginate";
Vue.component("paginate", Paginate);
export default {
  data() {
    return {
      auditorias: {
        content: [],
      },
      perPage: 10,
      totalRows: 0,
      currentPage: 1,
      perPagePerRow: 30,
      totalRowsPerRow: 0,
      currentPagePerRow: 1,
      loading: false,
      loadingPerRow: false,
      exportLoading: false,
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
      camposPerRow: [
        {
          key: "id",
          label: "#",
          sortable: false,
        },
        {
          key: "updateDateTime",
          label: "Fecha",
          sortable: false,
        },
        {
          key: "modulo",
          label: "Modulo",
        },
        {
          key: "revtype",
          label: "Evento",
          sortable: false,
        },
        {
          key: "usuarioModified",
          label: "Usuario",
          sortable: false,
        },
        {
          key: "acciones",
          label: "Acciones",
        },
      ],
      auditoriaId: [],
      entidad: [],
      solicitud: [],
      estatus: [],
      Excel: [],
      i: 0,
      parPage: 20,
      pag: true,
      body: true,
      modulo: "",
      isLoading: false,
      numero: "",
      indexId: "",
      entidadDepartamentoMunicipio: [],
      correoUsuario: "",
      url: window.location.origin,
      regex: /^[-\w.%+]{1,64}@(?:[A-Z0-9-]{1,63}\.){1,125}[A-Z]{2,63}$/i,
    };
  },

  components: {
    Loading,
  },
  methods: {
    fetch_auditorias(ctx, callback) {
      this.loading = true;
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

    fetch_auditoriasPerRow(ctx, callback) {
      this.loadingPerRow = true;
      axios
        .get(process.env.VUE_APP_URL + "/auditoria", {
          params: {
            page: ctx.currentPage - 1,
            size: ctx.perPage,
            sort: "id",
            direction: "desc",
            filterField: this.search_field,
            filterValue: this.search_Value,
          },
        })
        .then((result) => {
          this.totalRowsPerRow = result.data.totalElements;
          this.currentPagePerRow = ctx.currentPage;
          this.perPagePerRow = ctx.perPage;
          this.auditoriaId = result.data.content;
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

    // Codigo anterior

    reverse() {
      this.auditoria = this.auditoria.slice().reverse();
    },
    download() {
      const doc = new jsPDF("p");
      /** WITHOUT CSS */
      const contentHtml = this.$refs.content.innerHTML;
      /*var img = new Image();
      img.crossOrigin = ""; 
      img.src = 'assets/logo1.png'
      doc.addImage(img, "png", 15, 40, 80, 80);*/
      doc.setFontStyle("bold");
      doc.text("AUDITORIA SOLICITUD DE INFORMACIÓN", 60, 20);

      doc.setLineWidth(2);
      doc.line(0, 30, 210, 30);

      doc.fromHTML(contentHtml, 15, 15, {
        width: 170,
      });
      doc.save("Solicitud de Infomación.pdf");
    },
    onexport() {
      this.exportLoading = true;
      axios
        .post(process.env.VUE_APP_URL + "/auditoria/excel", null, {
          responseType: "blob",
          params: {
            updateDateTimeStart: this.fecha_desde,
            updateDateTimeEnd: this.fecha_hasta,
            usuarioModified: this.search_usuarioModified,
            idSolicitud: this.search_idSolicitud,
          },
        })
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", "auditorias.xlsx");
          document.body.appendChild(link);
          link.click();
        })
        .catch((error) => {
          console.error(error);
        })
        .finally(() => {
          this.exportLoading = false;
        });
    },
    detalles(aud, index) {
      this.isLoading = true;

      if (aud.codigo) {
        this.search_field = "codigo";
        this.search_Value = aud.codigo;
      }
      if (aud.id_formulario) {
        this.search_field = "id_formulario";
        this.search_Value = aud.id_formulario;
      }
      if (aud.id_roles) {
        this.search_field = "id_roles";
        this.search_Value = aud.id_roles;
      }
      if (aud.id_user) {
        this.search_field = "id_user";
        this.search_Value = aud.id_user;
      }
      if (aud.id_encuesta) {
        this.search_field = "id_encuesta";
        this.search_Value = aud.id_encuesta;
      }
      if (aud.id_solicitud) {
        this.search_field = "id_solicitud";
        this.search_Value = aud.id_solicitud;
      }

      this.$bvModal.show("tablaList");
    },
    detallesEsp(rev, index) {
      //this.isLoading = true;
      this.indexId = index + 1;

      let vm = this;
      axios
        .get(process.env.VUE_APP_URL + "/auditoria/" + rev)
        .then(function (response) {
          vm.isLoading = false;
          vm.solicitud = response.data;
          vm.body = false;
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
  },
  mounted() {
    let vm = this;
    axios.get(process.env.VUE_APP_URL + "/entidad").then(function (response) {
      vm.entidad = response.data;
    });
    axios.get(process.env.VUE_APP_URL + "/status").then(function (response) {
      vm.estatus = response.data;
    });
    axios
      .get(process.env.VUE_APP_URL + "/entidadDepartamentoMunicipio")
      .then(function (response) {
        vm.entidadDepartamentoMunicipio = response.data;
      });
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
#inviertes:checked ~ table tbody {
  transform: scaley(-1);
}
#inviertes:checked ~ table tbody > * {
  transform: scaley(-1);
}

.page-item.active {
  border-radius: 0px;
  position: static !important;
  background-color: #ed6528 !important;
}
label {
  word-break: break-all;
  white-space: pre-line;
}
</style>
