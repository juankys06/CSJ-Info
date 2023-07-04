<template>
  <div class="AsignarRoles">
    <Loading
      :active.sync="isLoading"
      loader="dots"
      height="120"
      width="120"
      color="#19325B"
    ></Loading>
    <span style="float: right; margin-right: 15%"
      ><input
        id="searchTerm"
        v-model="email"
        type="text"
        style="border: solid; color: gray; border-radius: 5px"
        @keyup="tabla"
      />&nbsp;<button
        class="btn btn-success"
        style="background-color: #19325b"
        @click="buscar"
      >
        Buscar
      </button></span
    >

    <br />
    <br />
    <div class="row">
      <table
        id="regTable"
        class="table table-striped table-hover"
        style="margin: auto; width: 60%"
      >
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">Nombre de Usuario</th>
            <th scope="col">Correo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.userid">
            <th scope="row">{{ user.userid }}</th>
            <td>{{ user.screenname }}</td>
            <td>{{ user.emailaddress }}</td>
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
                  <a class="dropdown-item" @click="Agregar(user.userid)"
                    >Agregar usuario</a
                  >
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
      id="Asignar"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Asignar Rol</h5>
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
              v-for="role in roles"
              :key="role.id"
              class="custom-control custom-checkbox"
            >
              <input
                :id="role.id"
                v-model="Rol"
                type="checkbox"
                class="custom-control-input"
                :value="role.id"
              />
              <label class="custom-control-label" :for="role.id">{{
                role.descripcion
              }}</label>
            </div>
          </div>
          <div class="modal-footer">
            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-success"
              @click="Asignar()"
            >
              Asignar
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
  </div>
</template>

<script>
import Login from "@/router/auth.vue";
import axios from "axios";
import Swal from "sweetalert2";
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Vue from "vue";
import Paginate from "vuejs-paginate";
Vue.component("paginate", Paginate);
export default {
  components: {
    Loading,
  },
  data() {
    return {
      entidad: [],
      nombre: "",
      fields: {},
      entidad_id: "",
      modal: true,
      actualizar: "",
      idUser: 0,
      Rol: [],
      users: [],
      usersPag: [],
      parPage: 10,
      currentPage: 1,
      pag: true,
      body: true,
      roles: [],
      email: "",
      isLoading: false,
    };
  },
  computed: {
    getItems: function () {
      let current = this.currentPage * this.usersPag.size;
      let start = current - this.usersPag.size;
      return this.users.slice(start, current);
    },
    getPageCount: function () {
      return Math.ceil(this.usersPag.totalElements / this.usersPag.size);
    },
  },
  mounted() {
    let vm = this;
    axios.get(process.env.VUE_APP_URL + "/ousers").then(function (response) {
      vm.users = response.data.content;
      vm.usersPag = response.data;
    });

    axios.get(process.env.VUE_APP_URL + "/roles").then(function (response) {
      vm.roles = response.data;
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
    buscar() {
      let vm = this;
      this.isLoading = true;
      if (this.email != "") {
        axios
          .get(process.env.VUE_APP_URL + "/ousers?email=" + this.email)
          .then(function (response) {
            vm.isLoading = false;
            if (response.data.content != "") {
              vm.users = response.data.content;
              vm.usersPag = response.data;
            }
          });
      }
    },
    Agregar(idUser) {
      this.isLoading = true;
      let vm = this;
      this.fields.header = { Authorization: "Bearer " + Login.methods.token };
      //let vm = this;
      axios
        .post(
          process.env.VUE_APP_URL + "/users?usuarios=" + idUser,
          this.fields,
        )
        .then(function () {
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Usuario Agregado  Exitosamente",
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
    fieldsUpdate(enti, index) {
      this.fields.nombre = enti.nombre;
      this.entidad_id = enti.codigo;
      this.modal = false;
      this.actualizar = index;
    },
    clickCallback: function (pageNum) {
      let vm = this;

      this.currentPage = Number(pageNum);
      axios
        .get(process.env.VUE_APP_URL + "/ousers?page=" + pageNum)
        .then(function (response) {
          vm.users = response.data.content;
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
