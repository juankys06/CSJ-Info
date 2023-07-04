<template>
  <div>
    <button @click="downloadWithCSS">Download PDF WITH CSS</button>
    <br />
    <br />
    <button @click="download">Download PDF WITHOUT CSS</button>
    <div ref="content">
      <p style="background-color: red">Hello Vue in CodeSandbox!</p>
      <h3>
        Installed CLI Plugins Installed CLI Plugins Installed CLI Plugins
        Installed CLI Plugins
      </h3>
      <ul>
        <li>
          <a
            href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-babel"
            target="_blank"
            rel="noopener"
            >babel</a
          >
        </li>
        <li>
          <a
            href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-eslint"
            target="_blank"
            rel="noopener"
            >eslint</a
          >
        </li>
      </ul>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>Essential Links</h3>
      <h3>Ecosystem</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
      <h3>hola</h3>
    </div>
  </div>
</template>

<script>
import jsPDF from "jspdf";
import axios from "axios";

export default {
  data() {
    return {
      consulta: [],
      entidad: [],
      auditoria: [],
      estatus: [],
      i: 0,
      parPage: 10,
      currentPage: 1,
      pag: true,
      reporte: [],
      tipo: 999,
      status_id: "",
      entidad_id: "",
      fecha: {},
      todos: [
        { title: "todo 1", description: "description 1" },
        { title: "todo 2", description: "description2" },
        { title: "todo 3", description: "description 3" },
        { title: "todo 4", description: "description 4" },
        { title: "todo 5", description: "description 5" },
      ],
    };
  },
  computed: {
    getItems: function () {
      let current = this.currentPage * this.parPage;
      let start = current - this.parPage;
      return this.reporte.slice(start, current);
    },
    getPageCount: function () {
      return Math.ceil(this.reporte.length / this.parPage);
    },
  },
  mounted() {
    let vm = this;

    axios.get(process.env.VUE_APP_URL + "/entidad").then(function (response) {
      vm.entidad = response.data;
    });
    axios.get(process.env.VUE_APP_URL + "/status").then(function (response) {
      vm.estatus = response.data;
    });
  },
  methods: {
    download() {
      const doc = new jsPDF();
      /** WITHOUT CSS */
      const contentHtml = this.$refs.content.innerHTML;
      doc.fromHTML(contentHtml, 15, 15, {
        width: 170,
      });
      doc.save("sample.pdf");
    },
    detalles(auditoriaId) {
      let vm = this;
      axios
        .get(process.env.VUE_APP_URL + "/auditoria/" + auditoriaId)
        .then(function (response) {
          vm.auditoria = response.data.content;
        });
    },

    clickCallback: function (pageNum) {
      this.currentPage = Number(pageNum);
    },
    Reportes() {
      let vm = this;
      if (this.tipo == 1) {
        axios
          .get(
            process.env.VUE_APP_URL +
              "/status/" +
              this.status_id +
              "/solicitudes",
          )
          .then(function (response) {
            vm.reporte = response.data.content;
          })
          .catch(function (error) {
            alert(error.response.data.data.message);
          });
      }
      if (this.tipo == 0) {
        axios
          .get(
            process.env.VUE_APP_URL +
              "/entidad/" +
              this.entidad_id +
              "/solicitudes",
          )
          .then(function (response) {
            vm.reporte = response.data.content;
          })
          .catch(function (error) {
            alert(error.response.data.message);
          });
      }
      if (this.tipo == 2) {
        this.fecha.desde = this.fecha.desde + " 00:00";
        this.fecha.hasta = this.fecha.hasta + " 00:00";
        let formData = new FormData();

        formData.append("desde", this.fecha.desde);
        formData.append("hasta", this.fecha.hasta);

        axios
          .post(process.env.VUE_APP_URL + "/solicitudes/fecha", formData)
          .then(function (response) {
            vm.reporte = response.data;
          })
          .catch(function (error) {
            alert(error.response.data.message);
          });
      }
    },
  },
};
</script>
<style>
.pagination {
  margin: 2% 65%;
}

.page-item.active {
  border-radius: 0px;
  position: static !important;
  background-color: #ed6528 !important;
}
</style>
