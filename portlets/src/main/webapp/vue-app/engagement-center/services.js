/**
 *
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
 *
 */

import * as  challengesServices from './js/challengesServices';
import  * as programsServices  from './js/programsServices';
import  * as ruleServices  from './js/RuleServices.js';
import  * as engagementCenterUtils  from './js/engagementCenterUtils';

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

if (!Vue.prototype.$programsServices) {
  window.Object.defineProperty(Vue.prototype, '$programsServices', {
    value: programsServices,
  });
}

if (!Vue.prototype.$ruleServices) {
  window.Object.defineProperty(Vue.prototype, '$ruleServices', {
    value: ruleServices,
  });
}