<template>
  <v-menu
    v-model="showMenu"
    :left="!$vuetify.rtl"
    :right="$vuetify.rtl"
    :top="top"
    :bottom="!top"
    offset-y
    attach>
    <template #activator="{ on, attrs }">
      <v-btn
        :class="dark && 'white'"
        :small="small"
        icon
        v-bind="attrs"
        v-on="on">
        <v-icon size="16" class="icon-default-color">
          fas fa-ellipsis-v
        </v-icon>
      </v-btn>
    </template>
    <v-list dense class="pa-0 white">
      <v-list-item
        dense
        @click.prevent.stop="editRule">
        <v-icon size="13" class="icon-default-color me-2">fas fa-edit</v-icon>
        <v-list-item-title class="text-start">
          {{ $t('programs.details.rule.button.edit') }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        dense
        @click.prevent.stop="deleteRule">
        <v-icon size="13" class="icon-default-color me-2">fas fa-trash-alt</v-icon>
        <v-list-item-title class="text-start">
          {{ $t('programs.details.rule.button.delete') }}
        </v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null
    },
    top: {
      type: Boolean,
      default: false,
    },
    dark: {
      type: Boolean,
      default: false,
    },
    small: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    showMenu: false,
  }),
  created() {
    // Workaround to fix closing menu when clicking outside
    $(document).mousedown(() => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
  },
  methods: {
    editRule(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$ruleService.getRuleById(this.rule?.id)
        .then(rule => this.$root.$emit('rule-form-drawer', rule));
    },
    deleteRule(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$root.$emit('rule-delete-confirm', this.rule);
    },
  },
};
</script>