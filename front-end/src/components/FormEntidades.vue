<template>
  <b-form-row :id="id_name" class="mb-4" align-v="center">
    <b-col md="12">
      <b-form-select
        v-model="value.entidad"
        required
        @change="onEntidadSelectChange"
      >
        <template #first>
          <b-form-select-option :value="null" disabled
            >-- Seleccionar dependencia --</b-form-select-option
          >
        </template>
        <b-form-select-option
          v-for="entidad in entidades"
          :key="'select_entidad' + entidad.codigo"
          :value="{
            codigo: entidad.codigo,
            tipo: entidad.tipo,
            nombre: entidad.nombre,
            entidadHijos: entidad.entidadHijos,
            email: entidad.email,
            telefono: entidad.telefono,
          }"
        >
          {{ entidad.nombre }}
        </b-form-select-option>
      </b-form-select>
    </b-col>

    <template v-if="value.entidad">
      <b-col v-if="value.entidad.tipo == 'Personalizado'" md="12">
        <b-form-select
          v-model="value.personalizado"
          required
          @change="onDMPSelectChange"
        >
          <template #first>
            <b-form-select-option :value="null" disabled
              >-- Seleccionar sub dependencia --</b-form-select-option
            >
          </template>

          <b-form-select-option
            v-for="entidad_hijo in value.entidad.entidadHijos"
            :key="'select_entidad_hijo' + entidad_hijo.codigo"
            :value="{
              codigo: entidad_hijo.codigo,
              tipo: entidad_hijo.tipo,
              nombre: entidad_hijo.nombre,
              entidadHijos: entidad_hijo.entidadHijos,
              email: entidad_hijo.email,
              telefono: entidad_hijo.telefono,
            }"
          >
            {{ entidad_hijo.nombre }}
          </b-form-select-option>
        </b-form-select>
      </b-col>
      <b-col v-if="value.entidad.tipo == 'Departamento'" md="12">
        <b-form-select
          v-model="value.departamento"
          required
          @change="onDMPSelectChange"
        >
          <template #first>
            <b-form-select-option :value="null" disabled
              >-- Seleccionar departamento --</b-form-select-option
            >
          </template>

          <b-form-select-option
            v-for="departamento in departamentos"
            :key="
              'select_departamento' + departamento.departamento.id_departamento
            "
            :value="{
              id_departamento: departamento.departamento.id_departamento,
              id_edm: departamento.id_edm,
              correo_dm: departamento.correo_dm,
              telefono_dm: departamento.telefono_dm,
              nombre: departamento.departamento.nombre,
            }"
          >
            {{ departamento.departamento.nombre }}
          </b-form-select-option>
        </b-form-select>
      </b-col>
      <b-col v-if="value.entidad.tipo == 'Municipio'" md="12">
        <b-form-select
          v-model="value.municipio"
          required
          @change="onDMPSelectChange"
        >
          <template #first>
            <b-form-select-option :value="null" disabled
              >-- Seleccionar municipio --</b-form-select-option
            >
          </template>
          <b-form-select-option
            v-for="municipio in municipios"
            :key="'select_municipio' + municipio.municipio.id_municipio"
            :value="{
              id_municipio: municipio.municipio.id_municipio,
              id_edm: municipio.id_edm,
              correo_dm: municipio.correo_dm,
              telefono_dm: municipio.telefono_dm,
              nombre: municipio.municipio.nombre,
            }"
          >
            {{ municipio.municipio.nombre }}
          </b-form-select-option>
        </b-form-select>
      </b-col>
      <b-col v-show="correo_manual" md="12">
        <b-form-group
          label="Correo de la dependencia:"
          label-for="correo_asignacion"
        >
          <template #label>
            <div class="d-flex justify-content-between">
              <span>Correo de la dependencia:</span>
              <b-button
                v-b-tooltip
                title="Si no conoce el correo, deje el campo en blanco"
                round
                class="btn-icon-only rounded-circle"
                size="sm"
                variant="info"
              >
                <b-icon
                  icon="question-circle-fill"
                  class="rounded-circle"
                ></b-icon>
              </b-button>
            </div>
          </template>
          <b-form-input
            :id="'correo_asignacion'"
            v-model="value.correo_asignacion"
            type="email"
            placeholder="Correo de la dependencia"
          ></b-form-input>
        </b-form-group>
      </b-col>
    </template>
  </b-form-row>
</template>

<script>
import axios from "axios";
import { abstractField } from "vue-form-generator";
export default {
  mixins: [abstractField],
  data() {
    return {
      departamentos: [],
      municipios: [],
      entidades: [],
      correo_manual: false,
      personalizados: [],
    };
  },
  computed: {
    id_name() {
      return this.schema.label
        .toLowerCase()
        .replaceAll(" ", "-")
        .replaceAll(":", "")
        .replaceAll("ó", "")
        .replaceAll("á", "")
        .replaceAll("é", "")
        .replaceAll("ú", "")
        .replaceAll("í", "")
        .replaceAll("/", "")
        .replaceAll(".", "");
    },
  },
  mounted() {
    this.fetch_entidades();
  },
  methods: {
    // Fetch data de modelos  --------------------------------------------

    async fetch_departamentos(codigo) {
      return await axios
        .get(process.env.VUE_APP_URL + `/departamento/seleccion/${codigo}`)
        .then((response) => {
          this.departamentos = response.data;
        });
    },

    async fetch_municipios(codigo) {
      return await axios
        .get(process.env.VUE_APP_URL + `/municipios/seleccion/${codigo}`)
        .then((response) => {
          this.municipios = response.data;
        });
    },

    fetch_entidades() {
      axios.get(process.env.VUE_APP_URL + "/entidadN").then((response) => {
        this.entidades = response.data;
      });
    },
    // Fin FETCH data de modelos ------------------------------------------

    // onChange events  ---------------------------------------------------

    onEntidadSelectChange(entidad) {
      // Reiniciando los valores al cambiar de entidad
      this.value.departamento = null;
      this.value.municipio = null;
      this.value.personalizado = null;
      this.value.correo_asignacion = "";

      // Solicitando la data segun el tipo de entidad
      if (entidad.tipo == "Departamento") {
        this.fetch_departamentos(entidad.codigo);
      }
      if (entidad.tipo == "Municipio") {
        this.fetch_municipios(entidad.codigo);
      }

      if (entidad.tipo == "Nacional") {
        this.$set(this.value, "correo_asignacion", entidad.email);
        this.$set(this.value, "telefono_asignacion", entidad.telefono);
        this.correo_manual = this.value.correo_asignacion ? false : true;
      } else {
        this.correo_manual = false;
      }
    },

    onDMPSelectChange(DMP) {
      if (!(this.value.entidad.tipo == "Personalizado")) {
        this.$set(this.value, "correo_asignacion", DMP.correo_dm);
        this.$set(this.value, "telefono_asignacion", DMP.telefono_dm);
      } else {
        this.$set(this.value, "correo_asignacion", DMP.email);
        this.$set(this.value, "telefono_asignacion", DMP.telefono);
      }

      this.correo_manual = this.value.correo_asignacion ? false : true;
    },
    // Fin de onChange events  ------------------------------------------
  },
};
</script>
