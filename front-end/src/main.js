import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import { ClientTable } from "vue-tables-2";
import { BootstrapVue, IconsPlugin } from "bootstrap-vue";

// Import Bootstrap an BootstrapVue CSS files (order is important)
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

import FormLink from "@/components/FormLink.vue";
import FormCheckbox from "@/components/FormCheckbox.vue";
import FormEntidades from "@/components/FormEntidades.vue";

Vue.component("fieldFormlink", FormLink);
Vue.component("fieldFormcheckbox", FormCheckbox);
Vue.component("fieldFormentidades", FormEntidades);

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue);
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin);

Vue.config.productionTip = false;
Vue.use(ClientTable);

new Vue({
  router,
  render: (h) => h(App),
}).$mount("#app");
