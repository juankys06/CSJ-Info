<template>
  <div class="Respuesta" style="margin: auto; width: 50%">
    <Loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></Loading>
    <form @submit.prevent="respuestaAdjunto()">
      <div class="form-group">
        <label class="col-md-4 control-label">Correo solicitante</label>

        <div class="col-md-12">
          <input
            id="email"
            v-model="fields.correo_solicitante"
            type="email"
            class="form-control"
            name="email"
            required
          />
        </div>
      </div>

      <div class="form-group">
        <label class="col-md-4 control-label">Asunto</label>

        <div class="col-md-12">
          <input
            id="asunto"
            v-model="fields.asunto"
            type="text"
            class="form-control"
            name="asunto"
            required
          />
        </div>
      </div>

      <div class="form-group">
        <label class="col-md-4 control-label">CC</label>

        <div class="col-md-12">
          <input
            id="cc"
            v-model="fields.cc"
            type="text"
            class="form-control"
            name="cc"
          />
        </div>
      </div>

      <div class="form-group">
        <label class="col-md-4 control-label"
          >Mis Respuestas Predeterminadas</label
        >

        <div class="col-md-12">
          <select
            v-model="respuesta_id"
            class="form-control"
            @change="actualizarRespuesta()"
          >
            <option
              v-for="resp in respuestas"
              :key="resp.id"
              :value="{
                id: resp.id,
                titulo: resp.titulo,
                contenido: resp.contenido_respuesta,
              }"
            >
              {{ resp.titulo }}
            </option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <label class="col-md-4 col-form-label">Respuesta:</label>
        <div class="col-md-12">
          <ckeditor
            v-model="fields.respuesta"
            :editor="editor"
            :config="editorConfig"
          ></ckeditor>
        </div>
      </div>

      <div class="form-group">
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
          </div>
        </div>
      </div>

      <div>
        <label class="col-md-4 col-form-label">Archivos adjuntos:</label>
        <div id="archivos"></div>
      </div>

      <div class="form-group buttons_class">
        <button type="button" class="btn btn-danger" @click="getBack">
          Regresar
        </button>
        <button type="submit" class="btn btn-primary">Enviar Respuesta</button>
      </div>
    </form>
    <br />
  </div>
</template>

<script>
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Login from "@/router/auth.vue";
import axios from "axios";
import Vue from "vue";
import CKEditor from "@ckeditor/ckeditor5-vue";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import Swal from "sweetalert2";
Vue.use(CKEditor);
export default {
  components: {
    Loading,
  },
  props: ["solicitud", "statusId"],
  data() {
    return {
      fields: {
        correo_solicitante: this.solicitud.correo_solicitante,
      },
      respuestas: {},
      respuesta_id: "",
      imagen: "",
      url: [],
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
      isLoading: false,
    };
  },
  created() {
    this.fields.asunto = this.solicitud.asunto;
  },
  mounted() {
    if (!this.solicitud) {
      this.$router.go(-1);
    }
    this.date = new Date();

    axios
      .get(process.env.VUE_APP_URL + "/respuestausuario")
      .then((response) => {
        this.respuestas = response.data;
      });
  },
  methods: {
    processFile(event) {
      this.imagen = event.target.files;

      var list = document.getElementsByClassName("adjuntos");
      for (var j = list.length - 1; 0 <= j; j--) {
        if (list[j] && list[j].parentElement)
          list[j].parentElement.removeChild(list[j]);
      }

      for (var i = 0; i < this.imagen.length; i++) {
        var urls = URL.createObjectURL(event.target.files[i]);
        var tipo = event.target.files[i].type.substr(0, 5);
        if (tipo == "image") {
          document.getElementById("archivos").innerHTML +=
            '<div class="adjuntos" id="adjunto-' +
            i +
            '"><img style="height:150px; width:150px;" src="' +
            urls +
            '"> ';
        } else {
          document.getElementById("archivos").innerHTML +=
            '<div class="adjuntos" id="adjunto-' +
            i +
            '"><label >' +
            event.target.files[i].name +
            "</label></div>";
        }
      }
    },
    actualizarRespuesta() {
      this.fields.respuesta = "";
      this.fields.respuesta = this.respuesta_id.contenido;
    },
    respuestaAdjunto() {
      let vm = this;
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
            vm.respuesta(true);
          })
          .catch(function (error) {
            alert(error.response.data.message);
          });
      } else {
        this.respuesta(false);
      }
    },

    respuesta(urlExiste) {
      this.isLoading = true;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("correo_solicitante", this.fields.correo_solicitante);
      formData.append("asunto", this.fields.asunto);
      formData.append("cc", this.fields.cc);
      formData.append("respuesta", this.fields.respuesta);
      formData.append("statusId", this.statusId);

      if (urlExiste) formData.append("url", this.fields.url);

      axios
        .post(
          process.env.VUE_APP_URL +
            "/solicitudes/" +
            this.solicitud.id +
            "/replica",
          formData,
          config,
        )
        .then(() => {
          this.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Respuesta Enviada Exitosamente",
            width: 350,
            padding: "0.5em",
            icon: "success",
          });
          this.$router.push({
            name: "solicitudes",
            replace: true,
            params: {
              formulario_url: this.$route.params.formulario_url, // or anything you want
            },
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

    getBack() {
      this.$router.push({
        name: "solicitudes",
        params: { formulario_url: this.$route.params.formulario_url },
      });
    },
  },
};
</script>

<style>
.ck-editor__editable {
  min-height: 200px;
}

.buttons_class {
  display: flex;
  flex-flow: row wrap;
  justify-content: space-between;
}
</style>
