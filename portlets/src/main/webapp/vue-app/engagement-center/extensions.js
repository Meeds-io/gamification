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
    icon: 'fas fa-stream',
    match: (actionLabel) => streamUserActions.includes(actionLabel),
    getLabel: () => ''
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Profile',
  options: {
    rank: 20,
    icon: 'fas fa-user',
    match: (actionLabel) => profileUserActions.includes(actionLabel),
    getLabel: () => ''
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Space',
  options: {
    rank: 30,
    icon: 'fas fa-layer-group',
    match: (actionLabel) => spaceUserActions.includes(actionLabel),
    getLabel: () => ''
  },
});
