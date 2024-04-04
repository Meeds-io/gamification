<!--

  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
    v-show="!loading"
    class="white">
    <widget-wrapper :title="title">
      <v-list v-if="!loading" class="pa-0">
        <v-list-item v-if="notConnectedYet" class="pa-0">
          <div class="mx-auto mb-2 ps-0">
            <div class="d-flex flex-column">
              <span class="subtitle-1 text-color">{{ $t('gamification.connectors.profile.notConnectedYet') }}</span>
            </div>
            <div class="d-flex justify-center my-3">
              <v-btn
                :href="settingsLink"
                color="primary"
                small
                depressed>
                {{ $t('gamification.connectors.label.connect') }}
              </v-btn>
            </div>
          </div>
        </v-list-item>
        <gamification-user-connector-item
          v-else
          v-for="connector in enabledConnectedConnectors"
          :key="connector.name"
          :connector="connector"
          :connector-extensions="connectorExtensions" />
      </v-list>
    </widget-wrapper>
  </v-app>
</template>

<script>
export default {
  data: () => ({
    profileOwner: eXo.env.portal.profileOwner,
    isCurrentUserProfile: eXo.env.portal.profileOwner === eXo.env.portal.userName,
    connectors: [],
    connectorExtensions: [],
    loading: true,
  }),
  computed: {
    settingsLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/settings`;
    },
    title() {
      return this.isCurrentUserProfile ? this.$t('gamification.connectors.profile.YourGamifiedProfiles') : this.$t('gamification.connectors.profile.MyGamifiedProfiles');
    },
    enabledConnectors() {
      return  this.connectors?.filter(connector => connector.enabled && connector.apiKey && connector.redirectUrl) || [];
    },
    enabledConnectedConnectors() {
      return this.enabledConnectors
        .filter(connector => connector.identifier?.length) || [];
    },
    notConnectedYet() {
      return !this.loading && !this.enabledConnectedConnectors.length;
    },
    displayed() {
      return this.loading || (this.enabledConnectors?.length && (!this.notConnectedYet || (this.notConnectedYet && this.isCurrentUserProfile)));
    },
  },
  created() {
    document.addEventListener('extension-gamification-connectors-updated', this.refreshUserConnectorList);
    this.init();
  },
  methods: {
    init() {
      this.refreshUserConnectorList();
      // Check connectors status from store
      this.loading = true;
      this.$gamificationConnectorService.getConnectors(this.profileOwner, 'userIdentifier')
        .then(connectors => this.connectors = connectors)
        .finally(() => this.loading = false);
    },
    refreshUserConnectorList() {
      // Get list of connectors from extensionRegistry
      this.connectorExtensions = extensionRegistry.loadExtensions('gamification', 'connectors') || [];
    },
  }
};
</script>
