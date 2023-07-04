<template>
  <div class="FormularioNuevo" style="margin: auto; width: 50%">
    <loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></loading>
    <div class="form-group">
      <label class="col-md-4 control-label">Descripcion del Formulario</label>

      <div class="col-md-12">
        <input
          v-model="formularioFinal.descripcion"
          type="text"
          class="form-control"
          name="nombre"
          required
        />
      </div>

      <label class="col-md-4 control-label">Url del Formulario</label>

      <div class="col-md-12">
        <input
          v-model="formularioFinal.url"
          type="text"
          class="form-control"
          name="nombre"
          placeholder="No se permiten caracteres especiales "
          pattern="[A-Za-z0-9]"
          required
        />
      </div>

      <label class="col-md-4 col-form-label"
        >Seccione el tipo de atributo a agregar:</label
      >
      <div class="col-md-12">
        <select
          v-model="field.type"
          class="form-control content-select"
          name="select"
        >
          <option value="input">
            Campo (Texto,Email,Contraseña,Numerico,Fecha)
          </option>
          <option value="label">Etiqueta</option>
          <option value="textArea">Caja de Texto</option>
          <option value="formcheckbox">Casilla de verificación</option>
          <option value="select">Seleccion</option>
          <option value="switch">Interruptor</option>
          <option value="radios">Radios</option>
          <option value="checklist">Lista de comprobación</option>
          <option value="formlink">Link</option>
        </select>
      </div>
      <button
        v-if="field.type"
        class="btn btn-success"
        data-toggle="modal"
        href="#agregar"
      >
        Agregar
      </button>
      <div class="form-group row align-items-center">
        <label class="col-md-4 col-form-label">¿Archivos Adjuntos?</label>
        <label class="switch">
          <input
            v-model="adjuntar"
            type="checkbox"
            @click="adjuntar = $event.target.checked"
          />
          <span class="slider round"></span>
        </label>
      </div>
      <div class="form-group row align-items-center">
        <label class="col-md-4 col-form-label">Seleccion de entidad</label>
        <label class="switch">
          <input
            v-model="selectEntidad"
            type="checkbox"
            @click="toggleEntidad($event.target.checked)"
          />
          <span class="slider round"></span>
        </label>
      </div>
    </div>

    <br />
    <div class="form-group">
      <label class="col-md-4 col-form-label"
        >Vista Previa del Formulario:</label
      >
      <button v-if="banEditar" class="btn btn-success" @click="editar1()">
        Editar Atributos
      </button>
      <button v-else class="btn btn-success" @click="editar1()">
        Deshabilitar
      </button>
      &nbsp;
      <button v-if="banOrdenar" class="btn btn-success" @click="ordenar()">
        Ordenar
      </button>
      <button v-else class="btn btn-success" @click="ordenar()">
        Deshabilitar
      </button>
      <br />
      <br />
      <vue-form-generator :schema="schema" :model="model"></vue-form-generator>

      <div :key="j" class="form-group">
        <div
          v-for="(link, index) in linksTotal"
          :id="'link' + index"
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
          <div class="col-md-12">
            <input
              id="files"
              ref="files"
              type="file"
              multiple
              @change="processFile($event)"
            />
            <br />
            Tamaño maximo de archivos 5MB
          </div>
        </div>
      </div>
    </div>
    <br />
    <button
      v-if="i != 0 && $route.name != 'editarformularios'"
      class="btn btn-success"
      @click="create()"
    >
      Crear
    </button>
    <button
      v-if="i != 0 && $route.name == 'editarformularios'"
      class="btn btn-success"
      @click="update()"
    >
      Guardar
    </button>

    <div
      id="agregar"
      class="modal fade"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="!varEditar" class="modal-title">
              Agregar {{ field.type }}
            </h5>
            <h5 v-if="varEditar" class="modal-title">
              Editar {{ field.type }}
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
            <div
              v-if="field.type == 'input'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">tipo:</label>
              <div class="col-md-12">
                <select v-model="field.inputType" class="form-control">
                  <option value="text">Texto</option>
                  <option value="email">Email</option>
                  <option value="password">Contraseña</option>
                  <option value="number">Numerico</option>
                  <option value="url">URL</option>
                  <option value="date">Fecha</option>
                </select>
              </div>
            </div>
            <div
              v-if="field.type != 'formlink'"
              class="form-group row align-items-center"
            >
              <label
                v-if="field.type != 'label'"
                class="col-md-6 col-form-label"
                >Nombre:</label
              >
              <label v-else class="col-md-6 col-form-label">Texto:</label>
              <div class="col-md-12">
                <input
                  v-model="field.label"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>
            <div v-else class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Nombre:</label>
              <div class="col-md-12">
                <input
                  v-model="field.inputName"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div
              v-if="field.type == 'formlink'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Texto:</label>
              <div class="col-md-12">
                <input
                  v-model="field.texto"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div
              v-if="field.type == 'formlink'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Prefijo:</label>
              <div class="col-md-12">
                <input
                  v-model="field.prefijo"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div
              v-if="field.type == 'formlink'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Sufijo:</label>
              <div class="col-md-12">
                <input
                  v-model="field.sufijo"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div
              v-if="field.type == 'formlink'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Url:</label>
              <div class="col-md-12">
                <input
                  v-model="field.href"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div
              v-if="
                field.type != 'formlink' &&
                field.type != 'label' &&
                field.type != 'formcheckbox'
              "
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Descripcion:</label>
              <div class="col-md-12">
                <input
                  v-model="field.placeholder"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>
            <div
              v-else-if="field.type == 'formcheckbox'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Texto:</label>
              <b-col md="12">
                <ckeditor
                  v-model="field.placeholder"
                  :editor="editor"
                  :config="editorConfig"
                  style="height: 200px"
                  tabindex="0"
                ></ckeditor>
              </b-col>
            </div>

            <div
              v-if="field.type != 'formlink'"
              class="form-group row align-items-center"
            >
              <label class="col-md-4 col-form-label">¿Campo Requerido?</label>
              <label class="switch">
                <input
                  v-model="field.required"
                  type="checkbox"
                  @click="field.required = $event.target.checked"
                />
                <span class="slider round"></span>
              </label>
            </div>

            <div
              v-if="field.type == 'select'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Valores:</label>
              <button class="btn btn-success" @click="numeroValores">
                Agregar +
              </button>
              <div
                v-for="index in cont"
                :id="index"
                :key="index"
                class="col-md-12"
              >
                <input
                  v-model="value[index]"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div
              v-if="field.type == 'radios' || field.type == 'checklist'"
              class="form-group row align-items-center"
            >
              <label class="col-md-6 col-form-label">Valores:</label>
              <button class="btn btn-success" @click="numeroValores">
                Agregar+
              </button>
              <div
                v-for="index in cont"
                :id="index"
                :key="index"
                class="col-md-12"
              >
                <input
                  v-model="value[index]"
                  type="text"
                  class="form-control"
                  required
                />
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
            <button
              v-if="!varEditar && field.type != 'formlink'"
              data-dismiss="modal"
              class="btn btn-success"
              @click="Agregar"
            >
              Agregar
            </button>
            <button
              v-if="varEditar && field.type != 'formlink'"
              data-dismiss="modal"
              class="btn btn-success"
              @click="Agregar"
            >
              Editar
            </button>
            <button
              v-if="!varEditar && field.type == 'formlink'"
              data-dismiss="modal"
              class="btn btn-success"
              @click="Agregar"
            >
              Agregar
            </button>
            <button
              v-if="varEditar && field.type == 'formlink'"
              data-dismiss="modal"
              class="btn btn-success"
              @click="Agregar"
            >
              Editar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
