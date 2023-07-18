<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<script>
export default {
  data: () => ({
    extensionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    actionValueExtensions: {},
  }),
  created() {
    if (this.$root.ruleExtensionsInstalled) {
      this.$root.ruleExtensionsInstalled++;
      return;
    } else {
      this.$root.ruleExtensionsInstalled = 1;
    }
    document.addEventListener(`extension-${this.extensionApp}-${this.actionValueExtensionType}-updated`, this.refreshActionValueExtensions);
    document.addEventListener('announcement-added-event', this.emitRuleAnnouncedInternaly);
    document.addEventListener('rule-created-event', this.emitRuleCreatedInternaly);
    document.addEventListener('rule-updated-event', this.emitRuleUpdatedInternaly);
    document.addEventListener('rule-deleted-event', this.emitRuleDeletedInternaly);
    document.addEventListener('rule-form-drawer-event', this.emitOpenRuleFormDrawerInternaly);
    document.addEventListener('open-owners-drawer-event', this.emitProgramOwnersDrawerInternaly);
    this.$root.$on('announcement-added-event', this.emitRuleAnnouncedGlobally);
    this.$root.$on('open-owners-drawer', this.emitProgramOwnersDrawerGlobally);
    this.$root.$on('rule-created-event', this.emitRuleCreatedGlobally);
    this.$root.$on('rule-updated-event', this.emitRuleUpdatedGlobally);
    this.$root.$on('rule-deleted-event', this.emitRuleDeletedGlobally);
    this.$root.$on('hide-empty-widget', this.hideEmptyWidgetInSpace);
    this.$root.$on('rule-detail-drawer', this.emitOpenRuleDrawerGlobally);
    this.$root.$on('rule-detail-drawer-by-id', this.emitOpenRuleDrawerByIdGlobally);
    this.$root.$on('rule-form-drawer', this.emitOpenRuleFormDrawerGlobally);
    this.init();
  },
  beforeDestroy() {
    this.$root.ruleExtensionsInstalled--;
    if (this.$root.ruleExtensionsInstalled > 0) {
      return;
    }
    document.removeEventListener(`extension-${this.extensionApp}-${this.actionValueExtensionType}-updated`, this.refreshActionValueExtensions);
    document.removeEventListener('announcement-added-event', this.emitRuleAnnouncedInternaly);
    document.removeEventListener('rule-created-event', this.emitRuleCreatedInternaly);
    document.removeEventListener('rule-updated-event', this.emitRuleUpdatedInternaly);
    document.removeEventListener('rule-deleted-event', this.emitRuleDeletedInternaly);
    document.removeEventListener('rule-form-drawer-event', this.emitOpenRuleFormDrawerInternaly);
    this.$root.$off('announcement-added-event', this.emitRuleAnnouncedGlobally);
    this.$root.$off('open-owners-drawer', this.emitProgramOwnersDrawerGlobally);
    this.$root.$off('rule-created-event', this.emitRuleCreatedGlobally);
    this.$root.$off('rule-updated-event', this.emitRuleUpdatedGlobally);
    this.$root.$off('rule-deleted-event', this.emitRuleDeletedGlobally);
    this.$root.$off('hide-empty-widget', this.hideEmptyWidgetInSpace);
    this.$root.$off('rule-detail-drawer', this.emitOpenRuleDrawerGlobally);
    this.$root.$off('rule-detail-drawer-by-id', this.emitOpenRuleDrawerByIdGlobally);
    this.$root.$off('rule-form-drawer', this.emitOpenRuleFormDrawerGlobally);
  },
  methods: {
    init() {
      this.refreshActionValueExtensions();
      const hiddenEmptyWidgetsJson = localStorage.getItem('gamification-overview-hidden-empty-space-widgets') || '{}';
      eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace = JSON.parse(hiddenEmptyWidgetsJson);
    },
    emitRuleDeletedInternaly(event) {
      this.$root.$emit('rule-deleted', event?.detail);
    },
    emitRuleUpdatedInternaly(event) {
      this.$root.$emit('rule-updated', event?.detail);
    },
    emitRuleCreatedInternaly(event) {
      this.$root.$emit('rule-created', event?.detail);
    },
    emitRuleAnnouncedInternaly(event) {
      this.$root.$emit('announcement-added', event?.detail);
    },
    emitOpenRuleFormDrawerInternaly(event) {
      this.$root.$emit('rule-form-drawer-event', event?.detail?.rule, event?.detail?.program);
    },
    emitProgramOwnersDrawerInternaly(event) {
      this.$root.$emit('open-owners-drawer-event', event?.detail?.avatars, event?.detail?.backIcon);
    },
    emitOpenRuleDrawerGlobally(rule, openAnnouncement) {
      document.dispatchEvent(new CustomEvent('rule-detail-drawer-event', {detail: {
        rule,
        openAnnouncement,
      }}));
    },
    emitOpenRuleFormDrawerGlobally(rule, program) {
      document.dispatchEvent(new CustomEvent('rule-form-drawer-event', {detail: {
        rule,
        program,
      }}));
    },
    emitOpenRuleDrawerByIdGlobally(ruleId, openAnnouncement) {
      document.dispatchEvent(new CustomEvent('rule-detail-drawer-by-id-event', {detail: {
        ruleId,
        openAnnouncement,
      }}));
    },
    emitRuleDeletedGlobally(data) {
      document.dispatchEvent(new CustomEvent('rule-deleted-event', {detail: data}));
    },
    emitRuleUpdatedGlobally(data) {
      document.dispatchEvent(new CustomEvent('rule-updated-event', {detail: data}));
    },
    emitRuleCreatedGlobally(data) {
      document.dispatchEvent(new CustomEvent('rule-created-event', {detail: data}));
    },
    emitRuleAnnouncedGlobally(data) {
      document.dispatchEvent(new CustomEvent('announcement-added-event', {detail: data}));
    },
    emitProgramOwnersDrawerGlobally(avatars, backIcon) {
      document.dispatchEvent(new CustomEvent('open-owners-drawer-event', {detail: {
        avatars,
        backIcon,
      }}));
    },
    hideEmptyWidgetInSpace() {
      if (eXo.env.portal.spaceId) {
        eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace[eXo.env.portal.spaceId] = true;
        localStorage.setItem('gamification-overview-hidden-empty-space-widgets', JSON.stringify(eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace));
      }
    },
    refreshActionValueExtensions() {
      const extensions = extensionRegistry.loadExtensions(this.extensionApp, this.actionValueExtensionType);
      let changed = false;
      extensions.forEach(extension => {
        if (extension.type && extension.options && (!this.actionValueExtensions[extension.type] || this.actionValueExtensions[extension.type] !== extension.options)) {
          this.$set(this.actionValueExtensions, extension.type, extension.options);
          changed = true;
        }
      });
      // force update of attribute to re-render switch new extension type
      if (changed) {
        this.$root.actionValueExtensions = Object.assign({}, this.actionValueExtensions);
        this.$root.$emit('rule-actions-updated');
      }
    },
  },
};
</script>