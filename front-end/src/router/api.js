import axios from "axios";
import Auth from "./auth.js";
window.auth = new Auth();
class Api {
  constructor() {}

  call(requestType, url, data = null) {
    return new Promise((resolve, reject) => {
      axios[requestType](url, data)
        .then((response) => {
          resolve(response);
        })
        .catch(({ response }) => {
          if (response.status === 401) {
            Auth.logout();
          }

          reject(response);
        });
    });
  }
}

export default Api;
