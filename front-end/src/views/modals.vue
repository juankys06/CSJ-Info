<template>
  <div class="container">
    <b-row>
      <b-col md="12">
        <b-form-group class="mb-0">
          <b-row>
            <b-input-group class="my-3">
              <b-button
                type="button"
                class="mx-3"
                style="background-color: #19325b; color: white"
                :disabled="fomularios_filtered.length < 1"
                @click="create_action"
              >
                Agregar
              </b-button>
              <b-form-input
                id="filter-input"
                v-model="buscar"
                type="search"
                placeholder="Buscar"
              ></b-form-input>
              <b-input-group-append class="mr-3">
                <b-button
                  style="background-color: #19325b; color: white"
                  :disabled="!buscar"
                  @click="buscar = ''"
                >
                  Limpiar
                </b-button>
              </b-input-group-append>
            </b-input-group>
          </b-row>
        </b-form-group>
        <b-table
          striped
          hover
          :items="modals"
          :fields="campos"
          :filter="buscar"
          :busy.sync="loading"
          :filter-included-fields="['id', 'asunto', 'formulario_descripcion']"
          :per-page="10"
          :current-page="current_page"
          @filtered="onFiltered"
        >
          <template #table-busy>
            <div class="text-center text-danger my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong>Cargando...</strong>
            </div>
          </template>
          <template v-slot:cell(activo)="{ item }">
            {{ item.activo ? "Si" : "No" }}
          </template>
          <template v-slot:cell(acciones)="{ item }">
            <div class="btn-group">
              <button
                type="button"
                class="btn btn-default dropdown-toggle"
                data-toggle="dropdown"
              >
                Acciones
              </button>
              <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <a class="dropdown-item" @click="update_action(item)">Editar</a>
                <a class="dropdown-item" @click="delete_action(item)">
                  Eliminar
                </a>
              </div>
            </div>
          </template>
        </b-table>
        <b-pagination
          v-model="current_page"
          :per-page="10"
          :total-rows="total_rows"
          :prev-text="'Anterior'"
          :next-text="'Siguiente'"
          :class="'pagination'"
          align="right"
        ></b-pagination>
      </b-col>
    </b-row>
    <b-modal
      v-if="modal_selected"
      id="modal-cu"
      :title="editar ? 'Editar modal' : 'Agregar modal'"
      scrollable
      size="lg"
      @hidden="cerrar_modal"
    >
      <b-container fluid>
        <b-form id="modal-form" @submit.prevent="editar ? update() : create()">
          <b-form-select v-model="modal_selected.formulario_id" class="mb-4">
            <template #first>
              <b-form-select-option :value="null" disabled
                >-- Seleccionar formulario --</b-form-select-option
              >
            </template>
            <b-form-select-option
              v-for="formulario in fomularios_filtered"
              :key="'select' + formulario.id"
              :value="formulario.id"
            >
              {{ formulario.descripcion }}
            </b-form-select-option>
          </b-form-select>
          <b-form-group>
            <b-row align-v="center">
              <b-col md="12">
                <b-form-group label="Titulo" label-for="titulo">
                  <b-form-input
                    id="titulo"
                    v-model="modal_selected.titulo"
                    type="text"
                    placeholder="Titulo del modal"
                    required
                  ></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-form-group>
          <b-form-group>
            <b-row align-v="center">
              <b-col md="12">
                <label for="contenido">Contenido</label>
                <ckeditor
                  id="contenido"
                  v-model="modal_selected.contenido"
                  :editor="editor"
                  :config="editorConfig"
                  tabindex="0"
                ></ckeditor>
              </b-col>
            </b-row>
          </b-form-group>
          <b-form-group>
            <b-row align-v="center">
              <b-col md="12">
                <b-form-checkbox
                  v-model="modal_selected.activo"
                  name="check-button"
                  switch
                  style="text-align: end"
                >
                  ¿Activo?
                </b-form-checkbox>
              </b-col>
            </b-row>
          </b-form-group>
        </b-form>
      </b-container>
      <template #modal-footer="{ cancel }">
        <b-btn @click="cancel">Cancel</b-btn>
        <b-btn variant="primary" type="submit" form="modal-form">{{
          editar ? "Guardar" : "Crear"
        }}</b-btn>
      </template>
    </b-modal>
  </div>
</template>

<script>
import Vue from "vue";
import axios from "axios";
import CKEditor from "@ckeditor/ckeditor5-vue";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
Vue.use(CKEditor);

