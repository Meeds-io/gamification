/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

const activityTypeExtensions = extensionRegistry.loadExtensions('activity', 'type');
const defaultActivityOptions = Object.assign({}, activityTypeExtensions.find(extension => extension.type === 'default').options);
const gamificationRuleActivityOptions = Object.assign(defaultActivityOptions, {
  canDelete: () => false,
  canHide: () => true,
  canUnhide: activity => activity?.rule?.activityId === Number(activity.id),
  init: initRuleActivity,
});

const gamificationAnnouncementCommentOptions = Object.assign(defaultActivityOptions, {
  canDelete: (activity, comment) => comment?.canDelete === 'true' && comment?.identity?.remoteId !== eXo.env.portal.userName,
  getTitle: () => '',
  getCommentExtendedComponent: () => ({
    component: Vue.options.components['activity-comment-announcement'],
  }),
  getBodyToEdit: comment => comment.title,
});

extensionRegistry.registerExtension('activity', 'type', {
  type: 'gamificationRuleActivity',
  options: gamificationRuleActivityOptions,
});

extensionRegistry.registerExtension('activity', 'type', {
  type: 'gamificationActionAnnouncement',
  options: gamificationAnnouncementCommentOptions,
});

extensionRegistry.registerComponent('ActivityContent', 'activity-content-extensions', {
  id: 'rule-activity',
  isEnabled: params => params?.activity?.type === 'gamificationRuleActivity' && params?.activity?.templateParams?.ruleId,
  vueComponent: Vue.options.components['rule-activity'],
  rank: 3,
});

extensionRegistry.registerComponent('ActivityContent', 'activity-content-extensions', {
  id: 'announcement',
  init: () => {
    const lang = window.eXo.env.portal.language;
    const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Challenges-${lang}.json`;
    return exoi18n.loadLanguageAsync(lang, url);
  },
  isEnabled: (params) => params?.activity?.type === 'challenges-announcement',
  vueComponent: Vue.options.components['activity-announcement'],
  rank: 5,
});

extensionRegistry.registerExtension('ActivityFavoriteIcon', 'activity-favorite-icon-extensions', {
  id: 'favorite-challenge',
  type: 'challenges-announcement',
  img: '/gamification-portlets/skin/images/challengesAppIcon.png',
});

extensionRegistry.registerExtension('activity', 'action', {
  id: 'cancelAnnouncement',
  labelKey: 'challenges.label.CancelAnnouncement',
  icon: 'fa-undo-alt',
  confirmDialog: true,
  confirmMessageKey: 'challenges.label.confirmCancelAnnouncement',
  confirmTitleKey: 'engagementCenter.button.Confirmation',
  confirmOkKey: 'engagementCenter.button.yes',
  confirmCancelKey: 'engagementCenter.button.cancel',
  rank: 50,
  isEnabled: (activity, activityTypeExtension) => {
    if (activityTypeExtension.canDelete && !activityTypeExtension.canDelete(activity)) {
      return false;
    }
    return activity.type === 'challenges-announcement'
      && activity.canDelete === 'true'
      && activity?.identity?.remoteId === eXo.env.portal.userName;
  },
  click: (activity) => {
    document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
    return Vue.prototype.$announcementService.cancelAnnouncement(activity.templateParams.announcementId)
      .finally(() => document.dispatchEvent(new CustomEvent('hideTopBarLoading')));
  },
});

extensionRegistry.registerExtension('activity', 'comment-action', {
  id: 'cancelAnnouncement',
  labelKey: 'challenges.label.CancelAnnouncement',
  icon: 'fa-undo-alt',
  confirmDialog: true,
  confirmMessageKey: 'challenges.label.confirmCancelAnnouncement',
  confirmTitleKey: 'engagementCenter.button.Confirmation',
  confirmOkKey: 'engagementCenter.button.yes',
  confirmCancelKey: 'engagementCenter.button.cancel',
  rank: 50,
  isEnabled: (activity, comment) => {
    return comment.type === 'gamificationActionAnnouncement'
      && comment.canDelete === 'true'
      && comment?.identity?.remoteId === eXo.env.portal.userName;
  },
  click: (activity, comment) => {
    document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
    return Vue.prototype.$announcementService.cancelAnnouncement(comment.templateParams.announcementId)
      .then(() => document.dispatchEvent(new CustomEvent('activity-comment-deleted', {detail: comment})))
      .finally(() => document.dispatchEvent(new CustomEvent('hideTopBarLoading')));
  },
});

extensionRegistry.registerComponent('ActivityFooter', 'activity-footer-action', {
  id: 'announce',
  init: initRuleActivity,
  isEnabled: params => params?.activity?.type === 'gamificationRuleActivity' && params.activity.rule && !params.activity.originalActivity,
  vueComponent: Vue.options.components['rule-activity-announce-action'],
  rank: 100,
});

extensionRegistry.registerComponent('ActivityStream', 'activity-stream-drawers', {
  id: 'rule-drawers',
  vueComponent: Vue.options.components['engagement-center-rule-extensions'],
  rank: 20,
});

extensionRegistry.registerComponent('activity', 'type', {
  id: 'announcement',
  init: () => {
    const lang = window.eXo.env.portal.language;
    const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Challenges-${lang}.json`;
    return exoi18n.loadLanguageAsync(lang, url);
  },
  isEnabled: (params) => params?.activity?.type === 'challenges-announcement',
  vueComponent: Vue.options.components['activity-announcement'],
  rank: 5,
});

export function initRuleActivity(params) {
  const activity = params?.activity?.id && params?.activity || params;
  if (activity?.type !== 'gamificationRuleActivity' || !activity.templateParams?.ruleId) {
    return Promise.resolve();
  }
  const lang = window.eXo.env.portal.language;
  const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Challenges-${lang}.json`;
  if (activity.rule) {
    return Promise.resolve(activity.rule);
  } else if (!activity.rule && !activity.ruleFetch) {
    activity.ruleFetch = exoi18n.loadLanguageAsync(lang, url)
      .then(() => Vue.prototype.$ruleService.getRuleById(activity.templateParams.ruleId, {
        lang: eXo.env.portal.language,
      }))
      .then(rule => {
        activity.rule = rule;
        delete activity.ruleFetch;
      })
      .catch(() => activity.ruleNotFound = true);
  }
  return Promise.resolve(activity.ruleFetch || activity.rule);
}