<template>
  <div class="Formulario" style="margin: auto; width: 50%">
    <Loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></Loading>
    <div style="text-align: center">
      <h4>{{ formularios.descripcion }}</h4>
      <span>{{ formularios.email }}</span>
    </div>
    <br />
    <form @submit.prevent="formulario()">
      <vue-form-generator :schema="schema" :model="model"></vue-form-generator>
      <div class="form-group">
        <div
          v-for="(link, index) in linksTotal"
          :key="index"
          class="form-group align-items-center"
        >
          <a class="col-md-4 col-form-label" :href="link.url" target="_blank">{{
            link.texto
          }}</a>
        </div>
      </div>

      <div v-if="adjuntar == true" class="form-group">
        <div class="form-group align-items-center">
          <label class="col-md-4 col-form-label">Aduntar Archivos:</label>
          <div class="col-md-8">
            <input
              id="files"
              ref="files"
              type="file"
              multiple
              @change="processFile($event)"
            />
            <br />
            Tamaño maximo de archivos 10MB
          </div>
        </div>
      </div>

      <div class="form-group" style="text-align: center">
        <div class="form-group">
          <VueRecaptcha
            ref="recaptcha"
            style="margin-left: 30%"
            sitekey="6LcuIM0ZAAAAAKtPxDbOzfmMNt0I3yM0DRLiwB8A"
            :load-recaptcha-script="true"
            @verify="validate"
          ></VueRecaptcha>
        </div>
        <button
          v-if="formularios.email != null"
          class="btn btn-primary"
          type="submit"
          style="width: 30%"
        >
          Enviar Solicitud
        </button>
      </div>
    </form>

    <br />
    <b-modal
      v-if="formularios && formularios.modal && formularios.modal.activo"
      id="modal-formulario"
      :key="'modal-formulario'"
      :title="formularios.modal.titulo"
      :ok-title="'Aceptar'"
      scrollable
      size="lg"
      ok-only
    >
      <b-container v-html="formularios.modal.contenido"></b-container>
    </b-modal>
  </div>
</template>

