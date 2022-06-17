<template>
  <activity-link
    :activity="activity"
    :activity-type-extension="announcementActivityTypeExtensionOptions"
    :is-activity-detail="isActivityDetail" />
</template>
<script>
export default {
  props: {
    activity: {
      type: Object,
      default: null,
    },
    activityTypeExtension: {
      type: String,
      default: null,
    },
    isActivityDetail: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    announcementActivityTypeExtensionOptions: {
      canEdit: () => true,
      supportsThumbnail: true,
      useSameViewForMobile: true,
      canShare: () => true,
      thumbnailProperties: {
        height: '90px',
        width: '90px',
        noBorder: true,
      },
      getSourceLink: () => '#',
      getTitle: activity => {
        const announcementAssigneeUsername = activity && activity.templateParams && activity.templateParams.announcementAssigneeUsername  || '';
        const announcementAssigneeFullName = activity && activity.templateParams && activity.templateParams.announcementAssigneeFullName  || '';
        const title = `<a href="${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/${ announcementAssigneeUsername}">${ announcementAssigneeFullName}</a>`;

        return {
          key: 'challenges.succeededChallenge',
          params: { 0: `${ title }` }
        };
      },
      getThumbnail: () => '/challenges/images/challengesAppIcon.png',
      getSummary: activity => activity && activity.templateParams && activity.templateParams.announcementChallenge  || activity && activity.templateParams && activity.templateParams.announcementDescription  || '',
    }
  }),
};
</script>