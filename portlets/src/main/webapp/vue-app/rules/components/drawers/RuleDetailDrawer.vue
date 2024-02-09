<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
<template>
  <exo-drawer
    id="ruleDetailDrawer"
    ref="ruleDetailDrawer"
    v-model="drawer"
    v-draggable="enabled"
    :right="!$vuetify.rtl"
    :go-back-button="goBackButton"
    allow-expand
    @closed="onClose"
    @expand-updated="expanded = $event">
    <template #title>
      <span
        :title="$t('rule.detail.letsSeeWhatToDo')"
        class="text-truncate">
        {{ $t('rule.detail.letsSeeWhatToDo') }}
      </span>
    </template>
    <template v-if="rule && !loading && drawer && !$root.isAnonymous" #titleIcons>
      <rule-favorite-button
        v-if="isProgramMember"
        :rule-id="rule.id"
        :space-id="spaceId"
        :favorite.sync="rule.favorite"
        type="rule"
        type-label="rules"
        class="my-auto" />
      <engagement-center-rule-menu
        :rule="rule" />
    </template>
    <template v-if="rule && !loading && drawer" #content>
      <v-row class="ma-0 py-0 px-2 text-color">
        <v-col :cols="expandedView && 6 || 12">
          <engagement-center-rule-header
            :rule="rule"
            :expanded="expandedView" />
          <v-divider v-if="!expandedView" class="mt-3 mb-1" />
        </v-col>
        <v-col v-if="expandedView" cols="6">
          <engagement-center-rule-program
            :rule="rule" />
        </v-col>

        <v-col
          :cols="expandedView && 6 || 12"
          :class="!expandedView && 'px-8'"
          class="py-0">
          <v-row class="ma-0 pa-0">
            <v-col cols="6" class="px-0">
              <engagement-center-rule-points
                :rule="rule" />
            </v-col>
            <v-col cols="6" class="px-0">
              <engagement-center-rule-achievements
                :rule="rule"
                :class="!expandedView && 'align-end d-flex flex-column'" />
            </v-col>
            <v-col
              v-if="expanded"
              cols="12"
              class="px-0 py-6">
              <engagement-center-rule-description
                :rule="rule" />
            </v-col>
          </v-row>
        </v-col>

        <v-col
          v-if="hasDetails"
          :cols="expandedView && 6 || 12"
          :class="!expandedView && 'px-8'"
          class="py-0">
          <v-row class="ma-0 pa-0">
            <v-col
              v-if="showEndDate"
              cols="6"
              class="px-0 rule-has-end-date">
              <engagement-center-rule-date-end
                :rule="rule" />
            </v-col>
            <v-col
              v-if="hasRecurrence"
              :class="showEndDate && 'align-end'"
              class="px-0 rule-has-recurrence"
              cols="6">
              <engagement-center-rule-recurrence
                :rule="rule"
                :class="showEndDate && !expandedView && 'align-end d-flex flex-column'" />
            </v-col>
            <v-col
              v-if="canAnnounce"
              cols="12"
              class="px-0 rule-can-announce">
              <engagement-center-rule-announcement-form
                ref="ruleAnnouncementForm"
                v-model="validAnnouncement"
                :rule="rule"
                @form-opened="announcementFormOpened = $event"
                @sending="sending = $event"
                @sent="close" />
            </v-col>
            <v-col
              v-else-if="!isDisabled && !isProgramMember"
              cols="12"
              class="px-0 d-flex rule-not-member">
              <v-btn
                v-bind="$root.isAnonymous && {
                  href: participateUrl
                }"
                v-on="!$root.isAnonymous && {
                  click: () => joinAudience()
                }"
                :loading="joining"
                class="primary mx-auto my-4"
                outlined>
                <v-icon size="16">fa-rocket</v-icon>
                <span class="font-weight-bold my-auto ms-3 text-none">
                  {{ $t('rule.detail.participate') }}
                </span>
              </v-btn>
            </v-col>
            <v-col
              v-else-if="hasValidityMessage"
              cols="12"
              class="px-0 py-6 rule-has-validity-message">
              <engagement-center-rule-disabled
                v-if="isDisabled" />
              <engagement-center-rule-connector-prerequisite-item v-else-if="isRequireConnectorConnection" :extension="connectorValueExtension" />
              <engagement-center-rule-invalid-audience
                v-else-if="!isValidAudience" />
              <engagement-center-rule-invalid-whitelist
                v-else-if="!isValidWhitelist" />
              <engagement-center-rule-date-over
                v-else-if="alreadyEnded"
                :rule="rule" />
              <engagement-center-rule-date-start
                v-else-if="!alreadyStarted"
                :rule="rule" />
              <engagement-center-rule-recurrence-validity
                v-else-if="isRecurrenceInvalid"
                :rule="rule" />
              <engagement-center-rule-prerequisites
                v-else-if="isPrerequisitesInvalid"
                :rule="rule"
                class="rounded" />
            </v-col>
          </v-row>
        </v-col>

        <v-col v-if="!expanded" :class="!expandedView && 'px-8'">
          <engagement-center-rule-description
            :rule="rule" />
        </v-col>
      </v-row>
      <v-card
        v-if="isExtensibleEvent"
        class="px-10 py-3"
        flat>
        <extension-registry-components
          :params="eventParams"
          name="engagementCenterEvent"
          type="connector-event-extensions"
          parent-element="div"
          element="div" />
      </v-card>
    </template>
    <template v-if="announcementFormOpened && rule && !loading && drawer" #footer>
      <div class="d-flex mr-2">
        <v-spacer />
        <v-btn
          class="btn me-2"
          @click="close">
          {{ $t('rule.detail.label.cancel') }}
        </v-btn>
        <v-btn
          :disabled="!validAnnouncement"
          :loading="sending"
          class="btn btn-primary"
          @click="createAnnouncement">
          {{ $t('rule.detail.label.announce') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data: () => ({
    drawer: false,
    expanded: false,
    rule: {},
    loading: false,
    joining: false,
    validAnnouncement: false,
    sending: false,
    announcementFormOpened: false,
    appUrl: null,
    drawerUrl: null,
    time: Date.now(),
    interval: null,
    goBackButton: false,
    updatePath: false,
    isPublicSite: eXo.env.portal.portalName === 'public',
    objectType: 'activity',
    connectorsEventComponentsExtensions: [],
    extensionEventApp: 'engagementCenterEvent',
    connectorEventExtensionType: 'connector-event-extensions',
  }),
  computed: {
    now() {
      return this.$root.now || this.time;
    },
    linkBasePath() {
      return this.isPublicSite && '/portal/public/overview/actions' || `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions`;
    },
    participateUrl() {
      return this.rule?.id && `/portal/login?initialURI=${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions/${this.rule.id}`;
    },
    isProgramMember() {
      return this.rule?.userInfo?.member;
    },
    expandedView() {
      return !this.$root.isMobile && this.expanded;
    },
    hasDetails() {
      return this.hasValidityMessage
        || this.canAnnounce
        || this.hasRecurrence
        || this.showEndDate
        || !this.isProgramMember
        || false;
    },
    hasValidityMessage() {
      return this.alreadyEnded
        || !this.alreadyStarted
        || this.isRecurrenceInvalid
        || this.isPrerequisitesInvalid
        || !this.isValidAudience
        || !this.isValidWhitelist
        || this.isDisabled
        || this.isRequireConnectorConnection
        || false;
    },
    hasRecurrence() {
      return this.rule?.recurrence && this.rule?.recurrence !== 'NONE' || false;
    },
    isValidAudience() {
      return this.rule?.userInfo?.context?.validAudience;
    },
    isDisabled() {
      return !this.rule?.enabled
          || this.rule?.deleted
          || !this.rule?.program?.enabled
          || this.rule?.program?.deleted;
    },
    isRequireConnectorConnection() {
      return this.connectorValueExtension?.identifier === null;
    },
    isValidWhitelist() {
      return this.rule?.userInfo?.context?.validWhitelist;
    },
    isRecurrenceInvalid() {
      return this.hasRecurrence && !this.rule?.userInfo?.context?.validRecurrence;
    },
    isPrerequisitesInvalid() {
      const prerequisitesStatus = this.rule?.userInfo?.context?.validPrerequisites;
      if (!prerequisitesStatus || !this.rule?.prerequisiteRules?.length) {
        return false;
      }
      return this.rule.prerequisiteRules.filter(r => !prerequisitesStatus[`${r.id}`]).length;
    },
    startDateMillis() {
      return this.rule?.startDate && new Date(this.rule?.startDate).getTime() || 0;
    },
    endDateMillis() {
      return this.rule?.endDate && new Date(this.rule?.endDate).getTime() || 0;
    },
    alreadyStarted() {
      return !this.startDateMillis || this.startDateMillis < this.now;
    },
    alreadyEnded() {
      return this.endDateMillis && this.endDateMillis < this.now;
    },
    showEndDate() {
      return !this.alreadyEnded && this.alreadyStarted && this.endDateMillis || false;
    },
    canAnnounce() {
      return this.rule?.userInfo?.context?.validForIdentity && this.rule?.type === 'MANUAL';
    },
    enabled() {
      return eXo.env.portal.editorAttachImageEnabled && eXo.env.portal.attachmentObjectTypes?.indexOf(this.objectType) >= 0;
    },
    spaceId() {
      return this.rule?.program?.spaceId;
    },
    eventType() {
      return this.rule?.event?.type;
    },
    connectorValueExtensions() {
      return this.$root.connectorValueExtensions;
    },
    connectorValueExtension() {
      return this.rule?.event?.type
          && Object.values(this.connectorValueExtensions)
            .find(extension => extension?.name === this.eventType)
          || null;
    },
    isExtensibleEvent() {
      return this.connectorsEventComponentsExtensions.map(extension => extension?.componentOptions?.isEnabled(this.eventParams));
    },
    eventParams() {
      return {
        trigger: this.rule?.event?.trigger,
        type: this.rule?.event?.type,
        properties: this.rule?.event?.properties,
        isEditing: false,
      };
    },
  },
  watch: {
    sending() {
      if (this.sending) {
        this.$refs.ruleDetailDrawer.startLoading();
      } else {
        this.$refs.ruleDetailDrawer.endLoading();
      }
    }, 
    loading() {
      if (this.loading) {
        this.$refs.ruleDetailDrawer.startLoading();
      } else {
        this.$refs.ruleDetailDrawer.endLoading();
        this.updatePagePath();
      }
    },
    drawer() {
      if (!this.drawer) {
        this.updatePagePath();
      }
    },
    expanded() {
      this.updatePagePath();
    }, 
    drawerUrl(newVal, oldVal) {
      if (newVal && newVal !== oldVal) {
        window.history.replaceState('challenges', this.$t('program.actions'), newVal);
      }
    },
  },
  created() {
    this.$root.$on('rule-detail-drawer', this.open);
    this.$root.$on('rule-detail-drawer-by-id', this.openById);
    this.$root.$on('rule-form-drawer-opened', this.close);
    this.$root.$on('rule-deleted', this.close);
    document.addEventListener('rule-detail-drawer-event', event => this.open(event?.detail?.rule, event?.detail?.openAnnouncement, event?.detail?.goBackButton, event?.detail?.updatePath));
    document.addEventListener('rule-detail-drawer-by-id-event', event => this.openById(event?.detail?.ruleId, event?.detail?.openAnnouncement));
    document.addEventListener(`component-${this.extensionEventApp}-${this.connectorEventExtensionType}-updated`, this.refreshConnectorExtensions);
    this.refreshConnectorExtensions();
    this.$connectorWebSocket.initCometd(this.handleConnectorIdentifierUpdates);
  },
  methods: {
    open(ruleToDisplay, displayAnnouncementForm, goBackButton, updatePath) {
      this.openById(ruleToDisplay?.id, displayAnnouncementForm, goBackButton, updatePath);
    },
    openById(id, displayAnnouncementForm, goBackButton, updatePath) {
      if (!id || !this.$refs.ruleDetailDrawer) {
        return;
      }
      this.goBackButton = goBackButton || false;
      this.updatePath = updatePath || (!goBackButton && this.isPublicSite);
      this.clear();
      this.checkTime();
      this.rule = {id};
      const hash = window.location.hash;
      this.$refs.ruleDetailDrawer.open();

      this.loading = true;
      return this.$ruleService.getRuleById(id, {
        lang: eXo.env.portal.language,
        expand: 'countRealizations,favorites',
        realizationsLimit: 3,
      })
        .then(rule => {
          this.rule = rule;
          return this.$nextTick();
        })
        .then(() => {
          this.loading = false; // Kept to allow displaying this.$refs.ruleAnnouncementForm
          if (hash === '#expanded') {
            return this.$refs.ruleDetailDrawer.toogleExpand();
          }
          return this.$nextTick();
        })
        .then(() => {
          if (displayAnnouncementForm) {
            window.setTimeout(() => {
              if (this.$refs.ruleAnnouncementForm) {
                this.$refs.ruleAnnouncementForm.displayForm();
              }
            }, 200);
          }
          this.collectRuleVisit();
        })
        .catch(e => {
          this.$refs.ruleDetailDrawer.close();
          const message = `${e}`;
          if (message.includes('403') || message.includes('401')) {
            document.dispatchEvent(new CustomEvent('rule-access-denied', {detail: id}));
          } else {
            document.dispatchEvent(new CustomEvent('rule-not-found', {detail: id}));
          }
        })
        .finally(() => this.loading = false);
    },
    checkTime() {
      if (!this.$root.now) {
        this.time = Date.now();
        this.interval = window.setInterval(() => {
          this.time = Date.now();
        }, 1000);
      }
    },
    close() {
      this.$refs.ruleDetailDrawer.close();
    },
    onClose() {
      this.clear();
      if (this.interval) {
        window.clearInterval(this.interval);
      }
    },
    clear() {
      this.rule = {};
      this.validAnnouncement = false;
      this.sending = false;
      this.announcementFormOpened = false;
      if (this.$refs.ruleAnnouncementForm) {
        this.$refs.ruleAnnouncementForm.clear();
      }
    },
    createAnnouncement() {
      if (this.$refs.ruleAnnouncementForm){
        this.$refs.ruleAnnouncementForm.createAnnouncement();
      }
    },
    updatePagePath() {
      if (window.location.pathname.includes('/actions') || this.updatePath) {
        if (!this.drawer && this.appUrl) {
          this.drawerUrl = this.appUrl;
        } else if (this.drawer && this.rule.id) {
          if (window.location.pathname.includes('/actions')) {
            this.appUrl = `${window.location.pathname.split('/actions')[0]}/actions${window.location.hash?.length && window.location.hash || ''}`;
          } else {
            this.appUrl = window.location.hash && `${window.location.pathname}${window.location.hash}` || window.location.pathname;
          }
          this.drawerUrl = `${this.linkBasePath}/${this.rule.id}${this.expanded && '#expanded' || ''}`;
        }
      }
    },
    collectRuleVisit() {
      if (this.rule?.id) {
        document.dispatchEvent(new CustomEvent('exo-statistic-message', {
          detail: {
            module: 'gamification',
            subModule: 'rule',
            userId: eXo.env.portal.userIdentityId,
            userName: eXo.env.portal.userName,
            spaceId: this.spaceId || 0,
            operation: 'viewRule',
            timestamp: Date.now(),
            parameters: {
              ruleId: this.rule.id,
              ruleTitle: this.rule.title,
              ruleDescription: this.rule.description,
              ruleBudget: this.rule.score || 0,
              ruleType: this.rule.type,
              ruleEvent: this.rule.event?.title,
              programId: this.rule.program?.id,
              programTitle: this.rule.program?.title,
              programType: this.rule.program?.type,
              programBudget: this.rule.program?.rulesTotalScore || 0,
              drawer: 'ruleDetail',
              portalName: eXo.env.portal.portalName,
              portalUri: eXo.env.server.portalBaseURL,
              pageUrl: window.location.pathname,
              pageTitle: eXo.env.portal.pageTitle,
              pageUri: eXo.env.portal.selectedNodeUri,
            },
          }
        }));
      }
    },
    joinAudience() {
      this.joining = true;
      return this.$spaceService.join(this.spaceId)
        .then(() => this.$root.$emit('alert-message', this.$t('rule.detail.successfullyJoined'), 'success'))
        .catch(() => this.$root.$emit('alert-message', this.$t('rule.detail.errorJoining'), 'error'))
        .then(() => this.openById(this.rule.id))
        .finally(() => this.joining = false);
    },
    refreshConnectorExtensions() {
      // Get list of connectors from extensionRegistry
      this.connectorsEventComponentsExtensions = extensionRegistry.loadComponents(this.extensionEventApp) || [];
      this.$emit('event-extension-initialized', this.connectorsEventComponentsExtensions.map(extension => extension.componentOptions.name));
    },
    handleConnectorIdentifierUpdates(updateParams) {
      this.$root.$emit('gamification-connector-identifier-updated', updateParams);
    }
  }
};
</script>