//import Login from '@/router/auth.vue';
import Login from "@/router/auth.vue";
import axios from "axios";
import Vue from "vue";
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Swal from "sweetalert2";
import VueFormGenerator from "vue-form-generator";
import "vue-form-generator/dist/vfg.css";

import CKEditor from "@ckeditor/ckeditor5-vue";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
Vue.use(CKEditor);

export default {
  props: ["formulario"],
  data() {
    return {
      schema: {},
      model: {},
      formularios: {},
      seleccion: "",
      field: {},
      fieldTotal: {},
      i: 0,
      j: 0,
      cont: 0,
      value: {},
      options: [],
      entidad: [],
      adjuntar: false,
      formularioFinal: {},
      selectEntidad: false,
      correo: true,
      varEditar: false,
      posEditar: 0,
      banEditar: true,
      banOrdenar: true,
      editarCont: 0,
      isLoading: false,
      links: {},
      linksTotal: {},
      radioCont: 0,
      departamentos: [],
      municipios: [],
      departamento: false,
      municipio: false,
      departamento_id: "",
      municipio_id: "",
      entidadPosicion: true,
      entidad_codigo: "",
      editor: ClassicEditor,
      editorData: "<span></span>",
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
    };
  },
  components: {
    Loading,
    "vue-form-generator": VueFormGenerator.component,
  },
  methods: {
    toggleEntidad(flag_entidad) {
      if (flag_entidad) {
        let entidad_field = {
          type: "formentidades",
          label: "Seleccionar la dependencia",
          model: "Seleccionar la dependencia",
          required: true,
          default: {
            entidad: null,
            departamento: null,
            municipio: null,
            correo_asignacion: "",
            telefono_asignacion: "",
            personalizado: null,
          },
        };
        this.fieldTotal[this.i] = entidad_field;
        let fields = this.fieldTotal;
        this.schema = { fields };
        this.i++;
      } else {
        let entidades_index = Object.keys(this.schema.fields).find(
          (key) => this.schema.fields[key].type == "formentidades",
        );

        delete this.schema.fields[entidades_index];

        let fields = [];
        let cont = 0;
        for (var i = 0; i <= Object.keys(this.schema.fields).length; i++) {
          if (this.schema.fields[i] != undefined) {
            fields[cont] = this.schema.fields[i];
            cont++;
          }
        }
        this.i = cont;
        this.schema = { fields };
        this.fieldTotal = fields;
      }
      this.model = VueFormGenerator.schema.createDefaultObject(this.schema);
    },
    actualizartipo() {
      for (var j = 0; j < this.entidad.length; j++) {
        if (this.entidad_codigo == this.entidad[j].codigo) {
          if (this.entidad[j].tipo == "Departamento") this.departamento = true;
          if (this.entidad[j].tipo == "Municipio") this.municipio = true;
        }
      }
    },
    ordenar() {
      let vm = this;
      let cn = 0;
      var idname = "";
      var contRadios = 1;
      if (this.banOrdenar) {
        for (var i = 0; i < Object.keys(this.schema.fields).length; i++) {
          if (this.schema.fields[i].type != "formlink") {
            idname = this.schema.fields[i].label;
          } else {
            idname = this.schema.fields[i].inputName;
          }
          idname = idname.toLowerCase().replaceAll(" ", "-");
          idname = idname
            .replaceAll(":", "")
            .replaceAll("ó", "")
            .replaceAll("á", "")
            .replaceAll("é", "")
            .replaceAll("ú", "")
            .replaceAll("í", "")
            .replaceAll("/", "")
            .replaceAll(".", "");
          if (this.schema.fields[i].type == "label") {
            idname = this.schema.fields[i].id.toLowerCase();
          }
          if (this.schema.fields[i].type == "radios") {
            jQuery("." + this.schema.fields[i].styleClasses).after(
              "<i style='float: right; font-size: 30px; color: #19325b; cursor: pointer;' id='subir" +
                i +
                "' class='fa fa-arrow-circle-up'></i>",
            );
            jQuery("." + this.schema.fields[i].styleClasses).after(
              "<i style='float: right; font-size: 30px; color: #19325b; cursor: pointer;' id='bajar" +
                i +
                "' class='fa fa-arrow-circle-down'></i>",
            );
          } else {
            jQuery("#" + idname).after(
              "<i style='float: right; font-size: 30px; color: #19325b; cursor: pointer;' id='subir" +
                i +
                "' class='fa fa-arrow-circle-up'></i>",
            );
            jQuery("#" + idname).after(
              "<i style='float: right; font-size: 30px; color: #19325b; cursor: pointer;' id='bajar" +
                i +
                "' class='fa fa-arrow-circle-down'></i>",
            );
          }

          jQuery("#subir" + i).on("click", function (cn) {
            vm.CambiarOrden(cn.target.id.slice(5), "subir");
          });

          jQuery("#bajar" + i).on("click", function (cn) {
            vm.CambiarOrden(cn.target.id.slice(5), "bajar");
          });
        }
        this.banOrdenar = false;
      } else {
        this.banOrdenar = true;
        for (var i = 0; i < Object.keys(this.schema.fields).length; i++) {
          jQuery("#subir" + i).remove();
          jQuery("#bajar" + i).remove();
        }
        jQuery("#subirEntidad").remove();
        jQuery("#bajarEntidad").remove();
      }
    },
    CambiarOrdenE(orden) {
      this.entidadPosicion = orden;
    },
    CambiarOrden(orden, tipo) {
      for (var i = 0; i < Object.keys(this.schema.fields).length; i++) {
        jQuery("#subir" + i).remove();
        jQuery("#bajar" + i).remove();
      }
      jQuery("#subirEntidad").remove();
      jQuery("#bajarEntidad").remove();

      if (tipo == "subir") {
        if (orden > 0) {
          let position = this.schema.fields[orden - 1];
          let position1 = this.schema.fields[orden];
          this.schema.fields[orden] = position;
          this.schema.fields[orden - 1] = position1;
          let fields = this.schema.fields;
          this.schema = { fields };
        }
      } else {
        if (orden < Object.keys(this.schema.fields).length - 1) {
          let position = this.schema.fields[Number(orden) + 1];
          let position1 = this.schema.fields[orden];
          this.schema.fields[orden] = position;
          this.schema.fields[Number(orden) + 1] = position1;
          let fields = this.schema.fields;
          this.schema = { fields };
        }
      }
      this.banOrdenar = true;
    },
    create() {
      let vm = this;
      this.isLoading = true;
      this.formularios.schema = this.schema;
      this.formularios.documentos = this.adjuntar;
      this.formularios.links = this.linksTotal;

      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("contenido", JSON.stringify(this.formularios));
      formData.append("descripcion", this.formularioFinal.descripcion);
      formData.append("url", this.formularioFinal.url);
      formData.append("activo", true);

      this.formularioFinal.contenido = JSON.stringify(this.formularios);

      axios
        .post(process.env.VUE_APP_URL + "/formularios", formData, config)
        .then(function () {
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "formulario creado exitosamente",
            width: 350,
            padding: "0.5em",
            icon: "success",
          });
          vm.field = {};
          vm.$router.push("/formularios");
          location.reload(true);
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
    update() {
      let vm = this;
      this.isLoading = true;
      this.formularios.schema = this.schema;
      this.formularios.documentos = this.adjuntar;
      this.formularios.links = this.linksTotal;

      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };
      let formData = new FormData();
      formData.append("contenido", JSON.stringify(this.formularios));
      formData.append("descripcion", this.formularioFinal.descripcion);
      formData.append("url", this.formularioFinal.url);

      this.formularioFinal.contenido = JSON.stringify(this.formularios);

      axios
        .put(
          process.env.VUE_APP_URL + "/formularios/" + this.formulario.id,
          formData,
          config,
        )
        .then(function () {
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Formulario Editado Exitosamente",
            width: 350,
            padding: "0.5em",
            icon: "success",
          });
          vm.field = {};
          vm.$router.push("/formularios");
          location.reload(true);
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
    agregarLink() {
      if (this.varEditar) {
        this.linksTotal[this.posEditar] = this.links;
        this.varEditar = false;
      } else {
        this.linksTotal[this.j] = this.links;
        this.j++;
        this.links = {};
      }
    },
    Agregar() {
      if (this.field.type != "formlink") {
        this.field.model = this.field.label;
      } else {
        this.field.model = this.field.inputName;
      }

      if (this.field.type == "input") {
        if (this.field.inputType == "email" && this.correo) {
          this.field.model = "Correo_Solicitante";
          this.correo = false;
        }
      }

      if (this.field.type == "label") {
        if (this.varEditar) {
          this.field.id = "label" + this.posEditar;
        } else {
          this.field.id = "label" + this.i;
        }
      }
      if (this.field.type == "textArea") {
        this.field.rows = 8;
        this.field.max = 4000;
        this.field.hint = "Maximo 4000 caracteres";
      }
      if (this.field.type == "checkbox") {
        this.field.default = true;
      }
      if (this.field.type == "switch") {
        this.field.textOn = "Active";
        this.field.textOff = "Inactive";
        /*this.field.multi = true
         this.field.readonly = false
         this.field.featured = false
         this.field.disabled = false
         this.field.default= true*/
      }
      if (this.field.type == "select") {
        this.field.values = Object.values(this.value);
        this.field.selectOptions = { hideNoneSelectedText: true };
      }
      if (this.field.type == "radios") {
        if (this.$route.name == "editarformularios") {
          this.radioCont = 0;
          for (var i = 0; i < Object.keys(this.schema.fields).length; i++) {
            if (this.schema.fields[i].type == "radios") {
              this.radioCont++;
            }
          }
          this.field.values = Object.values(this.value);
          this.field.styleClasses = "radio-" + this.radioCont;
          this.radioCont++;
        } else {
          this.field.values = Object.values(this.value);
          this.field.styleClasses = "radio-" + this.radioCont;
          this.radioCont++;
        }
      }
      if (this.field.type == "checklist") {
        this.field.values = Object.values(this.value);
      }

      if (this.varEditar) {
        this.fieldTotal[this.posEditar] = this.field;
        let fields = this.fieldTotal;
        this.schema = { fields };
        this.varEditar = false;
      } else {
        this.fieldTotal[this.i] = this.field;
        let fields = this.fieldTotal;
        this.schema = { fields };
        this.i++;
      }

      this.field = {};
      this.cont = 0;
      this.field.required = false;
    },
    numeroValores() {
      this.cont = this.cont + 1;
    },
    eliminar(data, p, tipo) {
      if (tipo == "formulario") {
        delete this.schema.fields[p];
        let fields = [];
        let cont = 0;
        for (var i = 0; i <= Object.keys(this.schema.fields).length; i++) {
          if (this.schema.fields[i] != undefined) {
            fields[cont] = this.schema.fields[i];
            cont++;
          }
        }
        this.i = cont;
        this.schema = { fields };
        this.fieldTotal = fields;
        this.editarCont = 0;

        this.banEditar = true;
      } else {
        delete this.linksTotal[p];
        let fields = [];
        let cont = 0;
        for (var i = 0; i <= Object.keys(this.linksTotal).length; i++) {
          if (this.linksTotal[i] != undefined) {
            fields[cont] = this.linksTotal[i];
            cont++;
          }
        }
        this.linksTotal = fields;
      }
      var list = document.getElementsByClassName("botons");
      for (var i = list.length - 1; 0 <= i; i--)
        if (list[i] && list[i].parentElement)
          list[i].parentElement.removeChild(list[i]);
    },
    editar1() {
      let vm = this;
      let cn = 0;
      var idname = "";
      var cont = 0,
        contRadios = 1;
      if (this.banEditar) {
        for (
          var i = this.editarCont;
          i < Object.keys(this.schema.fields).length;
          i++
        ) {
          if (this.schema.fields[i].type != "formlink") {
            idname = this.schema.fields[i].label;
          } else {
            idname = this.schema.fields[i].inputName;
          }
          idname = idname.toLowerCase().replaceAll(" ", "-");
          idname = idname
            .replaceAll(":", "")
            .replaceAll("ó", "")
            .replaceAll("á", "")
            .replaceAll("é", "")
            .replaceAll("ú", "")
            .replaceAll("í", "")
            .replaceAll("/", "")
            .replaceAll(".", "");
          if (this.schema.fields[i].type == "label") {
            idname = this.schema.fields[i].id.toLowerCase();
          }
          if (this.schema.fields[i].type == "radios") {
            jQuery("." + this.schema.fields[i].styleClasses).after(
              "<button class='btn btn-success botons' style='float: right;' id='eliminar" +
                i +
                "' >Eliminar</button>",
            );
            jQuery("." + this.schema.fields[i].styleClasses).after(
              "<button class='btn btn-success botons' style='float: right;' id='editar" +
                i +
                "' >Editar</button>",
            );
          } else if (this.schema.fields[i].type != "formentidades") {
            jQuery("#" + idname).after(
              "<button class='btn btn-success botons' style='float: right;' id='eliminar" +
                i +
                "' >Eliminar</button>",
            );
            jQuery("#" + idname).after(
              "<button class='btn btn-success botons' style='float: right;' id='editar" +
                i +
                "' >Editar</button>",
            );
          }

          jQuery("#eliminar" + i).on("click", function (cn) {
            vm.eliminar(
              vm.schema.fields[cn.target.id.slice(8)],
              cn.target.id.slice(8),
              "formulario",
            );
          });

          jQuery("#editar" + i).on("click", function (cn) {
            vm.editar(
              vm.schema.fields[cn.target.id.slice(6)],
              cn.target.id.slice(6),
              "formulario",
            );
          });
          cont++;
        }

        if (Object.keys(this.linksTotal).length != 0) {
          for (var i = 0; i < Object.keys(this.linksTotal).length; i++) {
            jQuery("#link" + i).after(
              "<button class='btn botons' style='float: right; border: 1px solid #ccc; border-radius: 4px;' id='eliminarlink" +
                i +
                "' >Eliminar</button>",
            );
            jQuery("#link" + i).after(
              "<button class='btn botons' style='float: right; border: 1px solid #ccc; border-radius: 4px;' id='editarlink" +
                i +
                "' >Editar</button>",
            );

            jQuery("#eliminarlink" + i).on("click", function (cn) {
              vm.eliminar(
                vm.linksTotal[cn.target.id.slice(12)],
                cn.target.id.slice(12),
                "link",
              );
            });

            jQuery("#editarlink" + i).on("click", function (cn) {
              vm.editar(
                vm.linksTotal[cn.target.id.slice(10)],
                cn.target.id.slice(10),
                "link",
              );
            });
          }
        }

        this.banEditar = false;
      } else {
        this.banEditar = true;
        for (var i = 0; i < Object.keys(this.schema.fields).length; i++) {
          jQuery("#eliminar" + i).remove();
          jQuery("#editar" + i).remove();
        }

        for (var i = 0; i < Object.keys(this.linksTotal).length; i++) {
          jQuery("#editarlink" + i).remove();
          jQuery("#eliminarlink" + i).remove();
        }
        // if (this.editarCont == Object.keys(this.schema.fields).length) {
        //   for (var i = 0; i < Object.keys(this.schema.fields).length; i++) {
        //     document.getElementById("editar" + i).style.visibility = "hidden";
        //     document.getElementById("eliminar" + i).style.visibility = "hidden";
        //   }
        //   for (var i = 0; i < Object.keys(this.linksTotal).length; i++) {
        //     document.getElementById("editarlink" + i).style.visibility =
        //       "hidden";
        //     document.getElementById("eliminarlink" + i).style.visibility =
        //       "hidden";
        //   }
        // }
      }
    },
    editar(data, p, tipo) {
      let vm = this;
      this.posEditar = p;
      this.varEditar = true;
      if (tipo == "formulario") {
        if (data.type == "formlink") {
          vm.field.model = data.model;
          vm.field.type = "formlink";
          vm.field.inputName = data.inputName;
          vm.field.texto = data.texto;
          vm.field.href = data.href;
          vm.field.prefijo = data.prefijo;
          vm.field.sufijo = data.sufijo;
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }

        if (data.type == "input") {
          vm.field.model = data.model;
          vm.field.type = "input";
          vm.field.inputType = data.inputType;
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
        if (data.type == "label") {
          vm.field.type = "label";
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
        if (data.type == "textArea") {
          vm.field.type = "textArea";
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
        if (data.type == "formcheckbox") {
          vm.field.type = "formcheckbox";
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
        if (data.type == "switch") {
          vm.field.type = "switch";
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
        if (data.type == "select") {
          vm.field.type = "select";
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          for (var i = 0; i < data.values.length; i++) {
            this.value[i] = data.values[i];
            this.cont++;
          }
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
        if (data.type == "radios") {
          vm.field.type = "radios";
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          for (var i = 0; i < data.values.length; i++) {
            this.value[i] = data.values[i];
            this.cont++;
          }
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
        if (data.type == "checklist") {
          vm.field.type = "checklist";
          vm.field.label = data.label;
          vm.field.placeholder = data.placeholder;
          for (var i = 0; i < data.values.length; i++) {
            this.value[i] = data.values[i];
            this.cont++;
          }
          this.$forceUpdate();
          jQuery("#agregar").modal("show");
          this.$forceUpdate();
        }
      } else {
        vm.field.type = "link";
        vm.links.texto = data.texto;
        vm.links.url = data.url;

        this.$forceUpdate();
        jQuery("#agregar").modal("show");
        this.$forceUpdate();
      }
    },
  },
  mounted() {
    let vm = this;
    if (this.$route.name == "editarformularios") {
      let contenido = {};
      axios
        .get(process.env.VUE_APP_URL + "/formularios/" + this.formulario.id)
        .then(function (response) {
          vm.formularios = response.data;
          contenido = JSON.parse(vm.formularios.contenido);
          vm.schema = contenido.schema;
          vm.model = VueFormGenerator.schema.createDefaultObject(vm.schema);
          vm.fieldTotal = contenido.schema.fields;
          vm.i = Object.keys(vm.fieldTotal).length;
          vm.formularioFinal.descripcion = vm.formularios.descripcion;
          vm.formularioFinal.url = vm.formularios.url;
          vm.adjuntar = contenido.documentos;
          vm.linksTotal = contenido.links;
          vm.j = Object.keys(vm.linksTotal).length;
          vm.formularios = {};
          vm.selectEntidad =
            Object.keys(contenido.schema.fields).find(
              (key) => contenido.schema.fields[key].type == "formentidades",
            ) != undefined
              ? true
              : false;
          vm.entidadPosicion = contenido.entidad.posicion;
        });
    }

    axios.get(process.env.VUE_APP_URL + "/entidad").then(function (response) {
      vm.entidad = response.data;
    });

    axios
      .get(process.env.VUE_APP_URL + "/municipios")
      .then(function (response) {
        vm.municipios = response.data;
      });
    axios
      .get(process.env.VUE_APP_URL + "/departamentos")
      .then(function (response) {
        vm.departamentos = response.data;
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

.vue-form-generator * {
  /* border: none; */
}
select {
  background: #fff
    url("https://www.freeiconspng.com/uploads/arrow-up-icon-23.png") no-repeat;
  background-size: 15px;
  background-position: right 10px center;
}
</style>
