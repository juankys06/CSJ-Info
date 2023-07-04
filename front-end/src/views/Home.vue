<template>
  <div class="home"></div>
</template>

<script>
//import Login from '@/router/auth.vue';
//import axios from "axios";

export default {
  data() {
    return {
      fields: {},
    };
  },
  mounted() {
    /*if (this.$route.query.code) {
          var config = {
                    headers: {'Authorization': 'Bearer '+ Login.methods.token,
                        'Content-type': 'application/x-www-form-urlencoded'
                    }
                  };

          let formData = new FormData();
          formData.append('code', this.$route.query.code);
          formData.append('redirect_uri','https://csjinfo.ramajudicial.gov.co/');

        axios.post(process.env.VUE_APP_URL+'/api/requestToken',formData,config)
                .then(function (response) {
                window.localStorage.setItem('tokenEmail', response.data.access_token);            
        });
        }*/
  },
  methods: {},
};
</script>

<style>
.ck-editor__editable {
  min-height: 200px;
}
</style>
