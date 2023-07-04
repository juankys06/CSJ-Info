import Vue from "vue";
import VueRouter from "vue-router";
import auth from "./auth.vue";
Vue.use(VueRouter);
import axios from "axios";

/*
let result =  axios.get(process.env.VUE_APP_URL+'/api/formularios/activo').then((value) => {
  return value.data.url
});

console.log(result)*/

const routes = [
  {
    path: "/",
    name: "home",
    component: () => import(/* webpackChunkName: "about" */ "@/views/Home.vue"),
    meta: { middlewareAuth: true, inicio: true },
  },
  {
    path: "/login",
    name: "login",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Login.vue"),
    meta: { Auth: true },
  },
  {
    path: "/formulario/:formulario_url",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/FormularioUsuario.vue"),
    meta: { enviar: true },
  },
  {
    path: "/solicitudes/:formulario_url",
    name: "solicitudes",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/SolicitudesNew.vue"),
    meta: { middlewareAuth: true, solicitudes: true },
  },
  {
    path: "/solicitudes/:formulario_url/respuesta",
    name: "respuesta",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Respuesta.vue"),
    props: true,
    meta: { middlewareAuth: true, respuesta: true },
  },
  {
    path: "/dependencias",
    name: "dependencias",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Entidad.vue"),
    meta: { middlewareAuth: true, entidad: true },
  },
  {
    path: "/listaNegra",
    name: "listaNegra",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/blacklist.vue"),
    meta: { middlewareAuth: true, blacklist: true },
  },
  {
    path: "/consulta",
    name: "consulta",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Consulta.vue"),
    meta: { consulta: true },
  },
  {
    path: "/auditoria",
    name: "auditoria",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Auditoria.vue"),
    meta: { middlewareAuth: true, auditoria: true },
  },
  {
    path: "/reportes",
    name: "reportes",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Reportes.vue"),
    props: true,
    meta: { middlewareAuth: true, reportes: true },
  },
  {
    path: "/formularios",
    name: "formularios",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Formularios.vue"),
    meta: { middlewareAuth: true, formularios: true },
  },
  {
    path: "/formularios/modals",
    name: "modals",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/modals.vue"),
    meta: { middlewareAuth: true },
  },
  {
    path: "/formularioSeleccion",
    name: "formularioSeleccion",
    component: () =>
      import(
        /* webpackChunkName: "about" */ "../views/SeleccionFormulario.vue"
      ),
    meta: { middlewareAuth: true, formulariosSeleccion: true },
  },
  {
    path: "/combinarSolicitudes",
    name: "combinarSolicitudes",
    component: () =>
      import(
        /* webpackChunkName: "about" */ "../views/combinarSolicitudes.vue"
      ),
    props: true,
    meta: { middlewareAuth: true, solicitudes: true },
  },
  {
    path: "/nuevoformularios",
    name: "nuevoformularios",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/NuevoFormulario.vue"),
    meta: { middlewareAuth: true, formularios: true },
  },
  {
    path: "/editarformularios",
    name: "editarformularios",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/NuevoFormulario.vue"),
    props: true,
    meta: { middlewareAuth: true, formularios: true },
  },
  {
    path: "/gestionar-roles",
    name: "gestionar-roles",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/GestionarRoles.vue"),
    meta: { middlewareAuth: true, roles: true },
  },
  {
    path: "/asignar-roles",
    name: "asignar-roles",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AsignarRoles.vue"),
    meta: { middlewareAuth: true, roles: true },
  },
  {
    path: "/agregar-usuarios",
    name: "agregar-usuarios",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AgregarUsuarios.vue"),
    meta: { middlewareAuth: true, roles: true },
  },
  {
    path: "/encuestas-admin",
    name: "encuestas-admin",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Encuestas.vue"),
    meta: { middlewareAuth: true, encuesta: true },
  },
  {
    path: "/preguntas",
    name: "preguntas",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Preguntas.vue"),
    meta: { middlewareAuth: true, preguntas: true },
  },
  {
    path: "/encuesta/:nombre",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/EncuestasVista.vue"),
    meta: { enviarEncuesta: true },
  },
  {
    path: "/mis-respuestas",
    name: "mis-respuestas",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/RespuestaUsuario.vue"),
    meta: { middlewareAuth: true, solicitudes: true },
  },
  {
    path: "/test",
    name: "test",
    component: () =>
      import(
        /* webpackChunkName: "about" */ "../components/ListAuditorias.vue"
      ),
    meta: { middlewareAuth: true },
  },
];

