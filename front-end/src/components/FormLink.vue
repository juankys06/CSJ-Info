<template>
  <div>
    <span v-if="schema.prefijo">{{ schema.prefijo }}</span>
    <a :id="id_name" :href="schema.href" target="_blank">
      {{ schema.texto }}
    </a>
    <span v-if="schema.sufijo">{{ schema.sufijo }}</span>
  </div>
</template>

<script>
import { abstractField } from "vue-form-generator";
export default {
  mixins: [abstractField],
  computed: {
    id_name() {
      return this.schema.inputName
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
};
</script>
