<template>
  <user-notification-template
    :loading="loading"
    :notification="notification"
    :avatar-url="earnerAvatarUrl"
    :message="message"
    :url="activityUrl">
    <template #actions>
      <div class="text-truncate">
        <rule-icon
          :rule-event="ruleEvent"
          icon-color-class="icon-default-color"
          size="14" />
        {{ ruleTitle }}
      </div>
      <div class="mt-2">
        <v-btn
          :href="replyUrl"
          color="primary"
          elevation="0"
          small
          outlined>
          <v-icon size="14" class="me-1">fa-handshake</v-icon>
          <span class="text-none">
            {{ $t('gamification.label.ruleAnnouncementButton') }}
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
    announcement: null,
  }),
  computed: {
    announcementId() {
      return this.notification?.parameters?.ANNOUNCEMENT_ID;
    },
    rule() {
      return this.announcement?.action;
    },
    ruleTitle() {
      return this.rule?.title;
    },
    ruleEvent() {
      return this.rule?.event;
    },
    activityId() {
      return this.notification?.parameters?.activityId;
    },
    activityUrl() {
      return this.activityId && `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${this.activityId}`;
    },
    replyUrl() {
      return this.activityId && `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${this.activityId}#comment-reply`
        || '#';
    },
    earner() {
      return this.announcement?.earner;
    },
    earnerAvatarUrl() {
      return this.earner?.avatarUrl;
    },
    earnerFullName() {
      return this.earner?.fullName;
    },
    message() {
      return this.earnerFullName && this.$t('Notification.gamification.webNotif.newAchievement', {
        0: `<a class="user-name font-weight-bold">${this.earnerFullName}</a>`,
      }) || '';
    },
  },
  created() {
    this.$realizationService.getRealizationById(this.announcementId)
      .then(announcement => {
        this.announcement = announcement;
        return this.$nextTick();
      })
      .finally(() => this.loading = false);
  },
};
</script>