<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
  data: () => ({
    profileOwner: eXo.env.portal.profileOwner,
    connectors: [],
    extensionApp: 'gamification',
    connectorValueExtensionType: 'connectors',
    connectorValueExtensions: {},
  }),
  created() {
    document.addEventListener(`extension-${this.extensionApp}-${this.connectorValueExtensionType}-updated`, this.refreshConnectorValueExtensions);
    document.addEventListener('gamification-connector-identifier-updated', this.refreshConnectorValueExtensions);
    this.refreshConnectorValueExtensions();
  },
  beforeDestroy() {
    document.removeEventListener(`extension-${this.extensionApp}-${this.connectorValueExtensionType}-updated`, this.refreshConnectorValueExtensions);
  },
  methods: {
    refreshConnectorValueExtensions() {
      const extensions = extensionRegistry.loadExtensions(this.extensionApp, this.connectorValueExtensionType);
      this.$gamificationConnectorService.getConnectors(this.profileOwner, 'userIdentifier')
        .then(connectors => {
          this.connectors = connectors;
          extensions.forEach(extension => {
            extension.identifier = this.connectors.find(connector => connector.name === extension.name)?.identifier;
          });
          this.$root.connectorValueExtensions = Object.assign({}, extensions);
        });
    },
  },
};
</script>