<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <gamification-overview-widget-row
    v-if="displayCompleted"
    class="my-auto">
    <div class="d-flex mx-auto align-center justify-center overflow-hidden">
      <v-card
        class="d-flex flex-column flex-grow-0 me-2 ms-n11 flex-shrink-0 border-box-sizing"
        min-width="45"
        max-width="45"
        min-height="45"
        max-height="45"
        color="transparent"
        flat>
        <v-avatar
          size="45"
          class="rule-program-cover border-color primary--text no-border"
          rounded>
          <v-img :src="completedRulesImageUrl" eager />
        </v-avatar>
      </v-card>
      <div class="flex-shrink-1 text-start text-truncate">
        {{ $t('gamification.overview.rulesOverviewCompletedTitle') }}
      </div>
    </div>
    <div class="d-flex mx-auto align-center justify-center text-subtitle my-4">
      {{ $t('gamification.overview.rulesOverviewCompletedSubtitle') }}
    </div>
  </gamification-overview-widget-row>
  <div v-else class="d-flex flex-column align-center justify-center full-width full-height">
    <v-icon color="tertiary" size="60">fa-rocket</v-icon>
    <span class="text-body mt-5">{{ spaceId && $t('gamification.overview.space.actions') || $t('gamification.overview.actions') }}</span>
  </div>
</template>
<script>
export default {
  props: {
    displayCompleted: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    spaceId: eXo.env.portal.spaceId,
  }),
  computed: {
    completedRulesImageIndex() {
      return (Number(eXo.env.portal.spaceId || '0') + Number(eXo.env.portal.userIdentityId)) % 8 + 1;
    },
    completedRulesImageUrl() {
      return `/gamification-portlets/skin/images/announcement/${this.completedRulesImageIndex}.webp`;
    },
  },
};
</script>