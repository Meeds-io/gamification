<template>
  <v-menu
    v-if="showActionsMenu"
    v-model="showMenu"
    :left="!$vuetify.rtl"
    :right="$vuetify.rtl"
    bottom
    offset-y>
    <template #activator="{ on, attrs }">
      <v-btn
        icon
        width="26"
        height="26"
        class="pull-right primary ma-2"
        v-bind="attrs"
        v-on="on"
        @blur="closeMenu">
        <v-icon size="15" color="white">fas fa-ellipsis-v</v-icon>
      </v-btn>
    </template>
    <v-list dense class="pa-0">
      <v-list-item
        dense
        @mousedown="$event.preventDefault()"
        @click="editProgram">
        <v-layout class="me-3">
          <v-icon size="13" class="icon-default-color pb-2px">fas fa-edit</v-icon>
        </v-layout>
        <v-list-item-title class="d-flex">{{ $t('programs.button.editProgram') }}</v-list-item-title>
      </v-list-item>
      <v-list-item
        dense
        @mousedown="$event.preventDefault()"
        @click="deleteProgram">
        <v-layout class="me-3">
          <v-icon size="13" class="icon-default-color pb-2px">fas fa-trash-alt</v-icon>
        </v-layout>
        <v-list-item-title class="d-flex">
          {{ $t('programs.button.deleteProgram') }}
        </v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
export default {
  props: {
    program: {
      type: Object,
      default: null
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    showMenu: false,
  }),
  computed: {
    showActionsMenu() {
      return this.isAdministrator || this.program?.userInfo?.canEdit;
    },
  },
  created() {
    $(document).mousedown(() => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
  },
  methods: {
    editProgram(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$root.$emit('program-form-open', this.program);
    },
    deleteProgram(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$root.$emit('delete-program', this.program);
    },
    closeMenu() {
      this.showMenu = false;
    },
  }
};
</script>