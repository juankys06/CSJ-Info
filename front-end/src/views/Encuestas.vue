<template>
  <div class="encuestas">
    <Loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></Loading>
    <span style="float: right; margin-right: 15%"
      >Buscar:
      <input
        id="searchTerm"
        type="text"
        style="border: solid; color: gray; border-radius: 5px"
        @keyup="tabla"
    /></span>
    <br />
    <br />
    <div class="row">
      <div class="btn-container" style="margin: auto; width: 60%">
        <a
          href="#EncuestaModal"
          data-toggle="modal"
          class="btn btn-lg"
          style="color: black; background-color: #eef0f2"
          @click="modal = true"
          >Agregar</a
        >
      </div>
      <table
        id="regTable"
        class="table table-striped table-hover"
        style="margin: auto; width: 60%"
      >
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">Titulo</th>
            <th scope="col">Activo</th>
            <th scope="col">URL</th>
            <th>Acciones</th>
            <th scope="col">Reporte</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(encuesta_item, index) in encuestas"
            :key="encuesta_item.id"
          >
            <th scope="row">{{ encuesta_item.id }}</th>
            <td>{{ encuesta_item.titulo }}</td>
            <td v-if="encuesta_item.activo == true">Activo</td>
            <td v-else></td>
            <td>
              <a
                :href="url + '/encuesta/' + encuesta_item.url"
                target="_blank"
                >{{ url + "/encuesta/" + encuesta_item.url }}</a
              >
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
                    v-if="encuesta_item.activo == false"
                    class="dropdown-item"
                    @click="activar(encuesta_item.id, true)"
                    >Activar</a
                  >
                  <a
                    v-else
                    class="dropdown-item"
                    @click="activar(encuesta_item.id, false)"
                    >Desactivar</a
                  >
                  <a
                    href="#PreguntasModal"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="
                      fieldsUpdate(encuesta_item, index),
                        (encuesta_id = encuesta_item.id)
                    "
                    >Agregar Preguntas</a
                  >
                  <a
                    href="#EncuestaModal"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="fieldsUpdate(encuesta_item, index)"
                    >Editar</a
                  >
                  <a
                    class="dropdown-item"
                    @click="onClickDelete(encuesta_item.id, index)"
                    >Eliminar</a
                  >
                </div>
              </div>
            </td>
            <td>
              <button
                class="btn btn-primary"
                @click="Reporte(encuesta_item.id)"
              >
                Generar
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div
      id="EncuestaModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="modal" class="modal-title">Crear Encuesta</h5>
            <h5 v-else class="modal-title">Editar Encuesta</h5>
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
              <label class="col-md-6 col-form-label">Titulo:</label>
              <div class="col-md-12">
                <input
                  v-model="fields.titulo"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>
            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Url:</label>
              <div class="col-md-12">
                <input
                  v-model="fields.url"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div v-if="modal" class="form-group row align-items-center">
              <label class="col-md-6 col-form-label"
                >Formulario al que Pertenece:</label
              >
              <div class="col-md-12">
                <select v-model="fields.formulario" class="form-control">
                  <option
                    v-for="formulario_item in formulario"
                    :key="formulario_item.id"
                    :value="formulario_item.id"
                  >
                    {{ formulario_item.descripcion }}
                  </option>
                </select>
              </div>
            </div>

            <div v-else class="form-group row align-items-center">
              <label class="col-md-6 col-form-label"
                >Formulario al que Pertenece:</label
              >
              <div class="col-md-12">
                <select
                  v-model="fields.formulario"
                  class="form-control"
                  disabled
                >
                  <option
                    v-for="formulario_item in formulario"
                    :key="formulario_item.id"
                    :value="formulario_item.id"
                  >
                    {{ formulario_item.descripcion }}
                  </option>
                </select>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button
              v-if="modal == true"
              data-dismiss="modal"
              class="btn btn-success"
              @click="create()"
            >
              Crear
            </button>
            <button
              v-if="modal == false"
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-success"
              @click="update()"
            >
              Editar
            </button>

            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-default"
              @click="fields = {}"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>

    <div
      id="PreguntasModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="modal" class="modal-title">Agregar Preguntas</h5>
            <h5 v-else class="modal-title">Editar</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div :key="actualizar" class="modal-body">
            <div
              v-for="pregunta_item in pregunta"
              :key="pregunta_item.id"
              class="custom-control custom-checkbox"
            >
              <input
                :id="pregunta_item.id"
                v-model="preguntas"
                type="checkbox"
                class="custom-control-input"
                :value="pregunta_item.id"
              />
              <label class="custom-control-label" :for="pregunta_item.id">{{
                pregunta_item.texto
              }}</label>
            </div>
          </div>
          <div class="modal-footer">
            <button
              v-if="modal == true"
              class="btn btn-success"
              @click="AgregarPreguntas()"
            >
              Agregar
            </button>
            <button
              v-if="modal == false"
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-success"
              @click="AgregarPreguntas()"
            >
              Editar
            </button>

            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-default"
              @click="preguntas = []"
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
import Login from "@/router/auth.vue";
import axios from "axios";
import Swal from "sweetalert2";
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
export default {
  components: {
    Loading,
  },
  data() {
    return {
      encuestas: [],
      fields: {},
      encuesta_id: "",
      modal: true,
      actualizar: "",
      pregunta: [],
      preguntas: [],
      isLoading: false,
      formulario: [],
      url: window.location.origin,
    };
  },
  mounted() {
    let vm = this;
    axios.get(process.env.VUE_APP_URL + "/encuestas").then(function (response) {
      vm.encuestas = response.data;
    });

    axios.get(process.env.VUE_APP_URL + "/preguntas").then(function (response) {
      vm.pregunta = response.data;
    });

    axios
      .get(process.env.VUE_APP_URL + "/formularios")
      .then(function (response) {
        vm.formulario = response.data;
      });
  },
  methods: {
    Reporte(id) {
      this.$router.push({
        name: "reportes",
        params: {
          Id: id,
          tipo: "encuesta", // or anything you want
        },
      });
    },
    tabla() {
      var tableReg = document.getElementById("regTable");

      var searchText = document
        .getElementById("searchTerm")
        .value.toLowerCase();

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
    create() {
      this.isLoading = true;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("titulo", this.fields.titulo);
      formData.append("url", this.fields.url);
      formData.append("formulario", this.fields.formulario);
      let vm = this;

      axios
        .post(process.env.VUE_APP_URL + "/encuestas", formData, config)
        .then(function (response) {
          const encuesta = response.data;
          vm.encuestas.push(encuesta);
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Encuesta Creada Exitosamente.!",
            width: 350,
            padding: "0.5em",
            icon: "success",
          });
        })
        .catch(function (error) {
          vm.isLoading = false;
          alert(error);
        });
    },
    activar(id, valor) {
      let vm = this;
      this.isLoading = true;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("activo", valor);

      axios
        .put(process.env.VUE_APP_URL + "/encuestas/" + id, formData, config)
        .then(function (response) {
          vm.isLoading = false;
          const encuesta = response.data;
          vm.encuestas[vm.actualizar] = encuesta;
          if (valor) {
            Swal.fire({
              title: "Encuesta Activada.!",
              width: 350,
              padding: "0.5em",
              icon: "success",
            });
          } else {
            Swal.fire({
              title: "Encuesta Desactivada.!",
              width: 350,
              padding: "0.5em",
              icon: "success",
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
    update() {
      this.isLoading = true;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("titulo", this.fields.titulo);
      formData.append("url", this.fields.url);
      formData.append("formulario", this.fields.formulario);

      let vm = this;
      axios
        .put(
          process.env.VUE_APP_URL + "/encuestas/" + this.encuesta_id,
          formData,
          config,
        )
        .then(function (response) {
          vm.isLoading = false;
          const encuesta = response.data;
          vm.encuestas[vm.actualizar] = encuesta;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Encuesta Actualizada Existosamente.!",
            width: 350,
            padding: "0.5em",
            icon: "success",
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
    fieldsUpdate(en, index) {
      this.fields.titulo = en.titulo;
      this.fields.url = en.url;
      this.fields.formulario = en.formulario.id;
      this.encuesta_id = en.id;
      this.modal = false;
      this.actualizar = index;

      for (var i = 0; i < en.preguntas.length; i++) {
        this.preguntas[i] = en.preguntas[i].id;
      }
    },
    onClickDelete(id, index) {
      var config = {
        headers: { Authorization: "Bearer " + Login.methods.token },
      };

      Swal.fire({
        title: "¿Estás seguro que deseas eliminar?",
        text: "¡No podrás revertir esto!",
        type: "warning",
        width: 350,
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Si, Eliminar!",
      }).then((result) => {
        if (result.value) {
          axios
            .delete(process.env.VUE_APP_URL + "/encuestas/" + id, config)
            .then(() => {
              this.encuestas.splice(index, 1);
              Swal.fire({
                title: "Eliminada",
                text: "Encuesta Eliminada",
                width: 350,
                padding: "0.5em",
                icon: "success",
              });
            })
            .catch(function () {
              // handle error
              Swal.fire("Error!", "Contacte el Administrador!", "error");
            });
        }
      });
    },
    AgregarPreguntas() {
      this.isLoading = true;
      let vm = this;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      for (var i = 0; i < this.preguntas.length; i++) {
        formData.append("preguntas", this.preguntas[i]);
      }
      axios
        .put(
          process.env.VUE_APP_URL + "/encuestas/" + this.encuesta_id,
          formData,
          config,
        )
        .then(function () {
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Las preguntas fueron Agregadas Exitosamente.!",
            width: 350,
            padding: "0.5em",
            icon: "success",
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
</style>
