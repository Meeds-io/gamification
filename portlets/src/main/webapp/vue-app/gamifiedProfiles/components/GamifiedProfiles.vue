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
  <v-app
    v-if="displayed"
    class="white">
    <v-toolbar
      id="gamifiedProfilesHeader"
      color="white"
      height="64"
      flat
      class="border-box-sizing">
      <div class="d-flex flex py-3">
        <div class="text-header-title text-sub-title text-no-wrap d-flex align-center">
          {{ Title }}
        </div>
      </div>
    </v-toolbar>
    <v-list class="pa-0">
      <v-list-item class="pa-0">
        <div v-if="notYetConnected" class="mx-auto mb-2 ps-0">
          <div class="d-flex flex-column">
            <span class="subtitle-1 text-color">{{ $t('gamification.connectors.profile.notYetConnected') }}</span>
          </div>
          <div class="d-flex justify-center my-3">
            <v-btn
              color="primary"
              small
              depressed
              @click="gotToUserSettings">
              {{ $t('gamification.connectors.label.connect') }}
            </v-btn>
          </div>
        </div>
        <gamified-profiles-item
          v-else
          v-for="connector in enabledConnectedConnectors"
          :key="connector.name"
          :connector="connector"
          class="mb-2 ps-0 full-width" />
      </v-list-item>
    </v-list>
  </v-app>
</template>

<script>
export default {
  data: () => ({
    profileOwner: eXo.env.portal.profileOwner,
    isCurrentUserProfile: eXo.env.portal.profileOwner === eXo.env.portal.userName,
    connectors: [],
  }),
  computed: {
    Title() {
      return this.isCurrentUserProfile ? this.$t('gamification.connectors.profile.YourGamifiedProfiles') : this.$t('gamification.connectors.profile.MyGamifiedProfiles');
    },
    enabledConnectors() {
      return  this.connectors?.filter(connector => connector.enabled) || [];
    },
    enabledConnectedConnectors() {
      return  this.connectors?.filter(connector => connector.enabled && connector.identifier !== '') || [];
    },
    notYetConnected() {
      return this.enabledConnectors.length > 0 && this.connectors.every(connector => connector.enabled && connector.identifier === '') && this.isCurrentUserProfile;
    },
    displayed() {
      return this.notYetConnected || this.enabledConnectedConnectors.length > 0;
    }
  },
  created() {
    this.refreshUserConnectorList();
  },
  methods: {
    refreshUserConnectorList() {
      // Get list of connectors from extensionRegistry
      const connectors = extensionRegistry.loadExtensions('gamification', 'connectors') || [];
      // Check connectors status from store
      this.$userConnectorService.getUsersConnectorsSetting(this.profileOwner).then(connectorsList => {
        if (connectorsList?.length) {
          connectors.forEach(connector => {
            const connectorObj = connectorsList.find(connectorSettings => connectorSettings.name === connector.name);
            this.$set(connector, 'enabled', connectorObj?.enabled && connectorObj?.apiKey !== '' && connectorObj?.redirectUrl !== '');
            this.$set(connector, 'identifier', connectorObj?.identifier || '');
            this.$set(connector, 'user', eXo.env.portal.userName);
          });
        } else {
          connectors.forEach(connector => (connector.enabled = false));

        }
      });
      this.connectors = connectors;
    },
    gotToUserSettings() {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/settings`;
    },
  }
};
</script>

