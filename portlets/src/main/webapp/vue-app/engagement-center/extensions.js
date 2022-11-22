const streamUserActions = ['addActivityOnMyStream', 'addActivityOnNetworkStream', 'addActivityOnSpaceStream', 'addActivityOnSpaceStreamTarget',
  'addActivityTargetUserStream', 'addCommentOnNetworkStream', 'addCommentOnSpaceStream', 'receiveCommentOnNetworkStream', 'receiveCommentOnSpaceStream',
  'likeActivityOnNetworkStream', 'likeActivityOnSpaceStream', 'likeActivityOnSpaceStreamTarget', 'likeActivityTargetUserStream', 'likeComment',
  'likeCommentOnNetworkStream', 'likeCommentOnNetworkStreamTarget', 'likeCommentOnSpaceStream', 'likeCommentOnSpaceStreamTarget', 'uploaddocumentOnNetworkStream'];
const profileUserActions = ['addUserProfileAboutMe', 'addUserProfileAvatar', 'addUserProfileBanner', 'userLogin', 'receiveRelationshipRequest', 'sendRelationshipRequest'];

const spaceUserActions = ['addSpace', 'becomeSpaceManager', 'joinSpace' , ];

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Stream',
  options: {
    rank: 10,
    vueComponent: Vue.options.components['stream-action-value'],
    match: (actionLabel) => streamUserActions.includes(actionLabel),
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Profile',
  options: {
    rank: 20,
    vueComponent: Vue.options.components['profile-action-value'],
    match: (actionLabel) => profileUserActions.includes(actionLabel),
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Space',
  options: {
    rank: 30,
    vueComponent: Vue.options.components['space-action-value'],
    match: (actionLabel) => spaceUserActions.includes(actionLabel),
  },
});
