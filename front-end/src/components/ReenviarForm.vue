<template>
  <div class="form-group row align-items-center">
    <label class="col-md-2 col-form-label">Correo solicitante:</label>
    <div class="col-md-8">
      <input
        id="correo_solicitante"
        v-model="reenviar.correo_solicitante"
        type="email"
        class="form-control"
        name="correo_solicitante"
        required
      />
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Login from "@/router/auth.vue";
export default {
  name: "SolicitudCambiarEstatus",
  props: {
    solicitud: {
      type: Object,
      required: true,
    },
  },

  data() {
    return {
      reenviar: {
        correo_solicitante: this.solicitud.correo_solicitante,
      },
    };
  },

  mounted() {},

  methods: {
    Reenviar() {
      let config = {
        headers: {
          Authorization: "Bearer " + Login.methods.token,
          "Content-type": "application/x-www-form-urlencoded",
        },
      };

      let formdata = new FormData();

      formdata.append("correo_solicitante", this.reenviar.correo_solicitante);

      return axios.put(
        process.env.VUE_APP_URL + `/solicitudes/${this.solicitud.id}/reenviar`,
        formdata,
        config,
      );
    },
  },
};
</script>

<style lang="scss" scoped></style>
