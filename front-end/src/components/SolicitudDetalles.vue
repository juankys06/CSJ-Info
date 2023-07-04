<template>
  <div>
    <div class="form-group align-items-center">
      <div
        v-for="(solicitud2, contenido_key) in JSON.parse(solicitud.contenido)"
        :key="solicitud2.id"
        class="col-md-12 control-label"
      >
        <p v-if="contenido_key != 'entidad'" class="mt-2">
          <b>{{ contenido_key + ": " }}</b>
        </p>
        <div
          style="overflow: auto; white-space: pre-line; word-break: break-all"
          v-html="procesar_contenido(solicitud2)"
        ></div>
      </div>

      <div
        v-if="
          solicitud.entidadDepartamentoMunicipio &&
          solicitud.entidadDepartamentoMunicipio.length > 0
        "
      >
        <label class="col-md-12 control-label"><b>Asignaciones:</b> </label>
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
              v-for="entidad in solicitud.entidadDepartamentoMunicipio"
              :key="'asignacion' + entidad.entidad.codigo"
            >
              <td>{{ entidad.entidad.nombre }}</td>
              <td>
                {{ entidad.departamento ? entidad.departamento.nombre : "-" }}
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
        ><b>Status:</b> {{ solicitud.status.nombreStatus }}
      </label>

      <div v-if="solicitud.archivos.length != 0">
        <label
          v-for="archivo in solicitud.archivos"
          :key="archivo.id"
          class="col-md-12 control-label"
        >
          <template v-if="checkImg(archivo.url)">
            <a :href="archivo.url" target="_blank">
              <img :src="archivo.url" alt="" style="max-width: 100%" />
            </a>
          </template>
          <template v-else>
            <i
              class="fa fa-download"
              style="font-size: 19px"
              aria-hidden="true"
            ></i>
            <a :href="archivo.url" target="_blank">
              {{ " " + archivo.url.substring(14, archivo.url.length) }}
            </a>
          </template>
        </label>
      </div>
    </div>
    <hr size="1px" color="gray" />
    <div class="form-group align-items-center">
      <label class="col-md-12 control-label"
        ><b>Nro de Solicitud:</b> {{ solicitud.id }}
      </label>

      <label
        v-if="solicitud.correo_asignacion != null && $route.name == 'auditoria'"
        class="col-md-12 control-label"
        ><b>Asignado a:</b> {{ solicitud.correo_asignacion }}
      </label>

      <label
        v-if="
          solicitud.telefono_asignacion != null && $route.name == 'auditoria'
        "
        class="col-md-12 control-label"
        ><b>Telefonos:</b> {{ solicitud.telefono_asignacion }}
      </label>

      <label class="col-md-12 control-label"
        ><b>Fecha de Actualizacion:</b>
        {{ new Date(solicitud.updateDateTime).toLocaleString("es-CO") }}
      </label>

      <label v-if="solicitud.usuario != 'null'" class="col-md-12 control-label"
        ><b>Actualizado por:</b> {{ solicitud.usuario }}
      </label>
      <label v-if="solicitud.usuario == 'null'" class="col-md-12 control-label"
        ><b>Actualizado por:</b>No Actualizado</label
      >
      <label v-if="solicitud.parent" class="col-md-12 control-label"
        ><b>Padre:</b> {{ solicitud.parent }}</label
      >
    </div>
    <div class="form-group align-items-center">
      <label v-if="solicitud.solicitudes.length" class="col-md-12 control-label"
        ><b>Solicitudes combinadas:</b>
      </label>
      <div class="accordion w-100" role="tablist">
        <b-card
          v-for="child_solicitud in solicitud.solicitudes"
          :key="child_solicitud.id"
          class="mb-1"
          no-body
        >
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-button
              v-b-toggle="'acordion-' + child_solicitud.id"
              variant="info"
              block
              >{{ child_solicitud.id }} - {{ child_solicitud.asunto }}</b-button
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
                  v-for="(solicitud2_child, contenido_key) in JSON.parse(
                    child_solicitud.contenido,
                  )"
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
                    child_solicitud.entidadDepartamentoMunicipio.length > 0
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
                        <th scope="col">Teléfono(s)</th>
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
                        <td>{{ entidad_child.telefono_asignacion }}</td>
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
                      " " + archivos.url.substring(14, archivos.url.length)
                    }}</a></label
                  >
                </div>

                <label
                  v-if="child_solicitud.correo_asignacion != null"
                  class="col-md-12 control-label"
                  ><b>Asignado a:</b>
                  {{ child_solicitud.correo_asignacion }}
                </label>

                <label
                  v-if="child_solicitud.telefono_asignacion != null"
                  class="col-md-12 control-label"
                  ><b>Telefonos:</b>
                  {{ child_solicitud.telefono_asignacion }}
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

                <label
                  v-if="
                    child_solicitud.telefono_asignacion != null &&
                    $route.name == 'auditoria'
                  "
                  class="col-md-12 control-label"
                  ><b>Telefonos:</b>
                  {{ child_solicitud.telefono_asignacion }}
                </label>

                <label class="col-md-12 control-label">
                  <b>Fecha de Actualizacion:</b>
                  {{
                    new Date(child_solicitud.updateDateTime).toLocaleString(
                      "es-CO",
                    )
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
</template>

<script>
export default {
  name: "SolicitudDetalles",
  props: {
    solicitud: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {};
  },

  mounted() {},

  methods: {
    procesar_contenido(contenido) {
      return typeof contenido == "string"
        ? contenido.replaceAll("||,||", "\r\n\r\n <hr> \r\n\r\n ")
        : contenido;
    },

    checkImg(url) {
      return url.match(/\.(jpeg|jpg|gif|png|svg)$/) != null;
    },
  },
};
</script>

<style lang="scss" scoped></style>
