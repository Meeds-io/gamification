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
const defaultActivityExtension = Object.assign({}, activityTypeExtensions.find(extension => extension.type === 'default'));
if (defaultActivityExtension) {
  extensionRegistry.registerExtension('activity', 'type', {
    type: 'gamificationRuleActivity',
    options: Object.assign(defaultActivityExtension.options, {
      hideOnDelete: true,
      init: activity => {
        const lang = window.eXo.env.portal.language;
        const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Challenges-${lang}.json`;
        const ruleId = activity?.templateParams?.ruleId;
        if (!ruleId) {
          return Promise.resolve();
        } else if (activity.ruleFetched) {
          return Promise.resolve(activity.rule);
        } else {
          activity.ruleFetched = true;
          activity.rule = {
            id: ruleId,
            title: activity?.templateParams?.ruleTitle,
          };
          return exoi18n.loadLanguageAsync(lang, url)
            .then(() => ruleId && Vue.prototype.$ruleService.getRuleById(ruleId, {
              lang: eXo.env.portal.language
            }))
            .then(rule => activity.rule = rule)
            .catch(() => activity.ruleNotFound = true);
        }
      },
    }),
  });
}

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
    return activity.type === 'challenges-announcement' && activity.canEdit === 'true' && (!activityTypeExtension.canEdit || activityTypeExtension.canEdit(activity));
  },
  click: (activity) => {
    document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
    return Vue.prototype.$announcementService.cancelAnnouncement(activity.templateParams.announcementId)
      .finally(() => document.dispatchEvent(new CustomEvent('hideTopBarLoading')));
  },
});

extensionRegistry.registerComponent('ActivityStream', 'activity-stream-drawers', {
  id: 'rule-drawers',
  vueComponent: Vue.options.components['engagement-center-rule-drawers'],
  rank: 20,
});
