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
  <v-card
    max-width="380"
    class="rule-prerequisites grey"
    flat>
    <div class="d-flex align-center justify-center pt-4 px-2">
      <img
        v-if="image"
        :src="image"
        alt=""
        width="30">
      <v-icon
        v-else
        class="white--text mb-4"
        size="52">
        {{ icon }}
      </v-icon>
      <div
        v-sanitized-html="prerequisitesTitle"
        class="white--text text-wrap font-weight-bold"></div>
    </div>
    <div class="d-flex flex-column align-center justify-center mt-3">
      <v-card
        max-width="250"
        color="white"
        class="width-fit-content mb-4"
        flat
        rounded>
        <v-btn
          :loading="loading"
          max-width="250"
          class="primary-border-color primary--text no-box-shadow"
          outlined
          @click="connect">
          <v-card
            max-width="220"
            color="transparent"
            class="text-truncate primary--text"
            flat>
            {{ $t('rules.card.connectYourAccount') }}
          </v-card>
        </v-btn>
      </v-card>
    </div>
  </v-card>
</template>
<script>
export default {
  props: {
    extension: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    loading: false,
  }),
  computed: {
    icon() {
      return this.extension?.icon;
    },
    iconColorClass() {
      return this.iconColorClass || this.extension?.iconColorClass || 'white';
    },
    image() {
      return this.extension?.imageCard || this.extension?.image;
    },
    connectorName() {
      return  this.extension?.name.charAt(0).toUpperCase() +  this.extension?.name.slice(1);
    },
    prerequisitesTitle() {
      return this.$t('rules.card.connectYourProfile', {0: this.connectorName});
    }
  },
  methods: {
    connect() {
      this.loading = true;
      this.popup = this.extension.openOauthPopup(this.extension);
      this.$root.$on('gamification-connector-identifier-updated', (updateParams) => {
        if (updateParams?.wsEventName === 'connectorIdentifierUsed') {
          document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
            message: this.$t('gamification.connectors.error.alreadyUsed',  {
              0: this.extension.name,
            }),
            type: 'error',
          }}));
        } else if (updateParams?.wsEventName === 'connectorIdentifierUpdated' && updateParams?.message?.hashmap?.connectorName === this.extension?.name) {
          if (this.$root.connectorValueExtensions) {
            const connectorValueExtensions = this.$root.connectorValueExtensions;
            const extension = Object.values(connectorValueExtensions).find(connectorValueExtension => connectorValueExtension.name === this.extension.name);
            this.$set(extension, 'identifier', updateParams?.message?.hashmap?.connectorIdentifier);
            const index =Object.values(connectorValueExtensions).findIndex(connectorValueExtension => connectorValueExtension.name === this.extension.name);
            connectorValueExtensions[index] = extension;
            this.$root.connectorValueExtensions = Object.assign({}, connectorValueExtensions);
            this.$root.$emit('connector-value-extensions-updated',  connectorValueExtensions);
          }
          this.loading = false;
        }
      });
    },
  },
};
</script>