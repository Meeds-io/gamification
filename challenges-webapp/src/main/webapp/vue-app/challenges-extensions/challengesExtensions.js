
export function initExtensions() {
  const announcementActivityTypeExtensionOptions = {
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
    getBody: activity => {
      const body = Vue.prototype.$utils.trim( activity.templateParams?.announcementComment || activity.title ) || '';
      return body.includes('<oembed>') && body.split('<oembed>')[0] || body;
    },
    getBodyToEdit: activity => {
      return Vue.prototype.$utils.trim(window.decodeURIComponent(activity.templateParams?.announcementComment || activity.title));
    },
  };

  extensionRegistry.registerExtension('activity', 'type', {
    type: 'challenges-announcement',
    options: announcementActivityTypeExtensionOptions,
  });

}