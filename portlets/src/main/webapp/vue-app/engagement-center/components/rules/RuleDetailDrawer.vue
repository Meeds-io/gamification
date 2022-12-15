<template>
  <exo-drawer
    id="ruleDetailDrawer"
    ref="ruleDetailDrawer"
    :right="!$vuetify.rtl">
    <template #title>
      <span class="pb-2"> {{ $t('rule.detail.letsSeeWhatToDo')}} </span>
    </template>
    <template #content>
      <v-card-text>
        <div class="d-flex">
          <v-icon size="60" class="align-start primary--text">{{ actionIcon }}</v-icon>
          <div class="mb-4 ms-4">
            <div class="font-weight-bold mx-0 mt-0 mb-2">{{ ruleTitle }}</div>
            <div class="font-weight-bold tertiary--text">{{ rule.score }} {{ $t('challenges.label.points') }}</div>
          </div>
        </div>
        <div class="d-flex flex-row pt-3">
          <span v-sanitized-html="rule.description"></span>
        </div>
        <div class="d-flex flex-row pt-3">
          <img
            :src="program.coverUrl"
            width="50"><span class="font-weight-bold my-auto">{{ program.title }}</span>
        </div>
      </v-card-text>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  props: {
    program: {
      type: Object,
      default: () => null,
    },
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
    },
  },
  data: () => ({
    rule: {},
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
    actionIcon() {
      return this.actionValueExtension?.icon;
    },
  },
  created() {
    this.$root.$on('rule-detail-drawer', (rule) => {
      this.rule = rule;
      if (this.$refs.ruleDetailDrawer) {
        this.$refs.ruleDetailDrawer.open();
      }
    });
  },
  methods: {
    close() {
      this.$refs.ruleDetailDrawer.close();
    },
  }
};
</script>