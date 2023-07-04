<template>
  <div class="form-group row align-items-center">
    <label class="col-md-2 col-form-label">Status:</label>
    <div class="col-md-8">
      <select v-model="estatus_id" class="form-control" required>
        <option
          v-for="estatus_object in estatus"
          :key="'cambiarEstatus' + estatus_object.id"
          :value="estatus_object.id"
        >
          {{ estatus_object.nombreStatus }}
        </option>
      </select>
    </div>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "SolicitudCambiarEstatus",
  props: {
    estatus: {
      type: Array,
      required: true,
    },
    solicitud: {
      type: Object,
      required: true,
    },
  },

  data() {
    return {
      estatus_id: this.solicitud.status.id,
    };
  },

  mounted() {},

  methods: {
    CambiarEstatus() {
      return axios.put(
        process.env.VUE_APP_URL +
          "/status/" +
          this.estatus_id +
          "/solicitudes/" +
          this.solicitud.id,
      );
    },
  },
};
</script>

<style lang="scss" scoped></style>
