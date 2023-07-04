<template>
  <div class="entidades">
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
            <th scope="col">Reporte</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="formulario in formulario" :key="formulario.id">
            <td scope="row" @click="seleccion(formulario)">
              {{ formulario.id }}
            </td>
            <td @click="seleccion(formulario)">
              {{ formulario.descripcion }}
            </td>
            <td @click="seleccion(formulario)">{{ formulario.url }}</td>
            <td v-if="formulario.activo == true" @click="seleccion(formulario)">
              Activo
            </td>
            <td v-else @click="seleccion(formulario)"></td>
            <td>
              <button class="btn btn-primary" @click="Reporte(formulario.id)">
                Generar
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import Login from "@/router/auth.vue";
import axios from "axios";
//import Swal from 'sweetalert2'
export default {
  data() {
    return {
      formulario: [],
      nombre: "",
      fields: {},
      entidad_id: "",
      modal: true,
      actualizar: "",
      authenticated: Login.methods.check(),
      user: Login.methods.user,
    };
  },
  mounted() {
    let vm = this;
    for (var j = 0; j < this.user.permisos.length; j++) {
      if (this.user.permisos[j] == "Administrador") {
        axios
          .get(process.env.VUE_APP_URL + "/formularios")
          .then(function (response) {
            vm.formulario = response.data;
          });
      }

      if (this.user.permisos[j] == "Administrador de Solicitudes") {
        axios.get(process.env.VUE_APP_URL + "/roles").then(function (response) {
          for (var k = 0; k < Object.keys(response.data).length; k++) {
            for (var q = 0; q < vm.user.roles.length; q++) {
              if (response.data[k].id == vm.user.roles[q]) {
                if (Object.keys(response.data[k].formularios).length != 0)
                  vm.formulario = response.data[k].formularios;
              }
            }
          }
        });
      }
    }
  },
  methods: {
    Reporte(id) {
      this.$router.push({
        name: "reportes",
        params: {
          Id: id,
          tipo: "formulario", // or anything you want
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

    seleccion(form) {
      if (this.$route.query.combinar) {
        this.$router.push({
          name: "combinarSolicitudes",
          params: {
            formulario: form,
          },
        });
      } else {
        this.$router.push({
          name: "solicitudes",
          params: {
            formulario_url: form.url,
          },
        });
      }
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
