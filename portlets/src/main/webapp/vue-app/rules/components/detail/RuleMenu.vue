<template>
  <v-menu
    v-model="showMenu"
    :left="!$vuetify.rtl"
    :right="$vuetify.rtl"
    :top="top"
    :bottom="!top"
    offset-y
    :attach="attachMenu">
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
        v-if="isEngagementCenterApp"
        v-show="rule.activityId"
        :href="activityLink"
        :title="$t('rule.form.goToActivityTooltip')"
        dense>
        <v-list-item-avatar
          min-width="15"
          width="15"
          class="mt-0 mb-auto">
          <v-icon size="13" class="icon-default-color">fas fa-external-link-alt</v-icon>
        </v-list-item-avatar>
        <v-list-item-title class="text-start">
          {{ $t('rule.form.goToActivity') }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-else
        :href="ruleLink"
        :title="$t('rule.form.goToActionTooltip')"
        dense>
        <v-list-item-avatar
          min-width="15"
          width="15"
          class="mt-0 mb-auto">
          <v-icon size="13" class="icon-default-color">fas fa-external-link-alt</v-icon>
        </v-list-item-avatar>
        <v-list-item-title class="text-start">
          {{ $t('rule.form.goToAction') }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        dense
        @click.prevent.stop="copyLink">
        <v-list-item-avatar
          min-width="15"
          width="15"
          class="mt-0 mb-auto">
          <v-icon size="13" class="icon-default-color">fas fa-copy</v-icon>
        </v-list-item-avatar>
        <v-list-item-title class="text-start">
          {{ $t('programs.details.rule.button.copyLink') }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-if="canEdit"
        dense
        @click.prevent.stop="editRule">
        <v-list-item-avatar
          min-width="15"
          width="15"
          class="mt-0 mb-auto">
          <v-icon size="13" class="icon-default-color">fas fa-edit</v-icon>
        </v-list-item-avatar>
        <v-list-item-title class="text-start">
          {{ $t('programs.details.rule.button.edit') }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-if="canEdit"
        dense
        @click.prevent.stop="enableDisableRule">
        <v-list-item-avatar
          min-width="15"
          width="15"
          class="mt-0 mb-auto">
          <v-icon size="13" class="icon-default-color">fas fa-user-slash</v-icon>
        </v-list-item-avatar>
        <v-list-item-title class="text-start">
          {{ enableDisableButtonLabel }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-if="canEdit"
        dense
        @click.prevent.stop="deleteRule">
        <v-list-item-avatar
          min-width="15"
          width="15"
          class="mt-0 mb-auto">
          <v-icon size="13" class="error-color">fas fa-trash</v-icon>
        </v-list-item-avatar>
        <v-list-item-title class="text-start">
          <span class="error-color">{{ $t('programs.details.rule.button.delete') }}</span>
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
    showActivityLink: {
      type: Boolean,
      default: false,
    },
    attachMenu: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    showMenu: false,
  }),
  computed: {
    ruleId() {
      return this.rule?.id;
    },
    canEdit() {
      return this.rule?.userInfo?.canEdit;
    },
    activityLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.rule.activityId}`;
    },
    ruleLink() {
      return eXo.env.portal.portalName === 'public' && `/portal/public/overview/actions/${this.rule.id}` || `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions/${this.rule.id}`;
    },
    ruleAbsoluteLink() {
      return `${document.location.href.split(eXo.env.portal.context)[0]}${this.ruleLink}`;
    },
    isEngagementCenterApp() {
      return window.location.href.includes('/actions') || window.location.href.includes('/programs') || window.location.href.includes('/achievements');
    },
    isEnabled() {
      return this.rule?.enabled;
    },
    enableDisableButtonLabel() {
      return this.isEnabled ? this.$t('programs.details.rule.button.disable') : this.$t('programs.details.rule.button.enable');
    }
  },
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
    copyLink(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }

      if (!document.getElementById('copyToClipboard')) {
        const copyToClipboardInput = document.createElement('div');
        copyToClipboardInput.innerHTML = `<input id="copyToClipboard" type="text" value="${this.ruleAbsoluteLink}" style="position:absolute;left: -9999px;">`;
        document.body.appendChild(copyToClipboardInput);
      }
      const clipboardInput = document.getElementById('copyToClipboard');
      clipboardInput.value = this.ruleAbsoluteLink;
      clipboardInput.select();
      clipboardInput.setSelectionRange(0, 99999);
      document.execCommand('copy');
      this.$root.$emit('alert-message', this.$t('rules.menu.linkCopied'), 'info');
    },
    deleteRule(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      document.dispatchEvent(new CustomEvent('rule-delete-confirm', {detail: this.rule}));
    },
    enableDisableRule() {
      this.$ruleService.updateRuleStatus(this.ruleId).then(() => {
        this.$root.$emit('alert-message', this.$t('programs.details.ruleStatusUpdateSuccess'), 'success');
        this.$root.$emit('rule-updated-event', this.rule);
      });
    }
  },
};
</script>