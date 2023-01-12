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
            <div class="font-weight-bold tertiary--text">{{ ruleScore }} {{ $t('challenges.label.points') }}</div>
          </div>
        </div>
        <div class="d-flex flex-row pt-3 rich-editor-content">
          <span v-sanitized-html="rule.description"></span>
        </div>
        <div class="d-flex flex-row py-3">
          <img
              :src="program.coverUrl"
              width="50"><span class="font-weight-bold my-auto ms-1">{{ program.title }}</span>
        </div>
        <div v-if="!automaticRule" class="d-flex flex-row py-3">
            <v-icon size="30" class="px-3 primary--text">fas fa-calendar-day</v-icon><span class="my-auto ms-1" v-sanitized-html="DateInfo"></span>
        </div>
      </v-card-text>
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
    DateInfo() {
      const startDate = new Date(this.rule?.startDate);
      const endDate = new Date(this.rule?.endDate);
      if (endDate < new Date()) {
        return this.$t('rule.detail.challengeEnded');
      } else if (startDate > new Date()) {
        const days = Math.round((startDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
        return this.$t('rule.detail.challengeOpenIn', {0: days});
      } else {
        const days = Math.round((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        return this.$t('rule.detail.challengeEndIn', {0: days});
      }
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
  },
  created() {
    this.$root.$on('rule-detail-drawer', (rule) => {
      this.rule = rule;
      this.program = rule?.domainDTO || rule?.program;
      if (this.$refs.ruleDetailDrawer) {
        this.$refs.ruleDetailDrawer.open();
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
    },
    open() {
      if (this.$refs.ruleDetailDrawer){
        this.$refs.ruleDetailDrawer.open();
      }
    }
  }
};
</script>