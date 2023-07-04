<script>
import axios from "axios";
import Vue from "vue";

window.Event = new Vue();
export default {
  name: "App",
  data() {
    return {
      token: "",
      user: [],
    };
  },

  methods: {
    constructor() {
      this.token = window.localStorage.getItem("token");

      let userData = window.localStorage.getItem("user");
      this.user = userData ? JSON.parse(userData) : null;

      if (this.token) {
        axios.defaults.headers.common["Authorization"] = "Bearer " + this.token;
        this.getUser();
      }
    },

    login(token, user) {
      window.localStorage.setItem("token", token);
      window.localStorage.setItem("user", JSON.stringify(user));

      axios.defaults.headers.common["Authorization"] = "Bearer " + token;

      this.token = token;
      this.user = user;

      Event.$emit("userLoggedIn");
    },
    getUser() {
      var config = {
        headers: { Authorization: "Bearer " + this.token },
      };
      axios
        .get(process.env.VUE_APP_URL + "/auth/user", config)
        .then(() => {})
        .catch(({ response }) => {
          if (response.status === 401) {
            this.logout();
          }
        });
    },
    logout() {
      this.token = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      location.reload(true);
    },
    check() {
      this.constructor();
      return !!this.token;
    },
  },
};
</script>
