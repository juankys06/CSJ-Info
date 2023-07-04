<template>
  <div class="entidades">
    <loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></loading>
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
          href="#RolesModal"
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
            <th scope="col">Descripcion</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(roles, index) in roles" :key="roles.id">
            <th scope="row">{{ roles.id }}</th>
            <td>{{ roles.descripcion }}</td>
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
                    href="#RolesModal"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="fieldsUpdate(roles, index)"
                    >Editar</a
                  >
                  <a
                    class="dropdown-item"
                    @click="onClickDelete(roles.id, index)"
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
      id="RolesModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="modal" class="modal-title">Crear Rol</h5>
            <h5 v-else class="modal-title">Editar Rol</h5>
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
              <label class="col-md-6 col-form-label"
                >Nombre o Descripción:</label
              >
              <div class="col-md-12">
                <input
                  v-model="fields.descripcion"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div
              v-for="permisos in permisos"
              :key="permisos.id"
              class="custom-control custom-checkbox"
            >
              <input
                :id="permisos.id"
                v-model="permisosRol"
                type="checkbox"
                class="custom-control-input"
                :value="permisos.id"
                @click="admSolicitudes(permisos.descripcion)"
              />
              <label class="custom-control-label" :for="permisos.id">{{
                permisos.descripcion
              }}</label>
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

    <div
      id="solicitudes"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Seccionar los Formularios a Administar</h5>
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
              v-for="formulario in formulario"
              :key="formulario.id"
              class="custom-control custom-checkbox"
            >
              <input
                :id="formulario.id + 'f'"
                v-model="formularioRol"
                type="checkbox"
                class="custom-control-input"
                :value="formulario.id"
              />
              <label class="custom-control-label" :for="formulario.id + 'f'">{{
                formulario.descripcion
              }}</label>
            </div>
          </div>
          <div class="modal-footer">
            <button data-dismiss="modal" class="btn btn-success">
              Seleccionar
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
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
import Login from "@/router/auth.vue";
import axios from "axios";
import Swal from "sweetalert2";
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
export default {
  data() {
    return {
      roles: [],
      fields: {},
      rol_id: "",
      modal: true,
      actualizar: "",
      permisos: [],
      permisosRol: [],
      formularioRol: [],
      isLoading: false,
      ban: false,
      formulario: [],
    };
  },
  components: {
    Loading,
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
    admSolicitudes(nom) {
      if (nom == "Administrador de Solicitudes")
        for (var i = 0; i < this.permisos.length; i++)
          if (this.permisos[i].descripcion == "Administrador de Solicitudes")
            if (document.getElementById("" + (i + 1)).checked)
              jQuery("#solicitudes").modal("show");
    },
    create() {
      this.fields.permisos = this.permisosRol;
      this.fields.formularios = this.formularioRol;

      this.fields.header = { Authorization: "Bearer " + Login.methods.token };
      this.isLoading = true;
      let vm = this;
      axios
        .post(process.env.VUE_APP_URL + "/roles", this.fields)
        .then(function (response) {
          vm.isLoading = false;
          const rol = response.data;
          vm.roles.push(rol);
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Rol Creado Exitosamente.!",
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
      this.fields.permisos = this.permisosRol;
      this.fields.formularios = this.formularioRol;

      this.fields.header = { Authorization: "Bearer " + Login.methods.token };
      this.isLoading = true;
      let vm = this;
      axios
        .put(process.env.VUE_APP_URL + "/roles/" + this.rol_id, this.fields)
        .then(function (response) {
          vm.isLoading = false;
          const rol = response.data;
          vm.roles[vm.actualizar] = rol;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Rol Actualizado",
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
    fieldsUpdate(role, index) {
      this.fields.descripcion = role.descripcion;
      this.rol_id = role.id;
      this.modal = false;
      this.actualizar = index;

      for (var i = 0; i < role.permisos.length; i++) {
        this.permisosRol[i] = role.permisos[i].id;
      }
    },
    clean() {
      this.fields = {};
      this.permisosRol = [];
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
            .delete(process.env.VUE_APP_URL + "/roles/" + id, config)
            .then(() => {
              this.roles.splice(index, 1);
              Swal.fire({
                title: "Eliminado",
                text: "Rol Eliminado",
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
  },
  mounted() {
    let vm = this;
    axios.get(process.env.VUE_APP_URL + "/roles").then(function (response) {
      vm.roles = response.data;
    });

    axios.get(process.env.VUE_APP_URL + "/permisos").then(function (response) {
      vm.permisos = response.data;
    });
    axios
      .get(process.env.VUE_APP_URL + "/formularios")
      .then(function (response) {
        vm.formulario = response.data;
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
</style>
