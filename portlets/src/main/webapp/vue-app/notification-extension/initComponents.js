/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

import RulePublished from './components/RulePublished.vue';
import RuleAnnounced from './components/RuleAnnounced.vue';
import ContributionAccepted from './components/ContributionAccepted.vue';
import ContributionRejected from './components/ContributionRejected.vue';

const components = {
  'user-notification-rule-published': RulePublished,
  'user-notification-rule-announced': RuleAnnounced,
  'user-notification-contribution-accepted': ContributionAccepted,
  'user-notification-contribution-rejected': ContributionRejected,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
