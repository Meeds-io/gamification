<template>
  <v-list-item :href="ruleUrl">
    <v-list-item-icon
      size="28"
      class="ms-n1 me-3 my-auto">
      <v-avatar
        size="28"
        class="rule-icon border-color grey lighten-2">
        <v-icon size="16" class="rule-icon primary--text">
          {{ ruleIcon }}
        </v-icon>
      </v-avatar>
    </v-list-item-icon>
    <v-list-item-content>
      <v-list-item-title class="text-color body-2 text-truncate">{{ ruleTitle }}</v-list-item-title>
    </v-list-item-content>
    <v-list-item-action>
      <favorite-button
        :id="id"
        :favorite="isFavorite"
        :top="top"
        :right="right"
        type="rule"
        type-label="rules"
        @removed="removed"
        @remove-error="removeError" />
    </v-list-item-action>
  </v-list-item>
</template>
<script>
export default {
  props: {
    id: {
      type: String,
      default: () => null,
    },
  },
  data: () => ({ 
    isFavorite: true,
    rule: {},
    extensionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    actionValueExtensions: {},
  }),
  computed: {
    ruleId() {
      return this.rule?.id || this.id;
    },
    ruleTitle() {
      return this.rule?.title || '';
    },
    ruleUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions/${this.ruleId}`;
    },
    ruleIcon() {
      return this.rule?.type === 'AUTOMATIC' ? this.extension?.icon : 'fas fa-trophy';
    },
    extension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.rule.event)) || null;
      }
      return null;
    },
  },
  created() {
    document.addEventListener(`extension-${this.extensionApp}-${this.actionValueExtensionType}-updated`, this.refreshActionValueExtensions);
    this.refreshActionValueExtensions();

    this.$ruleService.getRuleById(this.ruleId, {
      lang: eXo.env.portal.language,
    }).then(rule => this.rule = rule);
  },
  methods: {
    removed() {
      this.isFavorite = !this.isFavorite;
      this.displayAlert(this.$t('Favorite.tooltip.SuccessfullyDeletedFavorite'));
      this.$emit('removed');
      this.$root.$emit('refresh-favorite-list');
    },
    removeError() {
      this.displayAlert(this.$t('Favorite.tooltip.ErrorDeletingFavorite', 'rule'), 'error');
    },
    displayAlert(message, type) {
      document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
        message,
        type: type || 'success',
      }}));
    },
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