export default {
  name: "Modals",
  data() {
    return {
      loading: false,
      editar: false,
      current_page: 1,
      modals: [],
      formularios: [],
      modal_selected: {
        titulo: "",
        contenido: "<p></p>",
        activo: true,
        formulario_id: null,
      },
      buscar: "",
      total_rows: 1,
      campos: [
        {
          key: "id",
          label: "#",
        },
        {
          key: "titulo",
          label: "Titulo",
        },
        {
          key: "formulario_descripcion",
          label: "Formulario",
        },
        {
          key: "activo",
          label: "Activo",
        },
        {
          key: "acciones",
          label: "Acciones",
        },
      ],
      editor: ClassicEditor,
      editorData: "<p></p>",
      editorConfig: {
        minHeight: "300px",
        toolbar: [
          "heading",
          "|",
          "bold",
          "italic",
          "link",
          "|",
          "bulletedList",
          "numberedList",
          "|",
          "blockQuote",
          "mediaEmbed",
          "insertTable",
          "undo",
          "redo",
        ],
        link: {
          addTargetToExternalLinks: true,
        },
      },
    };
  },

  computed: {
    fomularios_filtered() {
      let formularios_prueba = this.formularios.filter((formulario) =>
        this.modal_selected.formulario_id == formulario.id
          ? true
          : formulario.modal
          ? false
          : true,
      );
      return formularios_prueba;
    },
  },

  mounted() {
    this.fetch_modals();
    this.fetch_formularios();
  },

  methods: {
    cerrar_modal() {
      this.modal_selected = {
        titulo: "",
        contenido: "<p></p>",
        activo: true,
        formulario_id: null,
      };
    },

    create_action() {
      this.editar = false;
      this.$bvModal.show("modal-cu");
    },

    update_action(modal) {
      this.modal_selected = modal;
      this.editar = true;
      this.$bvModal.show("modal-cu");
    },

    delete_action(modal) {
      this.modal_selected = modal;
      this.$bvModal
        .msgBoxConfirm("¿Esta seguro que desea eliminar el modal?", {
          title: "Confirmar accion",
          size: "sm",
          buttonSize: "sm",
          okVariant: "danger",
          okTitle: "SI",
          cancelTitle: "NO",
          footerClass: "p-2",
          hideHeaderClose: false,
          centered: true,
        })
        .then((value) => {
          if (value) {
            this.delete(this.modal_selected.id);
          } else {
            this.modal_selected = {
              titulo: "",
              contenido: "<p></p>",
              activo: true,
              formulario_id: null,
            };
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },

    // create request
    create() {
      this.$bvModal.hide("modal-cu");
      this.loading = true;
      axios
        .post(process.env.VUE_APP_URL + `/modals`, this.modal_selected)
        .then(() => {
          this.loading = false;
          this.$bvModal
            .msgBoxOk("Modal agregado correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.fetch_formularios();
              this.fetch_modals();
            });
        })
        .catch(() => {
          this.loading = false;
          this.$bvModal.msgBoxOk("No se pudo agregar el modal", {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },

    // update request
    update() {
      this.$bvModal.hide("modal-cu");
      this.loading = true;
      axios
        .put(
          process.env.VUE_APP_URL + `/modals/${this.modal_selected.id}`,
          this.modal_selected,
        )
        .then(() => {
          this.loading = false;
          this.$bvModal
            .msgBoxOk("Modal editado correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.fetch_formularios();
              this.fetch_modals();
            });
        })
        .catch(() => {
          this.loading = false;
          this.$bvModal.msgBoxOk("No se pudo editar el modal", {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },

    // delete request
    delete(id) {
      this.loading = true;
      axios
        .delete(process.env.VUE_APP_URL + `/modals/${id}`)
        .then(() => {
          this.loading = false;
          this.$bvModal
            .msgBoxOk("Modal eliminado correctamente", {
              title: "CORRECTO",
              size: "sm",
              buttonSize: "sm",
              okVariant: "success",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            })
            .then(() => {
              this.fetch_formularios();
              this.fetch_modals();
            });
        })
        .catch(() => {
          this.loading = false;
          this.$bvModal.msgBoxOk("No se pudo eliminar el modal", {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },

    // Se recalcula el numero total de filas para la paginacion.
    onFiltered(filteredItems) {
      this.total_rows = filteredItems.length;
    },

    // Se obtienen las solicitudes.
    fetch_modals() {
      this.loading = true;
      axios
        .get(process.env.VUE_APP_URL + "/modals")
        .then((e) => {
          this.modals = e.data;
          this.total_rows = this.modals.length;
          this.modal_selected = {
            titulo: "",
            contenido: "<p></p>",
            activo: true,
            formulario_id: null,
          };
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
          this.$bvModal.msgBoxOk("Error al traer la lista de modals", {
            title: "ERROR",
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            headerClass: "p-2 border-bottom-0",
            footerClass: "p-2 border-top-0",
            centered: true,
          });
        });
    },

    fetch_formularios() {
      axios
        .get(process.env.VUE_APP_URL + "/formularios")
        .then((e) => {
          this.formularios = e.data;
        })
        .catch(() => {
          this.$bvModal.msgBoxOk(
            "Error al traer los formularios, actualice la pagina",
            {
              title: "ERROR",
              size: "sm",
              buttonSize: "sm",
              okVariant: "danger",
              headerClass: "p-2 border-bottom-0",
              footerClass: "p-2 border-top-0",
              centered: true,
            },
          );
        });
    },
  },
};
</script>

<style>
.ck-editor__editable {
  min-height: 200px;
}
</style>
