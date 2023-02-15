<template>
  <exo-drawer
    id="ruleDetailDrawer"
    ref="ruleDetailDrawer"
    :right="!$vuetify.rtl"
    @closed="close">
    <template #title>
      <span class="pb-2"> {{ $t('rule.detail.letsSeeWhatToDo') }} </span>
    </template>
    <template #content>
      <v-card-text>
        <div class="d-flex">
          <v-icon size="60" class="align-start primary--text">{{ actionIcon }}</v-icon>
          <div class="mb-4 ms-4">
            <div class="font-weight-bold mx-0 mt-0 mb-2">{{ ruleTitle }}</div>
            <div class="font-weight-bold text--secondary">{{ ruleScore }} {{ $t('challenges.label.points') }}</div>
          </div>
        </div>
        <div class="d-flex flex-row pt-3 rich-editor-content text-break overflow-hidden">
          <span v-sanitized-html="rule.description"></span>
        </div>
        <div class="d-flex flex-row py-3">
          <v-img
            :src="program.coverUrl"
            :height="programCoverSize"
            :width="programCoverSize"
            :max-height="programCoverSize"
            :max-width="programCoverSize" /><span class="font-weight-bold my-auto ms-3">{{ program.title }}</span>
        </div>
        <div v-if="!automaticRule" class="d-flex flex-row py-3">
          <v-icon size="30" class="primary--text ps-1">fas fa-calendar-day</v-icon><span class="my-auto ms-4" v-sanitized-html="DateInfo"></span>
        </div>
        <div
          v-if="!automaticRule && isActiveRule"
          @click="showEditor"
          class="d-flex flex-row py-3 clickable">
          <v-icon size="30" class="primary--text">fas fa-bullhorn</v-icon><span class="font-weight-bold my-auto ms-3">{{ $t('rule.detail.AnnounceYourAchievement') }} </span>
        </div>
        <div v-show="editor">
          <div v-if="!automaticRule && isActiveRule" class="d-flex flex-row pt-3">
            <v-list-item class="text-truncate px-0">
              <exo-space-avatar
                :space-id="spaceId"
                extra-class="text-truncate"
                :size="30"
                avatar />
              <exo-user-avatar
                :profile-id="username"
                :size="25"
                extra-class="ms-n4 mt-6"
                avatar />
              <v-list-item-content class="py-0 accountTitleLabel text-truncate">
                <v-list-item-title class="font-weight-bold d-flex body-2 mb-0">
                  <exo-space-avatar
                    :space-id="spaceId"
                    extra-class="text-truncate"
                    fullname
                    bold-title
                    link-style
                    username-class />
                </v-list-item-title>
                <v-list-item-subtitle class="d-flex flex-row flex-nowrap">
                  <exo-user-avatar
                    :profile-id="username"
                    extra-class="text-truncate ms-2 me-1"
                    fullname
                    link-style
                    small-font-size
                    username-class />
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div v-if="!automaticRule && isActiveRule" class="py-3">
            <exo-activity-rich-editor
              v-model="comment"
              ref="announcementRichEditor"
              :max-length="MAX_LENGTH"
              :template-params="templateParams"
              :placeholder="$t('rule.detail.announceEditor.placeholder')"
              :tag-enabled="false"
              ck-editor-type="announcementContent"
              class="flex"
              @validity-updated=" validInput = $event" />
          </div>
        </div>
      </v-card-text>
    </template>
    <template v-if="!automaticRule && isActiveRule && editor" slot="footer">
      <div class="d-flex mr-2">
        <v-spacer />
        <v-btn
          class="btn me-2"
          @click="close">
          {{ $t('rule.detail.label.cancel') }}
        </v-btn>
        <v-btn
          :loading="sending"
          :disabled="btnDisabled"
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
    isOverviewDisplayed: {
      type: Boolean,
      default: false,
    },
    tabName: {
      type: String,
      default: null
    },
    tab: {
      type: Number,
      default: () => 0,
    },
  },
  data: () => ({
    rule: {},
    program: {},
    drawer: false,
    username: eXo.env.portal.userName,
    userId: eXo.env.portal.userIdentityId,
    validInput: true,
    comment: null,
    templateParams: {},
    programCoverSize: 35,
    MAX_LENGTH: 1300,
    editorFocus: false,
    editor: false,
    sending: false
  }),
  computed: {
    ruleTitle() {
      const key = `exoplatform.gamification.gamificationinformation.rule.title.${this.rule.title}`;
      if (this.$te(key)) {
        return this.$t(key);
      }
      return this.rule.title;
    },
    extendedActionValueComponent() {
      return this.actionValueExtension && {
        componentName: 'action-value',
        componentOptions: {
          vueComponent: this.actionValueExtension.vueComponent,
        },
      } || null;
    },
    actionValueExtension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.rule.event)) || null;
      }
      return null;
    },
    automaticRule() {
      return this.rule?.type === 'AUTOMATIC';
    },
    actionIcon() {
      return this.automaticRule ? this.actionValueExtension?.icon : 'fas fa-trophy';
    },
    ruleScore() {
      return this.rule?.score || this.rule?.points;
    },
    startDate() {
      return new Date(this.rule?.startDate).getTime() || 0;
    },
    endDate() {
      return new Date(this.rule?.endDate).getTime() || 0;
    },
    isActiveRule() {
      return this.automaticRule || (this.startDate <= Date.now() && this.endDate >= Date.now());
    },
    DateInfo() {
      if (this.isActiveRule) {
        const days = Math.round((this.endDate - Date.now()) / (1000 * 60 * 60 * 24)) + 1;
        return this.$t('rule.detail.challengeEndIn', {0: days});
      } else if (this.endDate < Date.now()) {
        return this.$t('rule.detail.challengeEnded');
      } else {
        const days = Math.round((this.startDate - Date.now()) / (1000 * 60 * 60 * 24)) + 1;
        return this.$t('rule.detail.challengeOpenIn', {0: days});
      }
    },
    spaceId() {
      return this.program?.audienceId;
    },
    btnDisabled() {
      return !this.validInput || !this.comment || !this.comment.length;
    }
  },
  watch: {
    rule() {
      if (!this.isOverviewDisplayed && this.tab === 1) {
        if (this.rule?.id) {
          window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges/${this.rule.id}`);
        } else {
          window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`);
        }
      }
    },
    watch: {
      sending() {
        if (this.sending) {
          this.$refs.ruleDetailDrawer.startLoading();
        } else {
          this.$refs.ruleDetailDrawer.endLoading();
        }
      }
    }
  },
  created() {
    this.$root.$on('rule-detail-drawer', (rule, editorFocus) => {
      this.rule = rule;
      this.editorFocus = editorFocus;
      this.program = rule?.domainDTO || rule?.program;
      if (this.$refs.ruleDetailDrawer) {
        this.$refs.ruleDetailDrawer.open();
        this.$nextTick()
          .then(() => {
            if (!this.automaticRule && this.isActiveRule) {
              this.$refs.announcementRichEditor.initCKEditor();
              if (this.editorFocus) {
                this.editor = true;
                this.setFocus();
              }
            }
          });
      }
    });
    document.addEventListener('rule-detail-drawer', event => {
      if (event && event.detail) {
        this.rule = event.detail;
        this.program = this.rule?.domainDTO || this.rule?.program;
        if (this.$refs.ruleDetailDrawer) {
          this.$refs.ruleDetailDrawer.open();
        }
      }
    });
  },
  methods: {
    close() {
      this.$refs.ruleDetailDrawer.close();
      this.rule = {};
      this.comment = null;
      this.editor = false;
      this.editorFocus = false;
    },
    open() {
      if (this.$refs.ruleDetailDrawer){
        this.$refs.ruleDetailDrawer.open();
      }
    },
    createAnnouncement() {
      const announcement = {
        assignee: this.userId,
        comment: this.comment,
        challengeId: this.rule.id,
        challengeTitle: this.rule.title,
        templateParams: this.templateParams,
      };
      this.sending = true;
      this.$challengesServices.saveAnnouncement(announcement)
        .then(createdAnnouncement => {
          this.$engagementCenterUtils.displayAlert(this.$t('challenges.announcementCreateSuccess'));
          this.$root.$emit('announcement-added', {detail: {
            announcement: createdAnnouncement,
            challengeId: this.rule.id,
          }});
          this.close();
        })
        .catch(e => {
          let msg = '';
          if (e.message === '401' || e.message === '403') {
            msg = this.$t('challenges.permissionDenied');
          } else if (e.message  === '406') {
            msg = this.$t('challenges.challengeNotStartedOrEnded');
          } else  {
            msg = this.$t('challenges.announcementErrorSave');
          }
          this.$engagementCenterUtils.displayAlert(msg, 'error');
        })
        .finally(() => {
          this.sending = false;
        });
    },
    showEditor() {
      this.editor = true;
      this.editorFocus = true;
      this.setFocus();
    },
    setFocus() {
      window.setTimeout(() => {
        const drawerContentElement = document.querySelector('#ruleDetailDrawer .drawerContent');
        drawerContentElement.scrollTo({
          top: drawerContentElement.scrollHeight,
          behavior: 'smooth',
          block: 'start',
        });
        this.$refs.announcementRichEditor.setFocus();
      }, 100);
    }
  }
};
</script>