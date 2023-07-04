<template>
  <b-container fluid>
    <b-row align-v="center">
      <b-col md="12">
        <b-form-group label="Correo solicitante" label-for="correo_solicitante">
          <b-form-input
            id="correo_solicitante"
            v-model="campos.correo_solicitante"
            type="text"
            placeholder="Correo solicitante"
          ></b-form-input>
          <small
            v-if="errores.correoSolicitanteError && !campos.correo_solicitante"
            class="text-danger"
          >
            El correo del solicitante es obligatorio
          </small>
        </b-form-group>
      </b-col>
    </b-row>
    <b-row class="mb-2" align-h="between" align-v="center">
      <b-col md="6"> Asignaciones: </b-col>
      <b-col md="3" class="text-right">
        <b-button type="button" variant="info" @click="add_entidad_template">
          Agregar<b-icon-plus style="font-size: 2em"></b-icon-plus>
        </b-button>
      </b-col>
    </b-row>
    <template v-for="(entidad_item, index) in campos.entidades">
      <b-form-row
        :key="index + 'entidad'"
        class="border border-info-light mb-4 rounded"
        align-v="center"
      >
        <b-col md="6">
          <b-form-select
            v-model="entidad_item.entidad"
            @change="onEntidadSelectChange($event, index)"
          >
            <template #first>
              <b-form-select-option :value="null" disabled
                >-- Seleccionar dependencia --</b-form-select-option
              >
            </template>
            <b-form-select-option
              v-for="entidad in entidades"
              :key="'select_entidad' + index + entidad.codigo"
              :value="{
                codigo: entidad.codigo,
                tipo: entidad.tipo,
                nombre: entidad.nombre,
                entidadHijos: entidad.entidadHijos,
                email: entidad.email,
                telefono: entidad.telefono,
              }"
            >
              {{ entidad.nombre }}
            </b-form-select-option>
          </b-form-select>
        </b-col>

        <template v-if="entidad_item.entidad">
          <b-col v-if="entidad_item.entidad.tipo == 'Personalizado'" md="6">
            <b-form-select
              v-model="entidad_item.personalizado"
              @change="onDMPSelectChange($event, index)"
            >
              <template #first>
                <b-form-select-option :value="null" disabled
                  >-- Seleccionar sub dependencia --</b-form-select-option
                >
              </template>

              <b-form-select-option
                v-for="entidad_hijo in entidad_item.entidad.entidadHijos"
                :key="'select_entidad_hijo' + index + entidad_hijo.codigo"
                :value="{
                  codigo: entidad_hijo.codigo,
                  tipo: entidad_hijo.tipo,
                  nombre: entidad_hijo.nombre,
                  entidadHijos: entidad_hijo.entidadHijos,
                  email: entidad_hijo.email,
                  telefono: entidad_hijo.telefono,
                }"
              >
                {{ entidad_hijo.nombre }}
              </b-form-select-option>
            </b-form-select>
          </b-col>
          <b-col v-if="entidad_item.entidad.tipo == 'Departamento'" md="6">
            <b-form-select
              v-model="entidad_item.departamento"
              @change="onDMPSelectChange($event, index)"
            >
              <template #first>
                <b-form-select-option :value="null" disabled
                  >-- Seleccionar departamento --</b-form-select-option
                >
              </template>

              <b-form-select-option
                v-for="departamento in departamentos[index]"
                :key="
                  'select_departamento' +
                  index +
                  departamento.departamento.id_departamento
                "
                :value="{
                  id_departamento: departamento.departamento.id_departamento,
                  id_edm: departamento.id_edm,
                  correo_dm: departamento.correo_dm,
                  telefono_dm: departamento.telefono_dm,
                  nombre: departamento.departamento.nombre,
                }"
              >
                {{ departamento.departamento.nombre }}
              </b-form-select-option>
            </b-form-select>
          </b-col>
          <b-col v-if="entidad_item.entidad.tipo == 'Municipio'" md="6">
            <b-form-select
              v-model="entidad_item.municipio"
              @change="onDMPSelectChange($event, index)"
            >
              <template #first>
                <b-form-select-option :value="null" disabled
                  >-- Seleccionar municipio --</b-form-select-option
                >
              </template>
              <b-form-select-option
                v-for="municipio in municipios[index]"
                :key="
                  'select_municipio' + index + municipio.municipio.id_municipio
                "
                :value="{
                  id_municipio: municipio.municipio.id_municipio,
                  id_edm: municipio.id_edm,
                  correo_dm: municipio.correo_dm,
                  telefono_dm: municipio.telefono_dm,
                  nombre: municipio.municipio.nombre,
                }"
              >
                {{ municipio.municipio.nombre }}
              </b-form-select-option>
            </b-form-select>
          </b-col>
          <b-col md="6">
            <b-form-input
              :id="'correo_asignacion_' + index"
              v-model="entidad_item.correo_asignacion"
              type="text"
              placeholder="Correo(s) a asignar"
            ></b-form-input>
          </b-col>
          <b-col md="5">
            <b-form-input
              :id="'telefono_asignacion_' + index"
              v-model="entidad_item.telefono_asignacion"
              type="text"
              placeholder="Telefonos(s)"
            ></b-form-input>
          </b-col>
        </template>
        <b-col md="1">
          <b-button
            type="button"
            variant="danger"
            size="sm"
            @click="remover_entidad(index)"
          >
            <b-icon-trash style="font-size: 1.5em"></b-icon-trash>
          </b-button>
        </b-col>
      </b-form-row>
    </template>
    <small
      v-if="errores.correosAsignacionError"
      class="text-danger"
      style="margin-top: -1.2rem; margin-bottom: 1.5rem; display: block"
    >
      Debe ingresar al menos un asignado
    </small>
    <b-form-group>
      <b-row align-v="center">
        <b-col md="12">
          <b-form-group label="Con copia a:" label-for="con_copia">
            <b-form-input
              id="con_copia"
              v-model="campos.CC"
              type="text"
              placeholder="CC"
            ></b-form-input>
          </b-form-group>
        </b-col>
      </b-row>
    </b-form-group>
    <b-form-group>
      <b-row align-v="center">
        <b-col md="12">
          <b-form-group
            label="Respuestas predeterminadas"
            label-for="respuestas_predeterminadas"
          >
            <b-form-select
              id="respuestas_predeterminadas"
              v-model="respuesta_intemediaria"
              @change="onRespuestaSelectChange"
            >
              <template #first>
                <b-form-select-option :value="null" disabled
                  >-- Seleccionar respuesta predeterminada
                  --</b-form-select-option
                >
              </template>
              <b-form-select-option
                v-for="respuesta in respuestas_predeterminadas"
                :key="'select_respuesta' + respuesta.id"
                :value="respuesta.contenido_respuesta"
              >
                {{ respuesta.titulo }}
              </b-form-select-option>
            </b-form-select>
          </b-form-group>
        </b-col>
      </b-row>
    </b-form-group>
    <b-form-group>
      <b-row align-v="center">
        <b-col md="12">
          <label for="mensaje_asignacion">Mensaje</label>
          <ckeditor
            id="mensaje_asignacion"
            v-model="campos.contenido"
            :editor="editor"
            :config="editorConfig"
            style="height: 200px"
          ></ckeditor>
          <small
            v-if="errores.contenidoError && !campos.contenido"
            class="text-danger"
          >
            Debe ingresar un mensaje en el campo
          </small>
        </b-col>
      </b-row>
    </b-form-group>
  </b-container>
