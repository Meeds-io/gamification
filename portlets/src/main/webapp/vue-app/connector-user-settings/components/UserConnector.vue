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
<script>
export default {
  props: {
    connectors: {
      type: Array,
      default: () => [],
    },
    connectorExtensions: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      username: eXo.env.portal.userName,
    };
  },
  created() {
    document.addEventListener('gamification-connectors-refresh', this.refreshUserConnectorList);
    document.addEventListener('extension-gamification-connectors-updated', this.refreshUserConnectorList);
    this.init();
  },
  methods: {
    init() {
      this.refreshUserConnectorList();
      this.connectorExtensions.forEach(extension => {
        if (extension.init) {
          const initPromise = extension.init();
          if (initPromise?.then) {
            return initPromise
              .then(() => this.$nextTick());
          }
        }
      });
      // Check connectors status from store
      this.$gamificationConnectorService.getConnectors(this.username, 'userIdentifier')
        .then(connectors => {
          this.connectors = connectors;
          this.$emit('connectors-loaded', this.connectors, this.connectorExtensions);
        });
    },
    refreshUserConnectorList() {
      // Get list of connectors from extensionRegistry
      this.connectorExtensions = extensionRegistry.loadExtensions('gamification', 'connectors') || [];
    },
  },
};
</script>
