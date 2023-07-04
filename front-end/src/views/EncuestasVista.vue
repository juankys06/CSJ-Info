<template>
  <div class="encuestas" style="margin: auto; width: 50%">
    <Loading
      :active.sync="isLoading"
      loader="dots"
      :height="120"
      :width="120"
      color="#19325B"
    ></Loading>
    <form @submit.prevent="Enviar()">
      <div class="form-group">
        <label class="col-md-12 control-label" style="text-align: center"
          ><b>{{ encuestas.titulo }}</b></label
        >

        <div v-for="(preguntas, index) in preguntas" :key="preguntas.id">
          <label class="col-md-12 control-label"
            ><b>{{ "Pregunta " + (index + 1) + ": " + preguntas.texto }}</b>
          </label>

          <div
            v-for="respuestas in preguntas.respuestas"
            :key="respuestas.id"
            class="custom-control custom-radio"
            style="display: inline-block; padding: 0px 40px 0px 50px"
          >
            <input
              :id="'respuesta' + respuestas.id + '-' + preguntas.id"
              v-model="respuestasEnviar[index]"
              type="radio"
              class="custom-control-input"
              :value="respuestas.id"
              @click="preguntasEnviar[index] = preguntas.id"
            />
            <label
              class="custom-control-label"
              :for="'respuesta' + respuestas.id + '-' + preguntas.id"
              >{{ respuestas.texto }}</label
            >
          </div>
        </div>
      </div>
      <br />
      <br />
      <div class="form-group" style="text-align: center">
        <button type="submit" class="btn btn-primary" style="width: 30%">
          Enviar Respuestas
        </button>
      </div>
    </form>
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
      nombre: "",
      fields: {},
      entidad_id: "",
      modal: true,
      actualizar: "",
      PreguntaRespuestas: [],
      respuestasEnviar: [],
      preguntasEnviar: [],
      preguntas: [],
      cont: 0,
      isLoading: false,
    };
  },
  mounted() {
    let vm = this;
    this.isLoading = true;

    axios.get(process.env.VUE_APP_URL + "/encuestas").then(function (response) {
      for (var i = 0; i < Object.keys(response.data).length; i++) {
        if (window.location.pathname == "/encuesta/" + response.data[i].url) {
          axios
            .get(
              process.env.VUE_APP_URL +
                "/encuestaCount?encuesta=" +
                response.data[i].id +
                "&email=" +
                vm.$route.query.user,
            )
            .then(function (response) {
              vm.isLoading = false;
              if (!response.data) {
                Swal.fire({
                  title:
                    "Ya respondio esta encuesta las veces correspondientes.!",
                  width: 350,
                  padding: "0.5em",
                  icon: "warning",
                });
                vm.$router.push("/");
              }
            });
          vm.isLoading = false;
          vm.encuestas = response.data[i];
          vm.preguntas = vm.encuestas.preguntas;
          vm.preguntas = vm.preguntas.sort(function (a, b) {
            return a.id - b.id; /* Modificar si se desea otra propiedad */
          });
          for (var j = 0; j < Object.keys(vm.preguntas).length; j++) {
            vm.preguntas[j].respuestas = vm.preguntas[j].respuestas.sort(
              function (a, b) {
                return a.id - b.id; /* Modificar si se desea otra propiedad */
              },
            );
          }

          break;
        } else if (Object.keys(response.data).length == i + 1) {
          vm.isLoading = false;
          vm.$router.push("/");
        }
      }
    });
  },
  methods: {
    Enviar() {
      this.isLoading = true;
      let vm = this;
      var config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };
      let formData = new FormData();
      formData.append("encuesta", this.encuestas.id);
      formData.append("user_agent", this.$route.query.user);
      for (var i = 0; i < this.preguntasEnviar.length; i++) {
        formData.append("preguntas", this.preguntasEnviar[i]);
        formData.append("respuestas", this.respuestasEnviar[i]);
      }

      axios
        .post(process.env.VUE_APP_URL + "/seleccion", formData, config)
        .then(function () {
          vm.isLoading = false;
          Swal.fire({
            title: "Buen Trabajo.!",
            text: "Respuestas Enviadas Exitosamente.!",
            width: 350,
            padding: "0.5em",
            icon: "success",
          });
          vm.$router.push("/consulta");
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
<style></style>