<script>
//import Login from '@/router/auth.vue';
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Swal from "sweetalert2";
import axios from "axios";
import VueFormGenerator from "vue-form-generator";
import "vue-form-generator/dist/vfg.css";
import VueRecaptcha from "vue-recaptcha";
export default {
  components: {
    "vue-form-generator": VueFormGenerator.component,
    Loading,
    VueRecaptcha,
  },
  data() {
    return {
      correo_manual: false,
      entidad_item: {
        entidad: null,
        departamento: null,
        municipio: null,
        correo_asignacion: "",
        telefono_asignacion: "",
        personalizado: null,
      },
      fields: {},
      departamentos: [],
      municipios: [],
      entidades: [],
      personalizados: [],
      selectEntidad: false,
      edm_id: "",
      municipio_id: "",
      entidad_id: "",
      meses: [
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre",
      ],
      imagen: "",
      url: [],
      adjuntar: false,
      model: {},
      isLoading: false,
      schema: {},
      formularios: {},
      linksTotal: {},
      validar: "",
      entidad_codigo: "",
      entidadP: "",
      entidadPosicion: "",
    };
  },
  mounted() {
    // this.$bvModal.show("modal-formulario");
    this.fetch_formulario();
    this.fetch_entidades();
  },
  methods: {
    //  FETCH data de modelos -------------------------------------------

    fetch_formulario() {
      let contenido = {};
      axios
        .get(
          `${process.env.VUE_APP_URL}/formularios/url/${this.$route.params.formulario_url}`,
        )
        .then((response) => {
          this.formularios = response.data;
          contenido = JSON.parse(this.formularios.contenido);
          this.schema = contenido.schema;
          this.model = VueFormGenerator.schema.createDefaultObject(this.schema);
          this.fieldTotal = contenido.schema.fields;
          this.i = Object.keys(this.fieldTotal).length;
          this.adjuntar = contenido.documentos;
          this.selectEntidad =
            Object.keys(this.schema.fields).find(
              (key) => this.schema.fields[key].type == "formentidades",
            ) != undefined
              ? true
              : false;
          this.linksTotal = contenido.links;
          if (this.formularios.activo == false) {
            Swal.fire({
              title: "Este formulario se encuentra Inactivo!",
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
            this.$router.push({
              name: "home",
            });
          }
          if (this.formularios.email == null) {
            Swal.fire({
              title: "Este formulario no se Encuentra Disponible!",
              text: "Falta la Configuracion del Correo",
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
            this.$router.push({
              name: "home",
            });
          }

          if (this.formularios.modal && this.formularios.modal.activo) {
            this.$nextTick().then(() => {
              this.$bvModal.show("modal-formulario");
            });
          }
        });
    },

    async fetch_departamentos(codigo) {
      return await axios
        .get(process.env.VUE_APP_URL + `/departamento/seleccion/${codigo}`)
        .then((response) => {
          this.departamentos = response.data;
        });
    },

    async fetch_municipios(codigo) {
      return await axios
        .get(process.env.VUE_APP_URL + `/municipios/seleccion/${codigo}`)
        .then((response) => {
          this.municipios = response.data;
        });
    },

    fetch_entidades() {
      axios.get(process.env.VUE_APP_URL + "/entidadN").then((response) => {
        this.entidades = response.data;
      });
    },
    // Fin FETCH data de modelos ------------------------------------------

    // onChange events  ---------------------------------------------------

    onEntidadSelectChange(entidad) {
      // Reiniciando los valores al cambiar de entidad
      this.entidad_item.departamento = null;
      this.entidad_item.municipio = null;
      this.entidad_item.personalizado = null;
      this.entidad_item.correo_asignacion = "";
      this.entidad_item.telefono_asignacion = "";

      // Solicitando la data segun el tipo de entidad
      if (entidad.tipo == "Departamento") {
        this.fetch_departamentos(entidad.codigo);
      }
      if (entidad.tipo == "Municipio") {
        this.fetch_municipios(entidad.codigo);
      }

      if (entidad.tipo == "Nacional") {
        this.$set(this.entidad_item, "correo_asignacion", entidad.email);
        this.$set(this.entidad_item, "telefono_asignacion", entidad.telefono);
        this.correo_manual = this.entidad_item.correo_asignacion ? false : true;
      } else {
        this.correo_manual = false;
      }
    },

    onDMPSelectChange(DMP) {
      if (!(this.entidad_item.entidad.tipo == "Personalizado")) {
        this.$set(this.entidad_item, "correo_asignacion", DMP.correo_dm);
        this.$set(this.entidad_item, "telefono_asignacion", DMP.telefono_dm);
      } else {
        this.$set(this.entidad_item, "correo_asignacion", DMP.email);
        this.$set(this.entidad_item, "telefono_asignacion", DMP.telefono);
      }

      this.correo_manual = this.entidad_item.correo_asignacion ? false : true;
    },
    // Fin de onChange events  ------------------------------------------

    validate(response) {
      this.validar = response;
    },
    processFile(event) {
      this.imagen = event.target.files;
    },

    formulario() {
      let vm = this;
      this.isLoading = true;
      let formData = new FormData();
      if (this.imagen != "") {
        for (var i = 0; i < this.imagen.length; i++) {
          formData.append("files", this.imagen[i]);
        }
        axios
          .post(process.env.VUE_APP_URL + "/multi-upload", formData)
          .then(function (response) {
            vm.url = response.data;
            vm.fields.url = vm.url;
            vm.formularioCompleto();
          })
          .catch(function (error) {
            vm.isLoading = false;
            if (error.response.data.message) {
              Swal.fire({
                title: "Error!",
                text: error.response.data.message,
                width: 350,
                padding: "0.5em",
                icon: "error",
              });
            } else {
              Swal.fire({
                title: "Error!",
                text: "El archivos excede el Tamaño maximo de 10mb",
                width: 350,
                padding: "0.5em",
                icon: "error",
              });
            }
            //alert(error.response);
          });
      } else {
        this.formularioCompleto();
      }
    },
    formularioCompleto() {
      const asArray = Object.entries(this.model);
      const filtered = asArray.filter(
        ([key, value]) =>
          typeof value !== "boolean" && key != "Seleccionar la dependencia",
      );
      let model_filtered = Object.fromEntries(filtered);

      if (this.selectEntidad) {
        this.fields.edm = JSON.stringify([
          this.model["Seleccionar la dependencia"],
        ]);
      }
      if (model_filtered.Correo_Solicitante) {
        this.fields.correo_solicitante = model_filtered.Correo_Solicitante;
      }
      this.fields.asunto = model_filtered.Asunto;
      this.fields.contenido = JSON.stringify(model_filtered);
      this.fields.formulario = this.formularios.id;

      if (this.validar) {
        this.isLoading = true;
        axios
          .post(process.env.VUE_APP_URL + "/solicitudes", this.fields)
          .then(() => {
            this.isLoading = false;
            Swal.fire({
              title: "Solicitud enviada Exitosamente!",
              text: "Pronto estaremos dando respuesta a tu solicitud",
              width: 350,
              padding: "0.5em",
              icon: "success",
            });
            this.fields = {};
            this.entidad_item = {
              entidad: null,
              departamento: null,
              municipio: null,
              correo_asignacion: "",
              telefono_asignacion: "",
              personalizado: null,
            };
            this.entidad_id = "";
            this.model = VueFormGenerator.schema.createDefaultObject(
              this.schema,
            );
            this.departamento = false;
            this.personalizado = false;
            this.municipio = false;
            this.entidad_codigo = "";
            this.$refs.files.value = null;
            this.edm_id = "";
            this.$refs.recaptcha.reset();
          })
          .catch(function (error) {
            this.isLoading = false;
            Swal.fire({
              title: "Error!",
              text: error.response.data.message,
              width: 350,
              padding: "0.5em",
              icon: "error",
            });
          });
      } else {
        alert("Ha fallado el Captcha, por favor intentelo de nuevo!");
        this.isLoading = false;
      }
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

.vue-form-generator * {
  /* border: none !important; */
}
</style>
