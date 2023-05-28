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
    @closed="clear">
    <template #title>
      <span class="pb-2"> {{ $t('rule.detail.letsSeeWhatToDo') }} </span>
    </template>
    <template v-if="!loading" #content>
      <v-card-text>
        <engagement-center-rule-header
          :rule="rule"
          :action-value-extensions="actionValueExtensions" />
        <engagement-center-rule-description
          :rule="rule" />
        <engagement-center-rule-program
          :rule="rule" />
        <engagement-center-rule-achievements
          :rule="rule" />
        <engagement-center-rule-recurrence
          :rule="rule" />
        <engagement-center-rule-dates
          :rule="rule" />
        <engagement-center-rule-prerequisites
          :rule="rule" />
        <engagement-center-rule-announcement-form
          ref="ruleAnnouncementForm"
          v-model="validAnnouncement"
          :rule="rule"
          @form-opened="announcementFormOpened = $event"
          @sending="sending = $event"
          @sent="close" />
      </v-card-text>
    </template>
    <template v-if="announcementFormOpened" #footer>
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
  props: {
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
    },
  },
  data: () => ({
    drawer: false,
    rule: {},
    loading: false,
    linkBasePath: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`,
    validAnnouncement: false,
    sending: false,
    announcementFormOpened: false,
  }),
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
      }
    }, 
    drawer() {
      this.updatePagePath();
    }, 
  },
  created() {
    this.$root.$on('rule-detail-drawer', this.open);
    this.$root.$on('rule-detail-drawer-by-id', this.openById);
    document.addEventListener('rule-detail-drawer', event => this.open(event?.detail));
    document.addEventListener('rule-detail-drawer-by-id', event => this.openById(event?.detail));
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
      this.rule = {id};

      this.loading = true;
      this.$refs.ruleDetailDrawer.open();
      this.$ruleService.getRuleById(id, 'countRealizations')
        .then(rule => {
          this.rule = rule;
          this.loading = false; // Kept to allow displaying this.$refs.ruleAnnouncementForm
          this.$nextTick().then(() => window.setTimeout(() => {
            if (this.$refs.ruleAnnouncementForm && displayAnnouncementForm) {
              this.$refs.ruleAnnouncementForm.displayForm();
            }
          }, 200));
          this.collectRuleVisit();
        })
        .finally(() => this.loading = false);
    },
    close() {
      this.$refs.ruleDetailDrawer.close();
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
      if (window.location.pathname.indexOf(this.linkBasePath) >= 0) {
        if (!this.drawer) {
          window.history.replaceState('challenges', this.$t('program.actions'), this.linkBasePath);
        } else if (this.rule.id) {
          window.history.replaceState('challenges', this.$t('program.actions'), `${this.linkBasePath}/${this.rule.id}`);
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
            spaceId: this.rule.program?.audienceId || 0,
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