</template>

<script>
// import Login from "@/router/auth.vue";
import axios from "axios";
import Vue from "vue";
import CKEditor from "@ckeditor/ckeditor5-vue";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import Login from "@/router/auth.vue";

Vue.use(CKEditor);

export default {
  name: "AsignacionForm",
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      solicitud: this.value,
      errores: {
        contenidoError: false,
        correoSolicitanteError: false,
        correosAsignacionError: false,
      },
      campos: {
        id: null,
        entidades: [],
        CC: "",
        contenido: "",
        correo_solicitante: "",
        correo_asignacion: "",
        telefono_asignacion: "",
      },
      campos_template: {
        entidad: null,
        departamento: null,
        municipio: null,
        correo_asignacion: "",
        telefono_asignacion: "",
        personalizado: null,
      },
      editor: ClassicEditor,
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
      respuestas_predeterminadas: [],
      respuesta_intemediaria: null,
      departamentos: [],
      municipios: [],
      entidades: [],
    };
  },

  mounted() {
    this.prepararData();
    this.fetch_respuestas_predeterminadas();
    this.fetch_entidades();

    if (this.campos.entidades.length < 1) {
      this.add_entidad_template();
    } else {
      this.campos.entidades.forEach((entidad, index) => {
        if (entidad.departamento) {
          this.fetch_departamentos(entidad.entidad.codigo, index);
        } else if (entidad.municipio) {
          this.fetch_municipios(entidad.entidad.codigo, index);
        }
      });
    }
  },

  methods: {
    prepararData() {
      this.campos.CC = "";
      this.campos.contenido = "";

      this.campos.entidades = JSON.parse(
        JSON.stringify(this.solicitud.entidadDepartamentoMunicipio),
      );
      this.campos.id = this.solicitud.id;
      this.campos.correo_solicitante = this.solicitud.correo_solicitante;
    },

    add_entidad_template() {
      this.campos.entidades.push(
        JSON.parse(JSON.stringify(this.campos_template)),
      );
    },

    remover_entidad(index) {
      this.campos.entidades.splice(index, 1);
      this.departamentos.splice(index, 1);
      this.municipios.splice(index, 1);
    },

    //  FETCH data de modelos -------------------------------------------
    fetch_respuestas_predeterminadas() {
      axios
        .get(process.env.VUE_APP_URL + "/respuestausuario")
        .then((response) => {
          this.respuestas_predeterminadas = response.data;
        });
    },

    async fetch_departamentos(codigo, index) {
      return await axios
        .get(process.env.VUE_APP_URL + `/departamento/seleccion/${codigo}`)
        .then((response) => {
          this.$set(this.departamentos, index, response.data);
        });
    },

    async fetch_municipios(codigo, index) {
      return await axios
        .get(process.env.VUE_APP_URL + `/municipios/seleccion/${codigo}`)
        .then((response) => {
          this.$set(this.municipios, index, response.data);
        });
    },

    fetch_entidades() {
      axios.get(process.env.VUE_APP_URL + "/entidadN").then((response) => {
        this.entidades = response.data;
      });
    },
    // Fin FETCH data de modelos ------------------------------------------

    // onChange events  ---------------------------------------------------

    onEntidadSelectChange(entidad, index) {
      // Reiniciando los valores al cambiar de entidad
      this.campos.entidades[index].departamento = null;
      this.campos.entidades[index].municipio = null;
      this.campos.entidades[index].personalizado = null;
      this.campos.entidades[index].correo_asignacion = "";
      this.campos.entidades[index].telefono_asignacion = "";

      // Solicitando la data segun el tipo de entidad
      if (entidad.tipo == "Departamento") {
        this.fetch_departamentos(entidad.codigo, index);
      }
      if (entidad.tipo == "Municipio") {
        this.fetch_municipios(entidad.codigo, index);
      }

      if (entidad.tipo == "Nacional") {
        this.$set(
          this.campos.entidades[index],
          "correo_asignacion",
          entidad.email,
        );
        this.$set(
          this.campos.entidades[index],
          "telefono_asignacion",
          entidad.telefono,
        );
      }
    },

    onDMPSelectChange(DMP, index) {
      if (!(this.campos.entidades[index].entidad.tipo === "Personalizado")) {
        this.$set(
          this.campos.entidades[index],
          "correo_asignacion",
          DMP.correo_dm,
        );
        this.$set(
          this.campos.entidades[index],
          "telefono_asignacion",
          DMP.telefono_dm,
        );
      } else {
        this.$set(this.campos.entidades[index], "correo_asignacion", DMP.email);
        this.$set(
          this.campos.entidades[index],
          "telefono_asignacion",
          DMP.telefono,
        );
      }
    },

    onRespuestaSelectChange(respuesta) {
      this.campos.contenido = respuesta;
      this.respuesta_intemediaria = respuesta;
    },
    // Fin de onChange events  ------------------------------------------

    AsignarSolicitud() {
      let correo_asignacion = [];
      let telefono_asignacion = [];

      this.campos.entidades.forEach((entidad) => {
        if (entidad.correo_asignacion && entidad.correo_asignacion != "") {
          correo_asignacion = correo_asignacion.concat(
            entidad.correo_asignacion.split(","),
          );
        }
        if (entidad.telefono_asignacion && entidad.telefono_asignacion != "") {
          telefono_asignacion = telefono_asignacion.concat(
            entidad.telefono_asignacion.split(","),
          );
        }
      });

      this.campos.correo_asignacion = correo_asignacion.join(",");
      this.campos.telefono_asignacion = telefono_asignacion.join(",");

      this.errores.contenidoError = !this.campos.contenido;
      this.errores.correoSolicitanteError = !this.campos.correo_solicitante;
      this.errores.correosAsignacionError = !this.campos.correo_asignacion;

      if (!this.campos.contenido) {
        this.contenidoError = true;
        return;
      }

      this.isLoading = true;

      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
        },
      };

      return axios.put(
        process.env.VUE_APP_URL + "/solicitudes2/" + this.campos.id,
        this.campos,
        config,
      );
    },
  },
};
</script>
