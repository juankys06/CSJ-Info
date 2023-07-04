import axios from "axios";

class Auth {
  constructor() {
    this.token = window.localStorage.getItem("token");

    let userData = window.localStorage.getItem("user");
    this.user = userData ? JSON.parse(userData) : null;

    if (this.token) {
      axios.defaults.headers.common["Authorization"] = "Bearer " + this.token;
      this.getUser();
    }
  }

  login(token, user) {
    window.localStorage.setItem("token", token);
    window.localStorage.setItem("user", JSON.stringify(user));

    axios.defaults.headers.common["Authorization"] = "Bearer " + token;

    this.token = token;
    this.user = user;

    Event.$emit("userLoggedIn");
  }
  getUser() {
    var config = {
      headers: { Authorization: "Bearer " + this.token },
    };
    axios
      .get("/api/auth/user", config)
      .then(({ data }) => {
        this.user = data;
      })
      .catch(({ response }) => {
        if (response.status === 401) {
          this.logout();
        }
      });
  }
  logout() {
    var config = {
      headers: { Authorization: "Bearer " + this.token },
    };
    axios
      .get("/api/auth/logout", config)
      .then(() => {
        this.token = null;
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        location.reload(true);
      })
      .catch(() => {
        this.token = null;
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        location.reload(true);
      });
  }
  check() {
    return !!this.token;
  }
}

export default Auth;
