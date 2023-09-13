<template>
  <user-notification-template
    :loading="loading"
    :notification="notification"
    :avatar-url="programAvatarUrl"
    :message="message"
    :url="actionUrl"
    space-avatar>
    <template #actions>
      <div class="text-truncate">
        <rule-icon :rule-event="ruleEvent" size="14" />
        {{ ruleTitle }}
      </div>
      <div class="my-1">
        <v-btn
          :href="announceUrl"
          color="primary"
          elevation="0"
          small
          outlined>
          <v-icon size="14" class="me-1">fa-rocket</v-icon>
          <span class="text-none">
            {{ $t('gamification.label.rulePublicationButton') }}
          </span>
        </v-btn>
      </div>
    </template>
  </user-notification-template>
</template>
<script>
export default {
  props: {
    notification: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    loading: true,
    rule: null,
  }),
  computed: {
    ruleId() {
      return this.notification?.parameters?.RULE_ID;
    },
    ruleTitle() {
      return this.rule?.title;
    },
    ruleEvent() {
      return this.rule?.event;
    },
    programAvatarUrl() {
      return this.rule?.program?.avatarUrl;
    },
    actionUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions/${this.rule?.id}`;
    },
    announceUrl() {
      return `${this.actionUrl}#announce`;
    },
    message() {
      return this.rule && this.$t('Notification.gamification.webNotif.newActionAvailable') || '';
    },
  },
  created() {
    this.$ruleService.getRuleById(this.ruleId)
      .then(rule => {
        this.rule = rule;
        return this.$nextTick();
      })
      .finally(() => this.loading = false);
  },
};
</script>