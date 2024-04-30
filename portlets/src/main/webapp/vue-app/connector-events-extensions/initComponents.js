/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import StreamEvent from './components/stream/StreamEvent.vue';
import StreamEventForm from './components/stream/StreamEventForm.vue';
import StreamEventDisplay from './components/stream/StreamEventDisplay.vue';
import StreamEventSpaceItem from './components/stream/StreamEventSpaceItem.vue';
import ProfileEvent from './components/profile/ProfileEvent.vue';
import SpaceEvent from './components/space/SpaceEvent.vue';
import ContributionEvent from './components/contribution/ContributionEvent.vue';

const components = {
  'meeds-stream-event': StreamEvent,
  'meeds-stream-event-form': StreamEventForm,
  'meeds-stream-event-display': StreamEventDisplay,
  'meeds-stream-event-space-item': StreamEventSpaceItem,
  'meeds-profile-event': ProfileEvent,
  'meeds-space-event': SpaceEvent,
  'meeds-contribution-event': ContributionEvent,
};

for (const key in components) {
  Vue.component(key, components[key]);
}