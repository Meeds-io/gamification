/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
 * contact@meeds.io
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
import ChallengesOverview from './components/ChallengesOverview.vue';
import RuleDetailDrawer from '../engagement-center/components/rules/RuleDetailDrawer.vue';
import AvatarsList from '../engagement-center/components/common/AvatarsList.vue';
import DescriptionEditor from '../engagement-center/components/common/DescriptionEditor.vue';
import * as challengesServices from '../engagement-center/js/challengesServices.js';
import * as engagementCenterUtils from '../engagement-center/js/engagementCenterUtils.js';

const components = {
  'gamification-overview-challenges': ChallengesOverview,
  'rule-details-drawer': RuleDetailDrawer,
  'engagement-center-avatars-list': AvatarsList,
  'engagement-center-description-editor': DescriptionEditor,
};

if (!Vue.prototype.$challengesServices) {
  window.Object.defineProperty(Vue.prototype, '$challengesServices', {
    value: challengesServices,
  });
}
if (!Vue.prototype.$engagementCenterUtils) {
  window.Object.defineProperty(Vue.prototype, '$engagementCenterUtils', {
    value: engagementCenterUtils,
  });
}

for (const key in components) {
  Vue.component(key, components[key]);
}
