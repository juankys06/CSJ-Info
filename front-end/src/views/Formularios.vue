<template>
  <div class="formularios">
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
        id="search"
        type="text"
        style="border: solid; color: gray; border-radius: 5px"
        autocomplete="ignorar"
        :readonly="readonly"
        @keyup="tabla"
        @click="readonly = false"
    /></span>
    <br />
    <br />
    <div class="row">
      <div class="btn-container" style="margin: auto; width: 60%">
        <a class="btn btn-lg" style="color: black; background-color: #eef0f2"
          ><router-link style="" to="/nuevoformularios">Agregar</router-link></a
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
            <th scope="col">Descripción</th>
            <th scope="col">Url</th>
            <th scope="col">Activo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(formulario, index) in formulario"
            :key="formulario.codigo"
          >
            <th scope="row">{{ formulario.id }}</th>
            <td>{{ formulario.descripcion }}</td>
            <td>
              <a
                :href="url + '/formulario/' + formulario.url"
                target="_blank"
                >{{ url + "/formulario/" + formulario.url }}</a
              >
            </td>
            <td v-if="formulario.activo == true">Activo</td>
            <td v-else></td>
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
                    v-if="formulario.activo == false"
                    class="dropdown-item"
                    @click="update(formulario.id, true)"
                    >Activar</a
                  >
                  <a
                    v-else
                    class="dropdown-item"
                    @click="update(formulario.id, false)"
                    >Desactivar</a
                  >
                  <a
                    v-if="!formulario.email"
                    data-toggle="modal"
                    href="#asignar"
                    class="dropdown-item"
                    @click="idFormulario = formulario.id"
                    >Asignar Correo</a
                  >
                  <a
                    v-else
                    data-toggle="modal"
                    href="#asignar"
                    class="dropdown-item"
                    @click="actualizarCorreo(index), (varEditar = true)"
                    >Editar Correo</a
                  >
                  <a class="dropdown-item" @click="fieldsUpdate(formulario)"
                    >Editar</a
                  >
                  <a class="dropdown-item" @click="onClickDelete()">Eliminar</a>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div
      id="asignar"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="!varEditar" class="modal-title">Asignar Correo</h5>
            <h5 v-if="varEditar" class="modal-title">Editar Correo</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div :key="cont" class="modal-body">
            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Correo:</label>
              <div class="col-md-12">
                <input
                  v-model="fields.email"
                  type="email"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Contraseña:</label>
              <div class="col-md-12">
                <input
                  v-model="fields.emailPassword"
                  type="password"
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
              v-if="!varEditar"
              data-dismiss="modal"
              class="btn btn-success"
              @click="Asignar(true)"
            >
              Asignar
            </button>
            <button
              v-if="varEditar"
              data-dismiss="modal"
              class="btn btn-success"
              @click="Asignar(false)"
            >
              Editar
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
      formulario: [],
      readonly: true,
      nombre: "",
      fields: {},
      entidad_id: "",
      modal: true,
      actualizar: "",
      idFormulario: "",
      varEditar: false,
      isLoading: false,
      cont: 0,
      url: window.location.origin,
    };
  },
  mounted() {
    this.fields.email = "@cendoj.ramajudicial.gov.co";
    let vm = this;
    axios
      .get(process.env.VUE_APP_URL + "/formularios")
      .then(function (response) {
        vm.formulario = response.data;
      });
  },
  methods: {
    tabla() {
      var tableReg = document.getElementById("regTable");

      var searchText = document.getElementById("search").value.toLowerCase();

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
    actualizarCorreo(id) {
      this.cont++;
      this.fields.email = this.formulario[id].email;

      this.idFormulario = this.formulario[id].id;
      this.actualizar = id;
      this.cont++;
    },

    Asignar(valor) {
      this.isLoading = true;
      let vm = this;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("email", this.fields.email);
      formData.append("emailPassword", this.fields.emailPassword);

      axios
        .put(
          process.env.VUE_APP_URL + "/formularios/" + this.idFormulario,
          formData,
          config,
        )
        .then(function (response) {
          vm.isLoading = false;
          const formularios = response.data;
          vm.formulario[vm.actualizar] = formularios;
          if (valor) {
            Swal.fire({
              title: "Correo y Configuracion Asignado",
              width: 350,
              padding: "0.5em",
              icon: "success",
            });
          } else {
            Swal.fire({
              title: "Correo y Configuracion Editado",
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

    update(id, valor) {
      this.isLoading = true;
      let vm = this;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("activo", valor);

      axios
        .put(process.env.VUE_APP_URL + "/formularios/" + id, formData, config)
        .then(function () {
          vm.isLoading = false;
          if (valor) {
            Swal.fire({
              title: "Formulario Activo",
              width: 350,
              padding: "0.5em",
              icon: "success",
            });
          } else {
            Swal.fire({
              title: "Formulario Desactivado",
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
    fieldsUpdate(data) {
      this.$router.push({
        name: "editarformularios",
        params: {
          formulario: data, // or anything you want
        },
      });
    },
    onClickDelete() {
      // var config = {
      //    headers: {'Authorization': 'Bearer '+ Login.methods.token}
      //};

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
          Swal.fire({
            text: "Para eliminar un formulario cotacte al Administrador ",
            width: 350,
            padding: "0.5em",
            icon: "warning",
          });

          /*axios.delete(process.env.VUE_APP_URL+'/formularios/'+id,config).then(() => {
                        this.formulario.splice(index, 1);
                        Swal.fire({
                          title: 'Eliminado',
                          text: 'Formulario Eliminado',
                          width: 350,
                          padding: '0.5em',
                          icon:'success'
                        })

                    }).catch(function () {
                          // handle error
                          Swal.fire(
                                  'Error!',
                                  'Contacte el Administrador!',
                                  'error'
                          );
                    }); */
        }
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
