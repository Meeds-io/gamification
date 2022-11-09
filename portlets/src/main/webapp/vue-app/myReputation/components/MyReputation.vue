<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
contact@meeds.io
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
  <gamification-overview-widget>
    <template #title>
      {{ $t('gamification.myReputation.title') }}
    </template>
    <template #content>
      <gamification-overview-widget-row>
        <template #title>
          {{ $t('gamification.myReputation.KudosTitle') }}
        </template>
        <template #content>
          <extension-registry-components
            :params="params"
            name="my-reputation-overview"
            type="my-reputation-item"
            class="d-flex flex-column mx-n4 mt-n4" />
        </template>
      </gamification-overview-widget-row>
    </template>
  </gamification-overview-widget>
</template>
<script>
export default {
  computed: {
    params() {
      return {
        isOverviewDisplay: true,
      };
    }
  }
};
</script>