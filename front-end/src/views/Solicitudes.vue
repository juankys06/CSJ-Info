<template>
  <div class="solicitudes">
    <loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></loading>
    <!--<pulse-loader :loading="false" :color="color" :size="size"></pulse-loader>-->
    <div class="Order" style="text-align: center">
      <div class="row align-items-center" style="justify-content: space-evenly">
        <div>
          <button
            class="btn"
            style="background-color: #19325b; color: white"
            @click="Ordenar('id')"
          >
            Ordenar por radicado
          </button>
        </div>
        <div>
          <button
            class="btn"
            style="background-color: #19325b; color: white"
            @click="Ordenar('solicitudes')"
          >
            Restablecer Filtros
          </button>
        </div>
        <!-- <div>
          <button
            class="btn"
            style="background-color: #19325b; color: white"
            @click="sincronizar()"
          >
            Sincronizar
          </button>
        </div> -->
        <div>
          <button
            class="btn"
            style="background-color: #19325b; color: white"
            @click="fetch_solicitudes()"
          >
            Refrescar
          </button>
        </div>
        <div>
          <b-form-checkbox v-model="show_combinadas" switch size="lg"
            >Mostrar combinadas</b-form-checkbox
          >
        </div>
      </div>
    </div>
    <br />
    <br />
    <div class="row">
      <b-col md="3" offset="8">
        <div class="row flex-nowrap">
          <b-form-input
            id="searchTerm"
            v-model="searchText"
            type="search"
            placeholder="Buscar"
            size="md"
            @keyup.native.enter="tabla"
            @keyup.native="tabla"
          ></b-form-input>
          <b-input-group-append class="mr-3">
            <b-button
              style="background-color: #19325b; color: white"
              @click="tabla"
            >
              Ir
            </b-button>
          </b-input-group-append>
        </div>
        <!-- <input
          id="searchTerm"
          v-model="searchText"
          type="text"
          style="border: solid; color: gray; border-radius: 5px"
          @keyup="tabla"
        /> -->
      </b-col>
      <table
        v-if="pag"
        id="regTable"
        :key="cont"
        class="table table-striped table-hover"
        style="margin: auto; width: 70%"
      >
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">Asunto</th>
            <th scope="col">Correo solicitante</th>
            <th scope="col">Fecha</th>
            <th scope="col">Estatus</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(solicitud, index) in getItems" :key="solicitud.id">
            <td scope="row">
              {{ new Date(solicitud.fecha).getFullYear() + "-" + solicitud.id }}
            </td>
            <td>{{ solicitud.asunto }}</td>
            <td>{{ solicitud.correo_solicitante }}</td>
            <td>
              {{
                new Date(solicitud.fecha).getDate() +
                "/" +
                (new Date(solicitud.fecha).getMonth() + 1) +
                "/" +
                new Date(solicitud.fecha).getFullYear() +
                " " +
                ("0" + (new Date(solicitud.fecha).getHours() % 12) || 12).slice(
                  -2,
                ) +
                ":" +
                ("0" + new Date(solicitud.fecha).getMinutes()).slice(-2) +
                " " +
                (new Date(solicitud.fecha).getHours() >= 12 ? "PM" : "AM")
              }}
            </td>
            <td
              v-if="solicitud.status.id == 1 || solicitud.status.id == 5"
              style="background: red"
            >
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 3" style="background: chartreuse">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 7" style="background: orange">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 8" style="background: #c6ff16">
              {{ solicitud.status.nombreStatus }}
            </td>

            <td v-if="solicitud.status.id == 9" style="background: #718cff">
              {{ solicitud.status.nombreStatus }} con: {{ solicitud.parent }}
            </td>
            <td v-if="solicitud.status.id == 2" style="background: yellow">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 4" style="background: green">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 6" style="background: #00aae4">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 10" style="background: #ffd2d2">
              {{ solicitud.status.nombreStatus }}
            </td>
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
                    @click="solicitud_seleccionada(solicitud)"
                    >Detalles</a
                  >
                  <a
                    v-if="admin && solicitud.status.id != 9"
                    href="#Estatus"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="(estatus_id = solicitud.status.id), (i = index)"
                    >Cambiar Estatus</a
                  >
                  <template v-if="solicitud.status.id != 10">
                    <a
                      v-if="solicitud.status.id == 9"
                      href="#Descombinar"
                      class="dropdown-item"
                      @click="set_descombinar(solicitud)"
                      >Descombinar</a
                    >
                    <a
                      v-if="
                        solicitud.status.id != 6 && solicitud.status.id != 9
                      "
                      class="dropdown-item"
                      href="#"
                      @click="show_modal_asignacion(solicitud)"
                    >
                      {{
                        solicitud.correo_asignacion ? "Reasignar" : "Asignar"
                      }}
                    </a>
                    <a
                      v-if="
                        solicitud.status.id != 2 &&
                        solicitud.status.id != 9 &&
                        solicitud.status.id != 6
                      "
                      href=""
                      class="dropdown-item"
                      @click.prevent="presentAction(solicitud, 7)"
                      >Solicitar información</a
                    >
                    <a
                      v-if="
                        solicitud.status.id != 2 && solicitud.status.id != 9
                      "
                      href=""
                      class="dropdown-item"
                      @click.prevent="presentAction(solicitud)"
                      >Resolución y Cierre</a
                    >
                  </template>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <table
        v-else
        id="regTable"
        :key="cont"
        class="table table-striped table-hover"
        style="margin: auto; width: 70%"
      >
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">Asunto</th>
            <th scope="col">Correo solicitante</th>
            <th scope="col">Fecha</th>
            <th scope="col">Estatus</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(solicitud, index) in solicitud.filter(
              (solicitud_item) =>
                solicitud_item.status.id != 9 || show_combinadas,
            )"
            :key="solicitud.id"
          >
            <td scope="row">
              {{ new Date(solicitud.fecha).getFullYear() + "-" + solicitud.id }}
            </td>
            <td>{{ solicitud.asunto }}</td>
            <td>{{ solicitud.correo_solicitante }}</td>
            <td>
              {{
                new Date(solicitud.fecha).getDate() +
                "/" +
                (new Date(solicitud.fecha).getMonth() + 1) +
                "/" +
                new Date(solicitud.fecha).getFullYear() +
                " " +
                ("0" + (new Date(solicitud.fecha).getHours() % 12) || 12).slice(
                  -2,
                ) +
                ":" +
                ("0" + new Date(solicitud.fecha).getMinutes()).slice(-2) +
                " " +
                (new Date(solicitud.fecha).getHours() >= 12 ? "PM" : "AM")
              }}
            </td>
            <td
              v-if="solicitud.status.id == 1 || solicitud.status.id == 5"
              style="background: red"
            >
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 3" style="background: chartreuse">
              {{ solicitud.status.nombreStatus }}
            </td>

            <td v-if="solicitud.status.id == 7" style="background: orange">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 8" style="background: #c6ff16">
              {{ solicitud.status.nombreStatus }}
            </td>

            <td v-if="solicitud.status.id == 9" style="background: #718cff">
              {{ solicitud.status.nombreStatus }} con: {{ solicitud.parent }}
            </td>

            <td v-if="solicitud.status.id == 2" style="background: yellow">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 4" style="background: green">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 6" style="background: #00aae4">
              {{ solicitud.status.nombreStatus }}
            </td>
            <td v-if="solicitud.status.id == 10" style="background: #ffd2d2">
              {{ solicitud.status.nombreStatus }}
            </td>
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
                    @click="solicitud_seleccionada(solicitud)"
                    >Detalles</a
                  >
                  <a
                    v-if="admin && solicitud.status.id != 9"
                    href="#Estatus"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="(estatus_id = solicitud.status.id), (i = index)"
                    >Cambiar Estatus</a
                  >
                  <template v-if="solicitud.status.id != 10">
                    <a
                      v-if="solicitud.status.id == 9"
                      href="#Descombinar"
                      class="dropdown-item"
                      @click="set_descombinar(solicitud)"
                      >Descombinar</a
                    >
                    <a
                      v-if="
                        solicitud.status.id != 6 && solicitud.status.id != 9
                      "
                      class="dropdown-item"
                      href="#"
                      @click="show_modal_asignacion(solicitud)"
                    >
                      {{
                        solicitud.correo_asignacion ? "Reasignar" : "Asignar"
                      }}
                    </a>
                    <a
                      v-if="
                        solicitud.status.id != 2 &&
                        solicitud.status.id != 9 &&
                        solicitud.status.id != 6
                      "
                      href=""
                      class="dropdown-item"
                      @click.prevent="presentAction(solicitud, 7)"
                      >Solicitar información</a
                    >
                    <a
                      v-if="
                        solicitud.status.id != 2 && solicitud.status.id != 9
                      "
                      href=""
                      class="dropdown-item"
                      @click.prevent="presentAction(solicitud)"
                      >Resolución y Cierre</a
                    >
                  </template>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
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

    <div
      v-if="bandera && solicitud_selected"
      id="Detalles"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div
        class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable"
        :style="style_modal"
        role="document"
      >
        <div class="modal-content" style="max-width: 100%">
          <div class="modal-header">
            <h5 class="modal-title">Detalles de la Solicitud</h5>
            <div>
              <button
                type="button"
                class="close"
                data-dismiss="modal"
                aria-label="Close"
              >
                <span aria-hidden="true">&times;</span>
              </button>
              <button
                type="button"
                class="close"
                @click="maximizar = !maximizar"
              >
                <b-icon icon="arrows-angle-expand" font-scale="0.75"></b-icon>
              </button>
            </div>
          </div>

          <div class="modal-body">
            <div class="form-group align-items-center">
              <div
                v-for="(solicitud2, contenido_key) in JSON.parse(
                  solicitud_selected.contenido,
                )"
                :key="solicitud2.id"
                class="col-md-12 control-label"
              >
                <p v-if="contenido_key != 'entidad'" class="mt-2">
                  <b>{{ contenido_key + ": " }}</b>
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
                  solicitud_selected.entidadDepartamentoMunicipio &&
                  solicitud_selected.entidadDepartamentoMunicipio.length > 0
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
                      v-for="entidad in solicitud_selected.entidadDepartamentoMunicipio"
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
                    </tr>
                  </tbody>
                </table>
              </div>

              <label class="col-md-12 control-label"
                ><b>Status:</b> {{ solicitud_selected.status.nombreStatus }}
              </label>

              <div v-if="solicitud_selected.archivos.length != 0">
                <label
                  v-for="archivos in solicitud_selected.archivos"
                  :key="archivos.id"
                  class="col-md-12 control-label"
                  ><i
                    class="fa fa-download"
                    style="font-size: 19px"
                    aria-hidden="true"
                  ></i
                  ><a :href="archivos.url" target="_blank">{{
                    " " + archivos.url.substring(14, archivos.url.length)
                  }}</a></label
                >
              </div>
            </div>
            <hr size="1px" color="gray" />
            <div class="form-group align-items-center">
              <label class="col-md-12 control-label"
                ><b>Nro de Solicitud:</b> {{ solicitud_selected.id }}
              </label>

              <label
                v-if="
                  solicitud_selected.correo_asignacion != null &&
                  $route.name == 'auditoria'
                "
                class="col-md-12 control-label"
                ><b>Asignado a:</b> {{ solicitud_selected.correo_asignacion }}
              </label>

              <label class="col-md-12 control-label"
                ><b>Fecha de Actualizacion:</b
                >{{
                  new Date(solicitud_selected.updateDateTime).getDate() +
                  "/" +
                  (new Date(solicitud_selected.updateDateTime).getMonth() + 1) +
                  "/" +
                  new Date(solicitud_selected.updateDateTime).getFullYear() +
                  " " +
                  (
                    "0" +
                      (new Date(solicitud_selected.updateDateTime).getHours() %
                        12) || 12
                  ).slice(-2) +
                  ":" +
                  (
                    "0" +
                    new Date(solicitud_selected.updateDateTime).getMinutes()
                  ).slice(-2) +
                  " " +
                  (new Date(solicitud_selected.updateDateTime).getHours() >= 12
                    ? "PM"
                    : "AM")
                }}
              </label>

              <label
                v-if="solicitud_selected.usuario != 'null'"
                class="col-md-12 control-label"
                ><b>Actualizado por:</b> {{ solicitud_selected.usuario }}
              </label>
              <label
                v-if="solicitud_selected.usuario == 'null'"
                class="col-md-12 control-label"
                ><b>Actualizado por:</b>No Actualizado</label
              >
              <label
                v-if="solicitud_selected.parent"
                class="col-md-12 control-label"
                ><b>Padre:</b> {{ solicitud_selected.parent }}</label
              >
            </div>
            <div class="form-group align-items-center">
              <label
                v-if="solicitud_selected.solicitudes.length"
                class="col-md-12 control-label"
                ><b>Solicitudes combinadas:</b>
              </label>
              <div class="accordion w-100" role="tablist">
                <b-card
                  v-for="child_solicitud in solicitud_selected.solicitudes"
                  :key="child_solicitud.id"
                  class="mb-1"
                  no-body
                >
                  <b-card-header header-tag="header" class="p-1" role="tab">
                    <b-button
                      v-b-toggle="'acordion-' + child_solicitud.id"
                      variant="info"
                      block
                      >{{ child_solicitud.id }} -
                      {{ child_solicitud.asunto }}</b-button
                    >
                  </b-card-header>
                  <b-collapse
                    :id="'acordion-' + child_solicitud.id"
                    accordion="my-accordion"
                    role="tabpanel"
                  >
                    <b-card-body>
                      <div class="form-group row align-items-center">
                        <div
                          v-for="(
                            solicitud2_child, contenido_key
                          ) in JSON.parse(child_solicitud.contenido)"
                          :key="solicitud2_child.id"
                          class="col-md-12 control-label"
                        >
                          <p v-if="contenido_key != 'entidad'">
                            <b>{{ contenido_key + ": " }}</b>
                          </p>
                          <div
                            style="
                              overflow: auto;
                              white-space: pre-line;
                              word-break: break-all;
                            "
                            v-html="procesar_contenido(solicitud2_child)"
                          ></div>
                        </div>

                        <div
                          v-if="
                            child_solicitud.entidadDepartamentoMunicipio &&
                            child_solicitud.entidadDepartamentoMunicipio
                              .length > 0
                          "
                        >
                          <table class="table">
                            <caption>
                              Asignaciones
                            </caption>
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
                                v-for="(
                                  entidad_child, index_child
                                ) in child_solicitud.entidadDepartamentoMunicipio"
                                :key="
                                  'asignacion_child' +
                                  index_child +
                                  entidad_child.entidad.codigo
                                "
                              >
                                <td>{{ entidad_child.entidad.nombre }}</td>
                                <td>
                                  {{
                                    entidad_child.departamento
                                      ? entidad_child.departamento.nombre
                                      : "-"
                                  }}
                                </td>
                                <td>
                                  {{
                                    entidad_child.municipio
                                      ? entidad_child.municipio.nombre
                                      : "-"
                                  }}
                                </td>
                                <td>{{ entidad_child.correo_asignacion }}</td>
                              </tr>
                            </tbody>
                          </table>
                        </div>

                        <label class="col-md-12 control-label"
                          ><b>Status:</b>
                          {{ child_solicitud.status.nombreStatus }}
                        </label>

                        <div v-if="child_solicitud.archivos.length != 0">
                          <label
                            v-for="archivos in child_solicitud.archivos"
                            :key="archivos.id"
                            class="col-md-12 control-label"
                            ><i
                              class="fa fa-download"
                              style="font-size: 19px"
                              aria-hidden="true"
                            ></i
                            ><a :href="archivos.url" target="_blank">{{
                              " " +
                              archivos.url.substring(14, archivos.url.length)
                            }}</a></label
                          >
                        </div>

                        <label
                          v-if="child_solicitud.correo_asignacion != null"
                          class="col-md-12 control-label"
                          ><b>Asignado a:</b>
                          {{ child_solicitud.correo_asignacion }}
                        </label>
                      </div>
                      <hr size="1px" color="gray" />
                      <div class="form-group row align-items-center">
                        <label class="col-md-12 control-label"
                          ><b>Nro de Solicitud:</b> {{ child_solicitud.id }}
                        </label>

                        <label
                          v-if="
                            child_solicitud.correo_asignacion != null &&
                            $route.name == 'auditoria'
                          "
                          class="col-md-12 control-label"
                          ><b>Asignado a:</b>
                          {{ child_solicitud.correo_asignacion }}
                        </label>

                        <label class="col-md-12 control-label"
                          ><b>Fecha de Actualizacion:</b
                          >{{
                            new Date(child_solicitud.updateDateTime).getDate() +
                            "/" +
                            (new Date(
                              child_solicitud.updateDateTime,
                            ).getMonth() +
                              1) +
                            "/" +
                            new Date(
                              child_solicitud.updateDateTime,
                            ).getFullYear() +
                            " " +
                            (
                              "0" +
                                (new Date(
                                  child_solicitud.updateDateTime,
                                ).getHours() %
                                  12) || 12
                            ).slice(-2) +
                            ":" +
                            (
                              "0" +
                              new Date(
                                child_solicitud.updateDateTime,
                              ).getMinutes()
                            ).slice(-2) +
                            " " +
                            (new Date(
                              child_solicitud.updateDateTime,
                            ).getHours() >= 12
                              ? "PM"
                              : "AM")
                          }}
                        </label>

                        <label
                          v-if="child_solicitud.usuario != 'null'"
                          class="col-md-12 control-label"
                          ><b>Actualizado por:</b> {{ child_solicitud.usuario }}
                        </label>
                        <label
                          v-if="child_solicitud.usuario == 'null'"
                          class="col-md-12 control-label"
                          ><b>Actualizado por:</b>No Actualizado</label
                        >
                      </div>
                    </b-card-body>
                  </b-collapse>
                </b-card>
              </div>
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

    <div
      v-if="bandera"
      id="Estatus"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Cambiar Estatus de la Solicitud</h5>
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
              <div class="form-group row align-items-center">
                <label class="col-md-2 col-form-label">Status:</label>
                <div class="col-md-8">
                  <select v-model="estatus_id" class="form-control" required>
                    <option
                      v-for="(estatus, index) in estatusFiltered"
                      :key="estatus.id + index"
                      :value="estatus.id"
                    >
                      {{ estatus.nombreStatus }}
                    </option>
                  </select>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-success"
              @click="CambiarEstatus()"
            >
              Cambiar
            </button>
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

    <!-- Modal asignacion -->
    <b-modal
      id="modal-asignacion"
      :key="'modal-asignacion'"
      :title="'Asignacion de solicitud'"
      :ok-title="'Asignar'"
      scrollable
      size="lg"
      @ok="AsignarSolicitud"
    >
      <asignacion-form v-model="asignar_fields"></asignacion-form>
    </b-modal>

    <!-- Modal descombinar -->
    <b-modal
      id="modal-descombinacion"
      :title="'Descombinar solicitud'"
      :ok-title="'Descombinar'"
      scrollable
      :ok-disabled="!solicitud_descombinar"
      @ok="descombinar_request"
    >
      <p>¿Esta seguro que desea descombinar la solicitud?:</p>
    </b-modal>
  </div>
