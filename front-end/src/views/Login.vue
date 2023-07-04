<template>
  <div class="header">
    <body>
      <Loading
        :active.sync="isLoading"
        loader="dots"
        height="120"
        width="120"
        color="#19325B"
      ></Loading>
      <div id="content" class="container body-content">
        <div class="row">
          <div class="col-md-12" style="margin-top: 2rem">
            <center>
              <section style="width: 33%; text-align: center">
                <h2>Iniciar Sesión</h2>
                <hr />
                <form
                  class="form-horizontal"
                  enctype="multipart/form-data"
                  @submit.prevent="login()"
                >
                  <div class="form-group">
                    <label for="email" class="col-md-4 control-label"
                      >E-Mail
                    </label>

                    <div class="col-md-12">
                      <input
                        id="email"
                        v-model="loginDetails.email"
                        type="text"
                        class="form-control"
                        name="email"
                        required
                      />
                    </div>
                  </div>

                  <div class="form-group">
                    <label for="password" class="col-md-4 control-label"
                      >Contraseña</label
                    >

                    <div class="col-md-12">
                      <input
                        id="password"
                        v-model="loginDetails.password"
                        type="password"
                        class="form-control"
                        name="password"
                        required
                      />
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-12 col-md-offset-4">
                      <div class="checkbox">
                        <label>
                          <input
                            v-model="loginDetails.remember"
                            type="checkbox"
                            name="remember"
                          />
                          Recuerdame
                        </label>
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <VueRecaptcha
                      sitekey="6LcuIM0ZAAAAAKtPxDbOzfmMNt0I3yM0DRLiwB8A"
                      :load-recaptcha-script="true"
                      @verify="validate"
                    ></VueRecaptcha>
                  </div>
                  <div class="form-group">
                    <button type="submit" class="btn btn-default">
                      Iniciar Sesión
                    </button>
                  </div>
                  <div class="form-group">
                    <p>
                      <router-link style="color: #0056b3" to="/login"
                        >¿Olvidó su contraseña?</router-link
                      >
                    </p>
                  </div>
                </form>
              </section>
            </center>
          </div>
        </div>
      </div>
    </body>
  </div>
</template>

<script>
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import Login from "@/router/auth.vue";
import axios from "axios";
import VueRecaptcha from "vue-recaptcha";
export default {
  components: {
    VueRecaptcha,
    Loading,
  },
  data() {
    return {
      date: "",
      loginDetails: {},
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
      validar: "",
      recaptcha: "",
      isLoading: false,
    };
  },
  mounted() {
    this.date = new Date();
  },
  methods: {
    login() {
      let vm = this;
      this.isLoading = true;
      if (this.validar) {
        axios
          .post(process.env.VUE_APP_URL + "/auth/signin", {
            email: this.loginDetails.email,
            password: this.loginDetails.password,
          })
          .then(function (data) {
            vm.isLoading = false;
            Login.methods.login(data.data.accessToken, data.data);
            vm.$router.push("/");
            location.reload(true);
          })
          .catch(function (error) {
            vm.isLoading = false;
            alert(error.response.data.message);
          });
      } else {
        alert("Ha fallado el Captcha, por favor intentelo de nuevo!");
      }
    },
    validate(response) {
      this.validar = response;
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