var router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.middlewareAuth)) {
    if (!auth.methods.check()) {
      axios
        .get(process.env.VUE_APP_URL + "/api/formularios/activo")
        .then(function (response) {
          next({
            path: "/formulario/" + response.data.url,
          });
        });

      return;
    } else {
      let user = JSON.parse(localStorage.getItem("user"));
      let admin = false,
        entidad = false,
        solicitudes = false,
        auditoria = false,
        reportes = false,
        roles = false,
        formularios = false,
        encuestas = false,
        formulariosSeleccion = false;
      if (to.matched.some((record) => record.meta.inicio)) {
        next();
      } else {
        for (var j = 0; j < user.permisos.length; j++) {
          if (user.permisos[j] == "Administrador") {
            admin = true;
            break;
          }
          if (user.permisos[j] == "Administrador de Entidad") {
            entidad = true;
          }
          if (user.permisos[j] == "Administrador de Solicitudes") {
            solicitudes = true;
            formulariosSeleccion = true;
          }
          if (
            user.permisos[j] == "Gestionar Roles" ||
            user.permisos[j] == "Asignar Roles"
          ) {
            roles = true;
          }
          if (user.permisos[j] == "Administrador de Auditoria") {
            auditoria = true;
          }
          if (user.permisos[j] == "Administrador de Reportes") {
            reportes = true;
          }
          if (user.permisos[j] == "Administrador de Formularios") {
            formularios = true;
          }
          if (user.permisos[j] == "Administrador de Encuestas") {
            encuestas = true;
          }
        }
        if (admin) {
          next();
        } else if (
          to.matched.some((record) => record.meta.entidad) &&
          entidad
        ) {
          next();
        } else if (
          (to.matched.some((record) => record.meta.solicitudes) &&
            solicitudes) ||
          (to.matched.some((record) => record.meta.formulariosSeleccion) &&
            formulariosSeleccion)
        ) {
          next();
        } else if (to.matched.some((record) => record.meta.roles) && roles) {
          next();
        } else if (
          to.matched.some((record) => record.meta.auditoria) &&
          auditoria
        ) {
          next();
        } else if (
          to.matched.some((record) => record.meta.reportes) &&
          reportes
        ) {
          next();
        } else if (
          to.matched.some((record) => record.meta.formularios) &&
          formularios
        ) {
          next();
        } else if (
          to.matched.some((record) => record.meta.encuesta) &&
          encuestas
        ) {
          next();
        } else {
          alert("Usuario no Autorizado");
          next({
            path: "/",
          });

          return;
        }
      }
    }
  } else if (to.matched.some((record) => record.meta.Auth)) {
    if (auth.methods.check()) {
      next({
        path: "/",
      });
      return;
    }
  } else if (to.matched.some((record) => record.meta.enviar)) {
    var ba = true;
    axios
      .get(process.env.VUE_APP_URL + "/api/formularios")
      .then(function (response) {
        for (var i = 0; i < Object.keys(response.data).length; i++) {
          if (
            window.location.pathname ==
            "/formulario/" + response.data[i].url
          ) {
            ba = false;
            next();
          }
          if (ba && Object.keys(response.data).length == i + 1) {
            next({
              path: "/",
            });
          }
        }
      });
  } else if (to.matched.some((record) => record.meta.enviarEncuesta)) {
    var ban = true;
    axios
      .get(process.env.VUE_APP_URL + "/api/encuestas")
      .then(function (response) {
        for (var i = 0; i < Object.keys(response.data).length; i++) {
          if (
            window.location.pathname == "/encuesta/" + response.data[i].url &&
            response.data[i].activo == true
          ) {
            ban = false;
            next();
          }
          if (ban && Object.keys(response.data).length == i + 1) {
            next({
              path: "/",
            });
          }
        }
      });
  }

  next();
});

export default router;
