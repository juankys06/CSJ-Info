<template>
  <header id="banner" role="banner">
    <div id="heading">
      <div id="top_navigation">
        <ul>
          <li>
            <a
              href="https://www.ramajudicial.gov.co/web/consejo-superior-de-la-judicatura/portal/inicio"
              target="_blank"
              title="Consejo Superior de la Judicatura"
              ><span style="background-color: #91be91"
                >Consejo Superior de la Judicatura</span
              ></a
            >
          </li>
          <li>
            <a
              href="https://cortesuprema.gov.co/corte/"
              target="_blank"
              title="Corte Suprema de Justicia"
              ><span style="background-color: #ffd20f; color: black"
                >Corte Suprema de Justicia</span
              ></a
            >
          </li>
          <li>
            <a
              href="http://www.consejodeestado.gov.co/"
              target="_blank"
              title="Consejo de Estado"
              ><span style="background-color: #4f90c8"
                >Consejo de Estado</span
              ></a
            >
          </li>
          <li>
            <a
              href="https://www.corteconstitucional.gov.co/"
              target="_blank"
              title="Corte Constitucional"
              ><span style="background-color: #cb302f"
                >Corte Constitucional</span
              ></a
            >
          </li>
        </ul>
      </div>
      <div class="row" style="display: flex; margin-top: 1%">
        <div style="width: 25%" class="site-title-content col-md-3">
          <div class="box-logo">
            <a
              class="logo custom-logo"
              href="https://www.ramajudicial.gov.co"
              title="Ir a Rama Judicial"
            >
              <img
                alt="Rama Judicial"
                class="block-margin-img1"
                height="110"
                src="@/assets/logo1.png"
              />
            </a>
          </div>
        </div>
        <div style="width: 50%" class="site-title-content col-md-6">
          <span class="currentDate">{{
            meses[date.getMonth()] +
            " " +
            date.getDate() +
            " - " +
            date.getFullYear()
          }}</span>
          <div id="searchBox" class="row">
            <div class="col-xs-10">
              <h1>SOLICITUD<br />DE INFORMACIÓN</h1>
            </div>
          </div>
        </div>
        <div style="width: 25%" class="site-title-content col-md-3 hidden-xs">
          <div class="box-logo">
            <a
              class="logo secondLogo"
              href="https://idm.presidencia.gov.co/deinteres/index.html"
            >
              <img
                src="@/assets/EscudoColombia.png"
                class="block-margin-img1"
                alt="Presidencia de la República de Colombia"
              />
            </a>
          </div>
        </div>
      </div>
    </div>
    <nav id="navigation" class="navbar navbar-inverse">
      <div class="container-fluid">
        <div class="navbar-header">
          <button
            type="button"
            class="navbar-toggle"
            data-toggle="collapse"
            data-target=".navbar-collapse"
          >
            <span class="sr-only">Desplegar navegación</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
        </div>
        <div class="navbar-collapse">
          <ul
            v-if="authenticated"
            class="nav navbar-nav"
            style="display: inline-block !important"
          >
            <li><router-link to="/">Inicio</router-link></li>
            <li v-if="solicitudes">
              <a href="#" class="name-login" data-toggle="dropdown">
                <p>Solicitudes</p>
              </a>
              <ul
                class="dropdown-menu"
                style="position: absolute"
                aria-labelledby="dropdownMenuButton"
              >
                <li>
                  <router-link
                    class="dropdown-item"
                    :to="{ name: 'formularioSeleccion' }"
                    >Solicitudes</router-link
                  >
                  <router-link
                    class="dropdown-item"
                    :to="{
                      name: 'formularioSeleccion',
                      query: { combinar: 1 },
                    }"
                    >Combinar solicitudes</router-link
                  >
                </li>
              </ul>
            </li>
            <li v-if="entidad">
              <router-link :to="{ name: 'dependencias' }">
                Dependencias
              </router-link>
            </li>
            <li v-if="formularios">
              <a href="#" class="name-login" data-toggle="dropdown">
                <p>Formularios</p>
              </a>
              <ul
                class="dropdown-menu"
                style="position: absolute"
                aria-labelledby="dropdownMenuButton"
              >
                <li>
                  <router-link
                    class="dropdown-item"
                    :to="{ name: 'formularios' }"
                    >Formularios</router-link
                  >
                  <router-link
                    class="dropdown-item"
                    :to="{
                      name: 'modals',
                    }"
                    >Modals de formularios</router-link
                  >
                </li>
              </ul>
            </li>
            <li v-if="roles" class="dropdown">
              <a href="#" class="name-login" data-toggle="dropdown">
                <p>Roles</p>
              </a>
              <ul
                class="dropdown-menu"
                style="position: absolute"
                aria-labelledby="dropdownMenuButton"
              >
                <li v-if="rolesGestionar">
                  <router-link class="dropdown-item" to="/gestionar-roles">
                    Gestionar Roles
                  </router-link>
                </li>
                <li v-if="rolesAsignar">
                  <router-link class="dropdown-item" to="/asignar-roles">
                    Asignar Roles
                  </router-link>
                </li>
                <li v-if="rolesAgregar">
                  <router-link class="dropdown-item" to="/agregar-usuarios">
                    Agregar Usuarios
                  </router-link>
                </li>
              </ul>
            </li>

            <li v-if="auditoria">
              <router-link to="/auditoria">Auditoría</router-link>
            </li>
            <li v-if="encuestas" class="dropdown">
              <a href="#" class="name-login" data-toggle="dropdown">
                <p>Encuestas</p>
              </a>
              <ul
                class="dropdown-menu"
                style="position: absolute"
                aria-labelledby="dropdownMenuButton"
              >
                <li>
                  <router-link class="dropdown-item" to="/encuestas-admin">
                    Encuestas
                  </router-link>
                </li>
                <li>
                  <router-link class="dropdown-item" to="/preguntas">
                    Preguntas
                  </router-link>
                </li>
              </ul>
            </li>
            <li v-if="blacklist">
              <router-link to="/listaNegra">Lista Negra</router-link>
            </li>
            <li v-if="solicitudes">
              <router-link to="/mis-respuestas">Mis Respuestas</router-link>
            </li>
          </ul>
          <ul
            v-else
            class="nav navbar-nav"
            style="display: inline-block !important; width: 100%;}"
          >
            <li>
              <router-link class="dropdown-item" to="/consulta"
                >Consulta</router-link
              >
            </li>
            <li style="float: right">
              <router-link class="dropdown-item" to="/login"
                >Iniciar Sesión</router-link
              >
            </li>
          </ul>
          <ul v-if="authenticated" style="display: contents; font-size: 0.8em">
            <li class="dropdown" style="float: right">
              <a href="#" class="name-login" data-toggle="dropdown">
                <p>
                  Bienvenido: <span>{{ user.email }}</span
                  ><br />
                </p>
              </a>
              <ul
                class="dropdown-menu"
                style="position: absolute"
                aria-labelledby="dropdownMenuButton"
              >
                <li>
                  <a class="dropdown-item" @click="logout()">Cerrar Sesion</a>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </header>
