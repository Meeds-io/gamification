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

import * as announcementService from './js/AnnouncementService.js';
import * as programService  from './js/ProgramService.js';
import * as ruleService  from './js/RuleService.js';
import * as realizationService  from './js/RealizationService.js';
import * as utils  from './js/Utils';

if (!Vue.prototype.$programService) {
  window.Object.defineProperty(Vue.prototype, '$programService', {
    value: programService,
  });
}

if (!Vue.prototype.$ruleService) {
  window.Object.defineProperty(Vue.prototype, '$ruleService', {
    value: ruleService,
  });
}

if (!Vue.prototype.$announcementService) {
  window.Object.defineProperty(Vue.prototype, '$announcementService', {
    value: announcementService,
  });
}

if (!Vue.prototype.$realizationService) {
  window.Object.defineProperty(Vue.prototype, '$realizationService', {
    value: realizationService,
  });
}

if (!Vue.prototype.$engagementCenterUtils) {
  window.Object.defineProperty(Vue.prototype, '$engagementCenterUtils', {
    value: utils,
  });
}
