<template>
  <div class="pregunta">
    <Loading
      :active.sync="isLoading"
      loader="dots"
      height="120"
      width="120"
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
          href="#PreguntaModal"
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
            <th scope="col">Pregunta</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(pregunta, index) in pregunta" :key="pregunta.id">
            <th scope="row">{{ pregunta.id }}</th>
            <td>{{ pregunta.texto }}</td>
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
                    href="#PreguntaModal"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="fieldsUpdate(pregunta, index)"
                    >Editar</a
                  >
                  <a
                    class="dropdown-item"
                    @click="onClickDelete(pregunta.id, index)"
                    >Eliminar</a
                  >
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div
      id="PreguntaModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="modal" class="modal-title">Crear Pregunta</h5>
            <h5 v-else class="modal-title">Editar Pregunta</h5>
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
              <label class="col-md-6 col-form-label">Pregunta:</label>
              <div class="col-md-12">
                <input
                  v-model="fields.pregunta"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>
            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Respuestas:</label>
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
          </div>
          <div class="modal-footer">
            <button
              v-if="modal == true"
              data-dismiss="modal"
              aria-label="Close"
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
              @click="clean()"
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
import Login from "@/router/auth.vue";
import axios from "axios";
import Swal from "sweetalert2";
export default {
  components: {
    Loading,
  },
  data() {
    return {
      pregunta: [],
      fields: {},
      pregunta_id: "",
      modal: true,
      actualizar: "",
      cont: 0,
      value: {},
      isLoading: false,
    };
  },
  mounted() {
    let vm = this;
    axios.get(process.env.VUE_APP_URL + "/preguntas").then(function (response) {
      vm.pregunta = response.data;
    });
  },
  methods: {
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
      let vm = this;
      this.isLoading = true;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("pregunta", this.fields.pregunta);
      for (var i = 0; i < this.cont; i++) {
        formData.append("respuestas", this.value[i + 1]);
      }

      axios
        .post(process.env.VUE_APP_URL + "/preguntas", formData, config)
        .then(function (response) {
          vm.isLoading = false;
          const preguntas = response.data;
          vm.pregunta.push(preguntas);
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Pregunta Creada Exitosamentes",
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
    update() {
      let vm = this;
      this.isLoading = true;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("pregunta", this.fields.pregunta);
      for (var i = 0; i < this.cont; i++) {
        formData.append("respuestas", this.value[i + 1]);
      }
      axios
        .put(
          process.env.VUE_APP_URL + "/preguntas/" + this.pregunta_id,
          formData,
          config,
        )
        .then(function () {
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Pregunta Actualizada Exitosamentes",
            width: 350,
            padding: "0.5em",
            icon: "success",
          });
          vm.clean();
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
    fieldsUpdate(pre, index) {
      this.fields.pregunta = pre.texto;
      this.pregunta_id = pre.id;
      this.modal = false;

      this.actualizar = index;
      for (var i = 0; i < Object.keys(pre.respuestas).length; i++) {
        this.value[i + 1] = pre.respuestas[i].texto;
        this.cont++;
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
            .delete(process.env.VUE_APP_URL + "/preguntas/" + id, config)
            .then(() => {
              this.pregunta.splice(index, 1);
              Swal.fire({
                title: "Eliminada",
                text: "Pregunta Eliminada",
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
    numeroValores() {
      this.cont = this.cont + 1;
    },
    clean() {
      this.cont = 0;
      this.value = {};
      this.fields = {};
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
