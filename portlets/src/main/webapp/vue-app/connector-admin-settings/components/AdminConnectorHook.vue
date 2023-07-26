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
  <v-card
    flat>
    <div class="d-flex flex-row">
      <div class="d-flex">
        <div class="d-flex align-center">
          <v-img
            :src="avatar"
            :alt="organizationName"
            height="60"
            width="60" />
        </div>
        <v-list class="ms-3">
          <v-list-item-title>
            {{ organizationTitle }}
          </v-list-item-title>
          <v-list-item-subtitle class="text-truncate d-flex caption mt-1">{{ description }}</v-list-item-subtitle>
          <div class="d-flex flex-row">
            <span class="text-truncate d-flex caption d-content pt-2px"> {{ watchedByLabel }} </span>
            <exo-user-avatar
              :profile-id="watchedBy"
              extra-class="ms-1"
              fullname
              popover />
          </div>
        </v-list>
      </div>
      <v-spacer />
      <div class="d-flex align-center px-2">
        <v-btn
          small
          icon
          @click="deleteConfirmDialog">
          <v-icon class="error-color" size="18">fas fa-trash-alt</v-icon>
        </v-btn>
      </div>
    </div>
    <exo-confirm-dialog
      ref="deleteHookConfirmDialog"
      :message="$t('gamification.connectors.message.confirmDeleteConnectorHook')"
      :title="$t('gamification.connectors.title.confirmDeleteConnectorHook')"
      :ok-label="$t('confirm.yes')"
      :cancel-label="$t('confirm.no')"
      @ok="deleteHook" />
  </v-card>
</template>

<script>
export default {
  props: {
    hook: {
      type: Object,
      default: null
    },
  },
  computed: {
    organizationName() {
      return this.hook?.name;
    },
    organizationTitle() {
      return this.hook?.title;
    },
    connectorName() {
      return this.hook?.connectorName;
    },
    description() {
      return this.hook?.description;
    },
    watchedDate() {
      return new Date(this.hook?.watchDate);
    },
    watchedByLabel() {
      return this.$t('gamification.connectors.settings.watchedBy', {0: this.$dateUtil.formatDateObjectToDisplay(this.watchedDate, {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      }, eXo.env.portal.language)});
    },
    watchedBy() {
      return this.hook?.watchedBy;
    },
    avatar() {
      return `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/hooks/${this.connectorName}/${this.organizationName}/avatar`;
    }
  },
  methods: {
    deleteConfirmDialog() {
      this.$refs.deleteHookConfirmDialog.open();
    },
    deleteHook() {
      return this.$gamificationConnectorService.deleteConnectorHook(this.connectorName, this.organizationName).then(() => {
        this.$root.$emit('gamification-connector-hooks-updated');
      });
    }
  }
};
</script>