</template>

<script>
import Login from "@/router/auth.vue";
//import axios from "axios";
export default {
  data() {
    return {
      date: new Date(),
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
      authenticated: Login.methods.check(),
      user: Login.methods.user,
      entidad: false,
      solicitudes: false,
      formularios: false,
      reportes: false,
      auditoria: false,
      roles: false,
      rolesGestionar: false,
      rolesAsignar: false,
      rolesAgregar: false,
      encuestas: false,
      blacklist: false,
    };
  },

  mounted() {
    let vm = this;

    for (var j = 0; j < this.user.permisos.length; j++) {
      if (this.user.permisos[j] == "Administrador") {
        vm.entidad = true;
        vm.solicitudes = true;
        vm.reportes = true;
        vm.auditoria = true;
        vm.roles = true;
        vm.formularios = true;
        vm.encuestas = true;
        vm.rolesGestionar = true;
        vm.rolesAsignar = true;
        vm.rolesAgregar = true;
        vm.blacklist = true;
        break;
      }
      if (this.user.permisos[j] == "Administrador de Entidad") {
        vm.entidad = true;
      }
      if (this.user.permisos[j] == "Administrador de Solicitudes") {
        vm.solicitudes = true;
      }
      if (this.user.permisos[j] == "Gestionar Roles") {
        vm.roles = true;
        vm.rolesGestionar = true;
      }
      if (this.user.permisos[j] == "Asignar Roles") {
        vm.roles = true;
        vm.rolesAgregar = true;
      }
      if (this.user.permisos[j] == "Agregar roles") {
        vm.roles = true;
        vm.rolesAsignar = true;
      }
      if (this.user.permisos[j] == "Administrador de Auditoria") {
        vm.auditoria = true;
      }
      if (this.user.permisos[j] == "Administrador de Reportes") {
        vm.reportes = true;
      }
      if (this.user.permisos[j] == "Administrador de Formularios") {
        vm.formularios = true;
      }
      if (this.user.permisos[j] == "Administrador de Encuestas") {
        vm.encuestas = true;
      }
    }
  },
  methods: {
    logout() {
      let vm = this;
      Login.methods.logout();
      vm.$router.push("/login");
    },
  },
};
</script>
