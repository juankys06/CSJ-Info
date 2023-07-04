<template>
  <div>
    <ListSolicitudes
      v-if="formulario"
      :formulario="formulario"
    ></ListSolicitudes>
  </div>
</template>

<script>
import ListSolicitudes from "@/components/ListSolicitudes.vue";
import axios from "axios";

export default {
  name: "SolicitudesNew",
  components: { ListSolicitudes },

  data() {
    return {
      formulario: null,
    };
  },

  mounted() {
    this.fetch_formulario();
  },

  methods: {
    fetch_formulario() {
      axios
        .get(
          `${process.env.VUE_APP_URL}/formularios/url/${this.$route.params.formulario_url}`,
        )
        .then((response) => {
          this.formulario = response.data;
        })
        .catch(() => {
          this.isLoading = false;
          this.$bvModal.msgBoxOk("Hubo un error al traer el formulario", {
            title: "Error de formulario!",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },
  },
};
</script>

<style lang="scss" scoped></style>
