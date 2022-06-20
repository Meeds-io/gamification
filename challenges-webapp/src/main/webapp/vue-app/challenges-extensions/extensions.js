extensionRegistry.registerComponent('ActivityContent', 'activity-content-extensions', {
  id: 'announcement',
  isEnabled: (params) => {
    const activity = params && params.activity;
    return activity.type === 'challenges-announcement';
  },
  vueComponent: Vue.options.components['activity-announcement'],
  rank: 5,
});

