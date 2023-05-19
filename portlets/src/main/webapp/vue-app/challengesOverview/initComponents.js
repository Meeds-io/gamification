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
import AvatarsList from '../engagement-center/components/common/AvatarsList.vue';
import DescriptionEditor from '../engagement-center/components/common/DescriptionEditor.vue';
import RuleDetailDrawer from '../engagement-center/components/rules/RuleDetailDrawer.vue';
import RuleHeader from '../engagement-center/components/rules/detail/RuleHeader.vue';
import RuleDescription from '../engagement-center/components/rules/detail/RuleDescription.vue';
import RuleProgram from '../engagement-center/components/rules/detail/RuleProgram.vue';
import RuleAnnouncements from '../engagement-center/components/rules/detail/RuleAnnouncements.vue';
import RuleRecurrence from '../engagement-center/components/rules/detail/RuleRecurrence.vue';
import RuleDates from '../engagement-center/components/rules/detail/RuleDates.vue';
import RulePrerequisites from '../engagement-center/components/rules/detail/RulePrerequisites.vue';
import RulePrerequisiteItem from '../engagement-center/components/rules/detail/RulePrerequisiteItem.vue';
import RuleAnnouncementForm from '../engagement-center/components/rules/detail/RuleAnnouncementForm.vue';
import WinnersDrawer from '../engagement-center/components/rules/WinnersDrawer.vue';

const components = {
  'gamification-overview-challenges': ChallengesOverview,
  'engagement-center-rule-detail-drawer': RuleDetailDrawer,
  'engagement-center-rule-header': RuleHeader,
  'engagement-center-rule-description': RuleDescription,
  'engagement-center-rule-program': RuleProgram,
  'engagement-center-rule-announcements': RuleAnnouncements,
  'engagement-center-rule-recurrence': RuleRecurrence,
  'engagement-center-rule-dates': RuleDates,
  'engagement-center-rule-prerequisites': RulePrerequisites,
  'engagement-center-rule-prerequisite-item': RulePrerequisiteItem,
  'engagement-center-rule-announcement-form': RuleAnnouncementForm,
  'engagement-center-winners-details': WinnersDrawer,
  'engagement-center-avatars-list': AvatarsList,
  'engagement-center-description-editor': DescriptionEditor,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
