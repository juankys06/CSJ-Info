<template>
  <label :id="id_name" style="display: flex">
    <input
      v-model="value"
      :class="schema.fieldClasses"
      type="checkbox"
      :disabled="disabled"
      style="margin: 10px 10px 0 0"
      :required="schema.required"
    />
    <div v-html="schema.placeholder"></div>
  </label>
</template>

<script>
import { abstractField } from "vue-form-generator";
export default {
  mixins: [abstractField],
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
};
</script>
