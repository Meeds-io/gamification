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
    :right="!$vuetify.rtl"
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
    <template v-if="rule && !loading && drawer" #titleIcons>
      <rule-favorite-button
        :rule-id="rule.id"
        :space-id="rule.spaceId"
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
              class="px-0">
              <engagement-center-rule-date-end
                :rule="rule" />
            </v-col>
            <v-col
              v-if="hasRecurrence"
              :class="showEndDate && 'align-end'"
              class="px-0"
              cols="6">
              <engagement-center-rule-recurrence
                :rule="rule"
                :class="showEndDate && !expandedView && 'align-end d-flex flex-column'" />
            </v-col>
            <v-col
              v-if="canAnnounce"
              cols="12"
              class="px-0">
              <engagement-center-rule-announcement-form
                ref="ruleAnnouncementForm"
                v-model="validAnnouncement"
                :rule="rule"
                @form-opened="announcementFormOpened = $event"
                @sending="sending = $event"
                @sent="close" />
            </v-col>
            <v-col
              v-else-if="hasValidityMessage"
              cols="12"
              class="px-0 py-6">
              <engagement-center-rule-disabled
                v-if="isDisabled" />
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
    linkBasePath: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`,
    validAnnouncement: false,
    sending: false,
    announcementFormOpened: false,
    appUrl: null,
    drawerUrl: null,
    time: Date.now(),
    interval: null,
  }),
  computed: {
    now() {
      return this.$root.now || this.time;
    },
    expandedView() {
      return !this.$root.isMobile && this.expanded;
    },
    hasDetails() {
      return this.hasValidityMessage
        || this.canAnnounce
        || this.hasRecurrence
        || this.showEndDate
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
      return this.rule?.userInfo?.context?.valid && this.rule?.type === 'MANUAL';
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
    document.addEventListener('rule-detail-drawer-event', event => this.open(event?.detail?.rule, event?.detail?.openAnnouncement));
    document.addEventListener('rule-detail-drawer-by-id-event', event => this.openById(event?.detail?.ruleId, event?.detail?.openAnnouncement));
  },
  methods: {
    open(ruleToDisplay, displayAnnouncementForm) {
      this.openById(ruleToDisplay?.id, displayAnnouncementForm);
    },
    openById(id, displayAnnouncementForm) {
      if (!id || !this.$refs.ruleDetailDrawer) {
        return;
      }
      this.clear();
      this.checkTime();
      this.rule = {id};
      const hash = window.location.hash;
      this.$refs.ruleDetailDrawer.open();

      this.loading = true;
      this.$ruleService.getRuleById(id, {
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
      if (window.location.pathname.includes('/actions')) {
        if (!this.drawer && this.appUrl) {
          this.drawerUrl = this.appUrl;
        } else if (this.drawer && this.rule.id) {
          this.appUrl = `${window.location.pathname.split('/actions')[0]}/actions${window.location.hash === '#all' || window.location.hash === '#trends' ? window.location.hash : ''}`;
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
            spaceId: this.rule.program?.spaceId || 0,
            operation: 'viewRule',
            timestamp: Date.now(),
            parameters: {
              ruleId: this.rule.id,
              ruleTitle: this.rule.title,
              ruleDescription: this.rule.description,
              ruleBudget: this.rule.score || 0,
              ruleType: this.rule.type,
              ruleEvent: this.rule.event,
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
  }
};
</script>