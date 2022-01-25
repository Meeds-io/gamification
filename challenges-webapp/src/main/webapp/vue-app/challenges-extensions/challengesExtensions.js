
export function initExtensions() {
  const announcementActivityTypeExtensionOptions = {
    canEdit: () => false,
    supportsThumbnail: true,
    useSameViewForMobile: false,
    canShare: () => true,
    thumbnailProperties: {
      height: '90px',
      width: '90px',
      noBorder: true,
    },
    getSourceLink: () => '#',
    getTitle: activity => {
      const announcementAssigneeUsername = activity && activity.templateParams && activity.templateParams.announcementAssigneeUsername && activity.templateParams.announcementAssigneeUsername.split('#') || '';
      const announcementAssigneeFullName = activity && activity.templateParams && activity.templateParams.announcementAssigneeUsername && activity.templateParams.announcementAssigneeFullName.split('#') || '';
      let title = '';
      for (let i=0 ; i < announcementAssigneeUsername.length-1; i++){
        title = `${ title } <a href="${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/${ announcementAssigneeUsername[i] }">${ announcementAssigneeFullName[i] }</a>`;
      }
      return {
        key: 'challenges.succeededChallenge',
        params: { 0: `${ title }` }
      };
    },
    getThumbnail: () => '/challenges/images/challengesAppIcon.png',
    getSummary: activity => activity && activity.templateParams && activity.templateParams.announcementDescription  || '',
    getBody: activity => {
      return Vue.prototype.$utils.trim((activity.templateParams && activity.templateParams.announcementComment)
           || '');
    },
  };

  extensionRegistry.registerExtension('activity', 'type', {
    type: 'challenges-announcement',
    options: announcementActivityTypeExtensionOptions,
  });

}