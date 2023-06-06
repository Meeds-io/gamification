/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
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
import ConnectorsSettings from './components/ConnectorsSettings.vue';
import GamificationConnector from './components/GamificationConnector.vue';
import GamificationConnectorsStatus from './components/GamificationConnectorsStatus.vue';
import GamificationConnectorStatus from './components/GamificationConnectorStatus.vue';

const components = {
  'connectors-settings': ConnectorsSettings,
  'gamification-connector': GamificationConnector,
  'gamification-connectors-status': GamificationConnectorsStatus,
  'gamification-connector-status': GamificationConnectorStatus,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
