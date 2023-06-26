<template>
  <v-icon :size="size" class="rule-icon primary--text">
    {{ ruleIcon }}
  </v-icon>
</template>
<script>
export default {
  props: {
    ruleEvent: {
      type: String,
      default: () => null,
    },
    size: {
      type: Number,
      default: () => 16,
    },
  },
  data: () => ({
    extensionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    actionValueExtensions: {},
  }),
  computed: {
    extension() {
      const actionValueExtensions = this.$root.actionValueExtensions || this.actionValueExtensions;
      if (actionValueExtensions) {
        return Object.values(actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.ruleEvent)) || null;
      }
      return null;
    },
    ruleIcon() {
      return this.extension?.icon || 'fas fa-trophy';
    },
  },
  created() {
    if (!this.$root.actionValueExtensions) {
      document.addEventListener(`extension-${this.extensionApp}-${this.actionValueExtensionType}-updated`, this.refreshActionValueExtensions);
      this.refreshActionValueExtensions();
    }
  },
  methods: {
    refreshActionValueExtensions() {
      const extensions = extensionRegistry.loadExtensions(this.extensionApp, this.actionValueExtensionType);
      let changed = false;
      extensions.forEach(extension => {
        if (extension.type && extension.options && (!this.actionValueExtensions[extension.type] || this.actionValueExtensions[extension.type] !== extension.options)) {
          this.actionValueExtensions[extension.type] = extension.options;
          changed = true;
        }
      });
      // force update of attribute to re-render switch new extension type
      if (changed) {
        this.actionValueExtensions = Object.assign({}, this.actionValueExtensions);
        this.$root.actionValueExtensions = Object.assign({}, this.actionValueExtensions);
      }
    },
  },
};
</script>
