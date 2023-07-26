<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 - 2023 Meeds Association
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
  <div>
    <div class="d-flex flex-row">
      <v-card-text class="d-flex align-center">{{ $t('gamification.connectors.settings.watchProjects') }}</v-card-text>
      <template v-if="accessToken">
        <v-btn
          class="my-4 me-5"
          icon
          small
          @click="addConnectorHook">
          <v-icon>fas fa-plus</v-icon>
        </v-btn>
        <v-btn
          class="my-4 me-5"
          icon
          small
          @click="editHookSetting">
          <v-icon>fa-sliders-h</v-icon>
        </v-btn>
      </template>
    </div>
    <div v-if="accessToken">
      <div
        v-for="hook in hooks"
        :key="hook.name"
        class="pa-4 d-flex flex-column">
        <gamification-admin-connector-hook
          class="full-height"
          :hook="hook" />
      </div>
    </div>
    <div v-else class="d-flex flex-column align-center justify-center pb-4">
      <v-card-text class="pt-0 dark-grey-color">{{ $t('gamification.connectors.settings.AddToken') }}</v-card-text>
      <v-btn
        small
        class="btn btn-primary my-4"
        @click="editHookSetting">
        <span>
          <v-icon size="14" class="pe-2">fas fa-plus</v-icon> {{ $t('gamification.connectors.settings.button.AddToken') }}
        </span>
      </v-btn>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    accessToken: {
      type: String,
      default: ''
    },
    hooks: {
      type: Array,
      default: () => [],
    },
    connectorExtension: {
      type: Object,
      default: null
    },
  },
  computed: {
    connectorName() {
      return this.connectorExtension?.componentOptions?.name || '';
    },
  },
  methods: {
    addConnectorHook() {
      document.dispatchEvent(new CustomEvent(`gamification-${this.connectorName}-hook-form-open`));
    },
    editHookSetting() {
      document.dispatchEvent(new CustomEvent(`gamification-${this.connectorName}-hook-setting-open`, {detail: this.accessToken}));
    }
  }
};
</script>