</template>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
//import PulseLoader from 'vue-spinner/src/PulseLoader.vue'
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Login from "@/router/auth.vue";
import axios from "axios";
import Vue from "vue";
import CKEditor from "@ckeditor/ckeditor5-vue";
import Paginate from "vuejs-paginate";
Vue.component("paginate", Paginate);
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import AsignacionForm from "@/components/AsignacionForm.vue";
Vue.use(CKEditor);

axios.defaults.timeout = 900000;

export default {
  data() {
    return {
      fields: {},
      maximizar: false,
      searchText: "",
      authenticated: Login.methods.check(),
      user: Login.methods.user,
      fieldsCorreo: {},
      solicitud_selected: null,
      asignar_fields: {
        entidades: [],
        CC: "",
        contenido: null,
        correo_solicitante: "",
        correo_asignacion: "",
      },
      solicitud_descombinar: null,
      estatus: [],
      formulario: null,
      solicitud: [],
      personalizados: [],
      departamento: false,
      personalizado: false,
      municipio: false,
      departamento_id: "",
      municipio_id: "",
      i: 0,
      admin: false,
      usuario: "",
      estatus_id: "",
      entidad_codigo: "",
      entidadCodigo: "",
      active: false,
      entidadOrden: "",
      cont: 0,
      estatusOrden: "",
      bandera: false,
      seccionPag: "",
      edm: "",
      validar: "",
      entidadP: "",
      pag: true,
      currentPage: 1,
      usersPag: [],
      parPage: 10,
      url: "",
      editor: ClassicEditor,
      editorData: "<p></p>",
      editorConfig: {
        toolbar: [
          "heading",
          "|",
          "bold",
          "italic",
          "link",
          "|",
          "bulletedList",
          "numberedList",
          "|",
          "blockQuote",
          "mediaEmbed",
          "insertTable",
          "undo",
          "redo",
        ],
        link: {
          addTargetToExternalLinks: true,
        },
      },
      isLoading: true,
      respuesta_id: "",
      show_combinadas: false,
    };
  },
  components: {
    Loading,
    AsignacionForm,
  },
  methods: {
    set_descombinar(solicitud) {
      this.solicitud_descombinar = solicitud;
      this.$bvModal.show("modal-descombinacion");
    },

    AsignarSolicitud() {
      this.isLoading = true;

      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
        },
      };

      let correo_asignacion = [];

      this.asignar_fields.entidades.forEach((entidad) => {
        correo_asignacion = correo_asignacion.concat(
          entidad.correo_asignacion.split(","),
        );
      });

      this.asignar_fields.correo_asignacion = correo_asignacion.join(",");

      axios
        .put(
          process.env.VUE_APP_URL +
            "/solicitudes2/" +
            this.solicitud_selected.id,
          this.asignar_fields,
          config,
        )
        .then((response) => {
          this.isLoading = false;
          this.$bvModal
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
              this.fetch_solicitudes();
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

    descombinar_request() {
      axios
        .put(
          process.env.VUE_APP_URL +
            "/solicitudes/descombinar/" +
            this.solicitud_descombinar.id,
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
              this.fetch_solicitudes();
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

    show_modal_asignacion(solicitud) {
      this.asignar_fields.CC = "";
      this.asignar_fields.contenido = "";

      this.asignar_fields.entidades = JSON.parse(
        JSON.stringify(solicitud.entidadDepartamentoMunicipio),
      );
      this.asignar_fields.correo_solicitante = solicitud.correo_solicitante;
      this.$bvModal.show("modal-asignacion");
      this.$nextTick().then(() => {
        document
          .querySelector(".modal-content[tabindex]")
          ?.removeAttribute("tabindex");
      });
      this.solicitud_seleccionada(solicitud);
    },

    procesar_contenido(contenido) {
      return typeof contenido == "string"
        ? contenido.replaceAll("||,||", "\r\n\r\n <hr> \r\n\r\n ")
        : contenido;
    },
    solicitud_seleccionada(solicitud) {
      this.solicitud_selected = { ...solicitud };
    },
    tabla() {
      var tableReg = document.getElementById("regTable");

      var searchText = document
        .getElementById("searchTerm")
        .value.toLowerCase();

      if (searchText == "") {
        this.pag = true;
      } else {
        this.pag = false;
      }

      for (var i = 1; i < tableReg.rows.length; i++) {
        var cellsOfRow = tableReg.rows[i].getElementsByTagName("td");
        var found = false;
        for (var j = 0; j < cellsOfRow.length && !found; j++) {
          var compareWith = cellsOfRow[j].innerHTML.toLowerCase();
          if (searchText.length == 0 || compareWith.indexOf(searchText) > -1) {
            found = true;
          }
        }
        if (found) {
          tableReg.rows[i].style.display = "";
        } else {
          tableReg.rows[i].style.display = "none";
        }
      }
    },
    Email() {
      this.fieldsCorreo.correo_asignacion = this.validar.correo;
      this.edm = this.validar.id;
    },
    actualEntidad(data, index) {
      this.i = index;
      this.departamento = false;
      this.municipio = false;
      if (data.entidadDepartamentoMunicipio != null) {
        this.edm = data.entidadDepartamentoMunicipio.id_edm;
        this.entidad_codigo = data.entidadDepartamentoMunicipio.entidad.codigo;
        if (data.entidadDepartamentoMunicipio.entidad.tipo == "Nacional") {
          this.fieldsCorreo.correo_asignacion =
            data.entidadDepartamentoMunicipio.entidad.email;
        }

        if (data.entidadDepartamentoMunicipio.departamento) {
          this.departamento = true;
          this.departamento_id =
            data.entidadDepartamentoMunicipio.departamento.id_departamento;
          this.fieldsCorreo.correo_asignacion =
            data.entidadDepartamentoMunicipio.correo_dm;
        }
        if (data.entidadDepartamentoMunicipio.municipio) {
          this.municipio = true;
          this.municipio_id =
            data.entidadDepartamentoMunicipio.municipio.id_municipio;
          this.fieldsCorreo.correo_asignacion =
            data.entidadDepartamentoMunicipio.correo_dm;
        }
      }
    },
    reset() {
      this.fieldsCorreo = {};
      this.respuesta_id = "";
      this.entidad_codigo = "";
    },
    actualizarRespuesta() {
      this.fieldsCorreo.contenido = "";
      this.fieldsCorreo.contenido = this.respuesta_id.contenido;
    },

    presentAction(data, statusId = 3) {
      this.$router.push({
        name: "respuesta",
        params: {
          solicitud: data,
          statusId: statusId,
          formulario_url: this.$route.params.formulario_url,
        },
      });
    },

    CambiarEstatus() {
      this.fields.header = { Authorization: "Bearer " + Login.methods.token };
      let vm = this;
      this.isLoading = true;

      let solicitud_id = this.pag
        ? this.getItems[this.i].id
        : this.solicitud[this.i].id;
      axios
        .put(
          process.env.VUE_APP_URL +
            "/status/" +
            this.estatus_id +
            "/solicitudes/" +
            solicitud_id,
          this.fields,
        )
        .then(function () {
          vm.$forceUpdate();
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Estatus Cambiado",
            width: 350,
            padding: "0.5em",
            icon: "success",
          }).then(() => {
            vm.actualizar();
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
    },
    Ordenar(tipo) {
      if (tipo == "entidad") {
        tipo = "entidad/solicitudes";
      }
      if (tipo == "id") {
        tipo = "id/solicitudes";
      }

      let vm = this;
      this.isLoading = true;
      axios
        .get(
          process.env.VUE_APP_URL +
            "/" +
            tipo +
            "?formulario=" +
            vm.formulario.id,
        )
        .then(function (response) {
          if (tipo == "solicitudes") {
            vm.show_combinadas = false;
            vm.searchText = "";
            vm.$nextTick().then(() => {
              vm.tabla();
            });
          }
          vm.isLoading = false;
          vm.solicitud = response.data;
          vm.solicitud.map((solicitud) => {
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
    },
    actualizar() {
      let vm = this;

      for (var j = 0; j < this.user.permisos.length; j++) {
        if (this.user.permisos[j] == "Administrador") {
          this.admin = true;

          break;
        }
      }

      if (this.admin) {
        vm.fetch_solicitudes();
      } else {
        vm.fetch_solicitudes();
      }
      axios.get(process.env.VUE_APP_URL + "/status").then(function (response) {
        vm.estatus = response.data;
      });
    },
    clickCallback: function (pageNum) {
      this.currentPage = Number(pageNum);
    },

    fetch_formulario() {
      this.isLoading = true;

      axios
        .get(
          `${process.env.VUE_APP_URL}/formularios/url/${this.$route.params.formulario_url}`,
        )
        .then((response) => {
          this.formulario = response.data;
          this.fetch_solicitudes();
        })
        .catch((e) => {
          this.isLoading = false;
          Swal.fire({
            title: "Error de formulario!",
            text: "Hubo un error al traer el formulario",
            width: 350,
            padding: "0.5em",
            icon: "error",
          });
        });
    },

    fetch_solicitudes() {
      if (!this.formulario) {
        this.$router.push({ name: "formularioSeleccion" });
      }

      this.isLoading = true;
      axios
        .get(
          process.env.VUE_APP_URL +
            "/solicitudes?formulario=" +
            this.formulario.id,
        )
        .then((response) => {
          this.solicitud = response.data;
          this.solicitud.map((solicitud) => {
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
          this.bandera = true;
          this.solicitud_descombinar = null;

          for (var i = 0; i < this.solicitud.length; i++) {
            delete JSON.parse(this.solicitud[i].contenido).entidad;
            Object.keys(JSON.parse(this.solicitud[i].contenido)).forEach(
              (formulario_item) => {},
            );
          }
        })
        .catch((error) => {
          this.isLoading = false;
          this.solicitud_descombinar = null;
          console.log(error.response);
          console.log(error);
          Swal.fire({
            title: "Error!",
            text: error.response.data.message,
            width: 350,
            padding: "0.5em",
            icon: "error",
          });
        });
    },

    sincronizar() {
      let vm = this;
      vm.isLoading = true;

      if (vm.formulario.activo) {
        axios
          .post(
            process.env.VUE_APP_URL +
              "/download_emails?formulario=" +
              vm.formulario.id,
          )
          .then(function () {
            vm.isLoading = false;
            Swal.fire({
              title: "¡La bandeja de entrada se ha actualizado!",
              width: 350,
              padding: "0.5em",
            }).then(() => {
              vm.fetch_solicitudes();
            });
          })
          .catch(function (error) {
            vm.isLoading = false;
            if (error.response.status == 511) {
              window.location.href = "" + error.response.data.redirect_uri;
            } else if (error.response.status == 502) {
              console.log(error.response);
              console.log(error);
              Swal.fire({
                title: "Error!",
                text: "El proveedor de correo ha tardado en responder intente de nuevo",
                width: 350,
                padding: "0.5em",
                icon: "error",
              });
            } else {
              Swal.fire({
                title: "Error!",
                text: error.response.data.message,
                width: 350,
                padding: "0.5em",
                icon: "error",
              });
            }

            vm.fetch_solicitudes();
          });
      } else {
        vm.fetch_solicitudes();
      }
    },
  },
  computed: {
    style_modal() {
      return this.maximizar
        ? "max-width: 100%; margin-left: 17px; margin-top: 0px;margin-bottom: 0px;min-height: 100vh;"
        : "";
    },
    getItems: function () {
      let current = this.currentPage * this.parPage;
      let start = current - this.parPage;
      this.seccionPag = start;
      return this.solicitud
        .slice(start, current)
        .filter(
          (solicitud) => solicitud.status.id != 9 || this.show_combinadas,
        );
    },
    getPageCount: function () {
      return Math.ceil(this.solicitud.length / this.parPage);
    },
    estatusFiltered() {
      return this.estatus;
    },
  },
  mounted() {
    let vm = this;
    for (var j = 0; j < this.user.permisos.length; j++) {
      if (this.user.permisos[j] == "Administrador") {
        this.admin = true;
        break;
      }
    }

    if (this.formulario) {
      this.fetch_solicitudes();
    } else if (this.$route.params.formulario_url) {
      this.fetch_formulario();
    } else {
      this.$router.push({ name: "formularioSeleccion" });
    }

    axios.get(process.env.VUE_APP_URL + "/status").then(function (response) {
      vm.estatus = response.data;
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

.page-item.active {
  border-radius: 0px;
  position: static !important;
  background-color: #ed6528 !important;
}
</style>
