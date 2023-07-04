<template>
  <div class="entidades">
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
          href="#EntidadModal"
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
            <th scope="col">Nombre</th>
            <th scope="col">Correo de la dependencia</th>
            <th scope="col">Teléfono</th>
            <th scope="col">Tipo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(entidad, index) in entidades" :key="entidad.codigo">
            <th scope="row">{{ entidad.codigo }}</th>
            <td>{{ entidad.nombre }}</td>
            <td>{{ entidad.email }}</td>
            <td>{{ entidad.telefono }}</td>
            <td>{{ entidad.tipo }}</td>
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
                    v-if="
                      entidad.tipo == 'Departamento' ||
                      entidad.tipo == 'Municipio'
                    "
                    href="#Detalles"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="detalles(entidad)"
                    >Detalles</a
                  >
                  <a
                    v-if="entidad.tipo == 'Personalizado'"
                    href="#DetallesP"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="detalles(entidad)"
                    >Detalles</a
                  >
                  <a
                    href="#EntidadModal"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="fieldsUpdate(entidad, index)"
                    >Editar</a
                  >
                  <a
                    v-if="
                      entidad.tipo == 'Departamento' ||
                      entidad.tipo == 'Municipio'
                    "
                    href="#Asignar"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="asignarUpdate(entidad)"
                    >Asignar</a
                  >
                  <a
                    v-if="entidad.tipo == 'Personalizado'"
                    href="#EntidadModal"
                    data-toggle="modal"
                    class="dropdown-item"
                    @click="AgregarUpdate(entidad)"
                    >Agregar Entidad</a
                  >
                  <a
                    class="dropdown-item"
                    @click="onClickDelete(entidad.codigo, index)"
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
      id="DetallesP"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ nombre_entidad }}</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
              @click="clearFields"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div class="modal-body">
            <label class="col-md-12 col-form-label">{{
              "Tipo de dependencia: " + tipo
            }}</label>
            <table
              id="regTable"
              class="table table-striped table-hover"
              style="margin: auto; width: 60%"
            >
              <thead>
                <tr>
                  <th scope="col">#</th>
                  <th scope="col">Nombre</th>
                  <th scope="col">Correo de la dependencia</th>
                  <th scope="col">Teléfono</th>
                  <th scope="col">Tipo</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(entidad, index) in entidadP" :key="entidad.codigo">
                  <th scope="row">{{ entidad.codigo }}</th>
                  <td>{{ entidad.nombre }}</td>
                  <td>{{ entidad.email }}</td>
                  <td>{{ entidad.telefono }}</td>
                  <td>{{ entidad.tipo }}</td>
                  <td>
                    <div class="btn-group">
                      <button
                        type="button"
                        class="btn btn-default dropdown-toggle"
                        data-toggle="dropdown"
                      >
                        Acciones
                      </button>
                      <div
                        class="dropdown-menu"
                        aria-labelledby="dropdownMenuButton"
                      >
                        <a
                          v-if="entidad.tipo == 'Departamento'"
                          href="#Detalles"
                          data-toggle="modal"
                          class="dropdown-item"
                          @click="detalles(entidad)"
                          >Detalles</a
                        >
                        <a
                          href="#EntidadModal"
                          data-toggle="modal"
                          class="dropdown-item"
                          @click="fieldsUpdate(entidad, index)"
                          >Editar</a
                        >
                        <a
                          v-if="entidad.tipo == 'Departamento'"
                          href="#Asignar"
                          data-toggle="modal"
                          class="dropdown-item"
                          @click="asignarUpdate(entidad)"
                          >Asignar</a
                        >
                        <a
                          class="dropdown-item"
                          @click="onClickDelete(entidad.codigo, index)"
                          >Eliminar</a
                        >
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-default"
              @click="clearFields"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>

    <div
      id="EntidadModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="modal" class="modal-title">Crear dependencia</h5>
            <h5 v-else class="modal-title">Editar dependencia</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
              @click="clearFields"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div class="modal-body">
            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Nombre:</label>
              <div class="col-md-12">
                <input
                  v-model="fields.nombre"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Tipo:</label>
              <div class="col-md-12">
                <select
                  v-model="fields.tipo"
                  class="form-control"
                  required
                  @change="actualizartipo()"
                >
                  <option value="Nacional">Nacional</option>
                  <option v-if="bam" value="Departamento">Departamental</option>
                  <option v-if="bam" value="Municipio">Municipal</option>
                  <option v-if="bam" value="Personalizado">
                    Personalizado
                  </option>
                </select>
              </div>
            </div>

            <div v-if="nacional" class="form-group row align-items-center">
              <label class="col-md-6 col-form-label"
                >Correo de la dependencia:</label
              >
              <div class="col-md-12">
                <input
                  v-model="fields.email"
                  type="email"
                  class="form-control"
                  required
                />
              </div>
            </div>

            <div v-if="nacional" class="form-group row align-items-center">
              <label class="col-md-6 col-form-label"
                >Teléfono de la dependencia:</label
              >
              <div class="col-md-12">
                <input
                  v-model="fields.telefono"
                  type="text"
                  class="form-control"
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
              @click="clearFields"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>

    <div
      id="Detalles"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      data-backdrop="static"
      data-keyboard="false"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ nombre_entidad }}</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
              @click="clearFields"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div class="modal-body">
            <label class="col-md-12 col-form-label">{{
              "Tipo de Entidad: " + tipo
            }}</label>
            <div v-if="tipo == 'Departamento'">
              <label
                v-for="departamentos in departamentos"
                :key="departamentos.id_edm"
                class="col-md-12 control-label"
              >
                <b>{{ departamentos.departamento.nombre }}</b>
                <div class="btn-group" style="float: right">
                  <button
                    type="button"
                    class="btn btn-default dropdown-toggle"
                    data-toggle="dropdown"
                  >
                    Acciones
                  </button>
                  <div
                    class="dropdown-menu"
                    aria-labelledby="dropdownMenuButton"
                  >
                    <a
                      href="#Asignar"
                      data-toggle="modal"
                      class="dropdown-item"
                      @click="fieldsActualizar(departamentos)"
                      >Editar</a
                    >
                    <a
                      class="dropdown-item"
                      @click="Delete(departamentos.id_edm)"
                      >Eliminar</a
                    >
                  </div>
                </div>

                <p class="m-0">{{ departamentos.correo_dm }}</p>
                <p>{{ departamentos.telefono_dm }}</p>
              </label>
            </div>

            <div v-if="tipo == 'Municipio'">
              <label
                v-for="municipios in municipios"
                :key="municipios.id"
                class="col-md-12 control-label"
              >
                <b>{{ municipios.municipio.nombre }}</b>
                <div class="btn-group" style="float: right">
                  <button
                    type="button"
                    class="btn btn-default dropdown-toggle"
                    data-toggle="dropdown"
                  >
                    Acciones
                  </button>
                  <div
                    class="dropdown-menu"
                    aria-labelledby="dropdownMenuButton"
                  >
                    <a
                      href="#Asignar"
                      data-toggle="modal"
                      class="dropdown-item"
                      @click="fieldsActualizar(municipios)"
                      >Editar</a
                    >
                    <a class="dropdown-item" @click="Delete(municipios.id_edm)"
                      >Eliminar</a
                    >
                  </div>
                </div>
                <p class="m-0">{{ municipios.correo_dm }}</p>
                <p>{{ municipios.telefono_dm }}</p>
              </label>
            </div>
          </div>
          <div class="modal-footer">
            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-default"
              @click="clearFields"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>

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
            <h5 v-if="!fieldsActual" class="modal-title">
              {{ "Asignar " + tipo }}
            </h5>
            <h5 v-else class="modal-title">{{ "Editar " + tipo }}</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
              @click="clearFields"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div class="modal-body">
            <div
              v-if="tipo == 'Departamento'"
              class="form-group row align-items-center"
            >
              <label class="col-md-4 col-form-label">{{ tipo + ":" }}</label>
              <div class="col-md-8">
                <select v-model="id_departamento" class="form-control" required>
                  <option
                    v-for="dm in dm"
                    :key="dm.id_departamento"
                    :value="dm.id_departamento"
                  >
                    {{ dm.nombre }}
                  </option>
                </select>
              </div>
            </div>

            <div
              v-if="tipo == 'Municipio'"
              class="form-group row align-items-center"
            >
              <label class="col-md-4 col-form-label">Departamento:</label>
              <div class="col-md-8">
                <select
                  v-model="index"
                  class="form-control"
                  required
                  @change="municipiosDeparamento()"
                >
                  <option
                    v-for="(
                      departamentosMunicipio, index
                    ) in departamentosMunicipio"
                    :key="departamentosMunicipio.id_departamento"
                    :value="index"
                  >
                    {{ departamentosMunicipio.nombre }}
                  </option>
                </select>
              </div>
            </div>

            <div
              v-if="tipo == 'Municipio'"
              class="form-group row align-items-center"
            >
              <label class="col-md-4 col-form-label">{{ tipo + ":" }}</label>
              <div class="col-md-8">
                <select v-model="id_municipio" class="form-control" required>
                  <option
                    v-for="dm in dm"
                    :key="dm.id_municipio"
                    :value="dm.id_municipio"
                  >
                    {{ dm.nombre }}
                  </option>
                </select>
              </div>
            </div>
            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Correo</label>
              <div class="col-md-12">
                <input
                  v-model="fields.correo"
                  type="email"
                  class="form-control"
                  required
                />
              </div>
            </div>
            <div class="form-group row align-items-center">
              <label class="col-md-6 col-form-label">Teléfono</label>
              <div class="col-md-12">
                <input
                  v-model="fields.telefono"
                  type="text"
                  class="form-control"
                />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button
              v-if="!fieldsActual"
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-success"
              @click="asignar()"
            >
              Asignar
            </button>
            <button
              v-else
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-success"
              @click="editar()"
            >
              Editar
            </button>

            <button
              data-dismiss="modal"
              aria-label="Close"
              class="btn btn-default"
              @click="clearFields"
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
      entidades: [],
      entidadP: [],
      nombre: "",
      fields: {},
      entidad_id: "",
      modal: true,
      actualizar: "",
      isLoading: false,
      dm: [],
      tipo: "",
      id_departamento: "",
      id_municipio: "",
      departamentos: "",
      departamentosMunicipio: "",
      municipio: "",
      nombre_entidad: "",
      fieldsActual: false,
      index: "",
      entidad_codigo: "",
      bam: true,
      nacional: false,
    };
  },
  mounted() {
    let vm = this;
    axios.get(process.env.VUE_APP_URL + "/entidadN").then(function (response) {
      vm.entidades = response.data;
    });
    axios
      .get(process.env.VUE_APP_URL + "/departamentos")
      .then(function (response) {
        vm.departamentosMunicipio = response.data;
      });
  },
  methods: {
    clearFields() {
      this.fields = {};
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
    municipiosDeparamento() {
      this.dm = this.departamentosMunicipio[this.index].municipio;
    },
    AgregarUpdate(data) {
      this.modal = true;
      this.fields.entidadPadre = data.codigo;
      this.bam = false;
    },
    detalles(data) {
      let vm = this;
      this.nombre_entidad = data.nombre;
      this.tipo = data.tipo;
      this.departamento = false;
      this.municipio = false;
      this.isLoading = true;
      if (data.tipo == "Departamento") {
        this.departamento = true;
        axios
          .get(
            process.env.VUE_APP_URL + "/departamento/seleccion/" + data.codigo,
          )
          .then(function (response) {
            vm.isLoading = false;
            vm.departamentos = response.data;
          });
      }
      if (data.tipo == "Municipio") {
        this.municipio = true;
        axios
          .get(process.env.VUE_APP_URL + "/municipios/seleccion/" + data.codigo)
          .then(function (response) {
            vm.isLoading = false;
            vm.municipios = response.data;
          });
      }
      if (data.tipo == "Personalizado") {
        this.entidadP = data.entidadHijos;
        vm.isLoading = false;
      }
    },
    actualizartipo() {
      if (this.fields.tipo == "Nacional") this.nacional = true;
      else this.nacional = false;
    },
    asignarUpdate(data) {
      this.tipo = data.tipo;
      this.id_entidad = data.codigo;
      this.isLoading = true;
      let vm = this;
      if (data.tipo == "Departamento") {
        axios
          .get(process.env.VUE_APP_URL + "/departamento/" + data.codigo)
          .then(function (response) {
            vm.isLoading = false;
            vm.dm = response.data;
          });
      }
      if (data.tipo == "Municipio") {
        axios
          .get(process.env.VUE_APP_URL + "/municipios/" + data.codigo)
          .then(function (response) {
            vm.isLoading = false;
            vm.dm = response.data;
          });
      }
    },
    asignar() {
      this.isLoading = true;
      this.fieldsActual = false;
      let vm = this;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      formData.append("entidad", this.id_entidad);
      if (this.id_departamento)
        formData.append("departamento", this.id_departamento);
      if (this.id_municipio) formData.append("municipio", this.id_municipio);

      formData.append("correo", this.fields.correo);
      formData.append("telefono", this.fields.telefono);

      axios
        .post(
          process.env.VUE_APP_URL + "/entidadDepartamentoMunicipio",
          formData,
          config,
        )
        .then(function () {
          vm.isLoading = false;
          vm.fields.correo = "";
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Dependencia asignada",
            width: 350,
            padding: "0.5em",
            icon: "success",
          }).then(() => {
            vm.clearFields();
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
          }).then(() => {
            vm.clearFields();
          });
        });
    },
    create() {
      this.isLoading = true;
      this.bam = true;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };
      let vm = this;

      let formData = new FormData();
      formData.append("nombre", this.fields.nombre);
      if (this.fields.tipo) formData.append("tipo", this.fields.tipo);
      if (this.fields.email) formData.append("email", this.fields.email);
      if (this.fields.telefono) {
        formData.append("telefono", this.fields.telefono);
      }
      if (this.fields.entidadPadre)
        formData.append("entidadPadre", this.fields.entidadPadre);

      axios
        .post(process.env.VUE_APP_URL + "/entidad", formData, config)
        .then(function (response) {
          if (!vm.fields.entidadPadre) {
            const entidades = response.data;
            vm.entidades.push(entidades);
          }

          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Dependencia Creada",
            width: 350,
            padding: "0.5em",
            icon: "success",
          }).then(() => {
            vm.clearFields();
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
          }).then(() => {
            vm.clearFields();
          });
        });
    },
    update() {
      this.fields.header = { Authorization: "Bearer " + Login.methods.token };
      let vm = this;
      this.isLoading = true;
      axios
        .put(
          process.env.VUE_APP_URL + "/entidad/" + this.entidad_id,
          this.fields,
        )
        .then(function (response) {
          vm.isLoading = false;
          const entidades = response.data;
          vm.entidades[vm.actualizar] = entidades;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Dependencia Actualizada",
            width: 350,
            padding: "0.5em",
            icon: "success",
          }).then(() => {
            vm.clearFields();
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
          }).then(() => {
            vm.clearFields();
          });
        });
    },
    editar() {
      let vm = this;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formData = new FormData();
      if (this.id_departamento)
        formData.append("departamento", this.id_departamento);
      if (this.id_municipio) formData.append("municipio", this.id_municipio);
      if (this.fields.correo) formData.append("correo", this.fields.correo);
      if (this.fields.telefono) {
        formData.append("telefono", this.fields.telefono);
      }
      this.isLoading = true;
      axios
        .put(
          process.env.VUE_APP_URL + "/entidadDepartamentoMunicipio/" + this.edm,
          formData,
          config,
        )
        .then(function (response) {
          vm.isLoading = false;
          const entidades = response.data;
          vm.entidades[vm.actualizar] = entidades;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Actualizado",
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
      this.fields.email = enti.email;
      this.fields.telefono = enti.telefono;
      this.fields.tipo = enti.tipo;
      if (this.fields.tipo == "Nacional") {
        this.nacional = true;
      } else {
        this.nacional = false;
      }
      this.entidad_id = enti.codigo;
      this.modal = false;
      this.actualizar = index;
    },
    fieldsActualizar(data) {
      this.fields.correo = data.correo_dm;
      this.fields.telefono = data.telefono_dm;
      this.edm = data.id_edm;
      this.tipo = data.entidad.tipo;
      this.fieldsActual = true;

      let vm = this;
      if (this.tipo == "Departamento") {
        axios
          .get(process.env.VUE_APP_URL + "/departamento/" + data.entidad.codigo)
          .then(function (response) {
            vm.isLoading = false;
            vm.dm = response.data;
            vm.id_departamento = data.departamento.id_departamento;
          });
      }
      if (this.tipo == "Municipio") {
        axios
          .get(process.env.VUE_APP_URL + "/municipios/" + data.entidad.codigo)
          .then(function (response) {
            vm.isLoading = false;
            vm.dm = response.data;
          });
      }
    },
    onClickDelete(id, index) {
      let vm = this;
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
          this.isLoading = true;
          axios
            .delete(process.env.VUE_APP_URL + "/entidad/" + id, config)
            .then(() => {
              this.entidades.splice(index, 1);
              vm.isLoading = false;
              Swal.fire({
                title: "Eliminada",
                text: "Entidad Eliminada",
                width: 350,
                padding: "0.5em",
                icon: "success",
              });
            })
            .catch(function () {
              vm.isLoading = false;
              Swal.fire(
                "Error! Esta entidad esta relacionada con una solicitud",
                "Contacte el Administrador!",
                "error",
              );
            });
        }
      });
    },
    Delete(id) {
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
            .delete(
              process.env.VUE_APP_URL + "/entidadDepartamentoMunicipio/" + id,
              config,
            )
            .then(() => {
              Swal.fire({
                title: "Eliminado",
                text: "Correo Eliminado",
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
