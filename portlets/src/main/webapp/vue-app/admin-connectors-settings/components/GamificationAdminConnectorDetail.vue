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
  <v-app>
    <div class="py-2 py-sm-5 d-flex align-center">
      <v-tooltip :disabled="$root.isMobile" bottom>
        <template #activator="{ on }">
          <v-card
            class="d-flex align-center"
            flat
            v-on="on"
            @click="backToProgramList">
            <v-btn
              class="width-auto ms-n3"
              icon>
              <v-icon size="18" class="icon-default-color mx-2">fa-arrow-left</v-icon>
            </v-btn>
            <div class="text-header-title">{{ $t('gamification.connectors.label.connectors') }}</div>
          </v-card>
        </template>
        <span>{{ $t('gamification.connectors.details.BackToList') }}</span>
      </v-tooltip>
    </div>

    <div class="d-flex flex-row">
      <div class="d-flex align-center">
        <v-list-item-title class="font-weight-bold">
          {{ title }}
        </v-list-item-title>
      </div>
      <v-spacer />
      <v-btn
        small
        class="btn btn-primary ms-2"
        @click="openConnectorSettings">
        {{ $t('gamification.connectors.label.connect') }}
      </v-btn>
    </div>
    <div class="my-4">{{ description }}</div>
  </v-app>
</template>
<script>

export default {
  props: {
    connectorSetting: {
      type: Object,
      default: null
    },
  },
  computed: {
    title() {
      return this.connectorSetting?.componentOptions?.title || '';
    },
    description() {
      return this.$t(`${this.connectorSetting?.componentOptions.description}`);
    },
  },
  methods: {
    saveConnectorSetting(event) {
      if (event?.detail) {
        const setting = event?.detail;
        this.$adminConnectorService.saveConnectorSettings(this.name, setting?.apiKey, setting?.secretKey, setting?.redirectUrl);
      }
    },
    backToProgramList() {
      this.$root.$emit('close-connector-detail');
    },
    openConnectorSettings() {
      this.$root.$emit('open-connector-settings', this.connectorSetting);
    },
  }
};
</script>