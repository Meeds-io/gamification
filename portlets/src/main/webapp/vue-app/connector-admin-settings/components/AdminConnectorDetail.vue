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
  <v-card flat>
    <div class="py-2 py-sm-5 d-flex align-center">
      <v-tooltip :disabled="$root.isMobile" bottom>
        <template #activator="{ on }">
          <v-card
            class="d-flex align-center"
            flat
            v-on="on"
            @click="backToConnectorList">
            <v-btn
              class="width-auto ms-n3"
              icon>
              <v-icon size="18" class="text-color mx-2">fa-arrow-left</v-icon>
            </v-btn>
            <div class="text-color text-header-title">{{ $t('gamification.connectors.label.connectors') }}</div>
          </v-card>
        </template>
        <span>{{ $t('gamification.connectors.details.BackToList') }}</span>
      </v-tooltip>
    </div>
    <div class="d-flex flex-row align-center">
      <div class="d-flex align-center">
        <v-list-item-title class="font-weight-bold text-color">
          {{ title }}
        </v-list-item-title>
      </div>
      <v-spacer />
      <div v-if="idDefaultConnector" class="subtitle-2 text-light-color">{{ $t('gamification.label.defaultConnector') }}</div>
      <template v-else>
        <div :class="connectorActivated && 'primary--text'" class="text-sub-title me-3">{{ connectorStatusLabel }}</div>
        <v-btn
          small
          class="btn btn-primary ms-2"
          @click="openConnectorSettings">
          {{ $t('gamification.connectors.label.configure') }}
        </v-btn>
      </template>
    </div>
    <v-card-text class="px-0 text-sub-title">{{ description }}</v-card-text>
    <v-card
      class="mx-auto"
      flat
      tile>
      <v-list dense>
        <v-subheader class="pb-4 ps-0">
          <v-icon size="20" class="primary--text">fas fa-bolt</v-icon>
          <div class="text-subtitle-1 text-color ps-3">{{ eventsSize }} {{ $t('gamification.label.events') }}</div>
          <v-spacer />
          <v-card
            width="220"
            max-width="100%"
            flat>
            <v-text-field
              v-model="keyword"
              :placeholder="$t('gamification.label.filter.filterEvents')"
              prepend-inner-icon="fa-filter icon-default-color"
              clear-icon="fa-times fa-1x"
              class="pa-0 me-3 my-auto"
              clearable
              hide-details />
          </v-card>
        </v-subheader>
        <gamification-admin-connector-event
          v-for="event in eventToDisplay"
          :key="event.title"
          :event="event"
          class="py-2" />
      </v-list>
    </v-card>
    <div v-if="hasMoreEvents" class="d-flex justify-center py-4">
      <v-btn
        min-width="95%"
        class="btn"
        text
        @click="loadMore">
        {{ $t('rules.loadMore') }}
      </v-btn>
    </div>
  </v-card>
</template>
<script>

export default {
  props: {
    connector: {
      type: Object,
      default: null
    },
    connectorExtension: {
      type: Object,
      default: null
    },
  },
  data() {
    return {
      pageSize: 10,
      keyword: ''
    };
  },
  computed: {
    title() {
      return this.connectorExtension?.componentOptions?.title || '';
    },
    name() {
      return this.connectorExtension?.componentOptions?.name || '';
    },
    description() {
      return this.$t(`${this.connectorExtension?.componentOptions?.description}`);
    },
    connectorActivated() {
      return this.connector?.apiKey && this.connector?.secretKey && this.connector?.redirectUrl && this.connector?.enabled;
    },
    connectorStatusLabel() {
      return this.connectorActivated ? this.$t('gamification.connectors.label.activated') : this.$t('gamification.connectors.label.deactivated');
    },
    events() {
      return this.connector?.events;
    },
    eventsSize() {
      return this.connector?.eventsSize;
    },
    hasMoreEvents() {
      return this.keyword ? this.sortedEvent.length > this.pageSize : this.eventsSize > this.pageSize;
    },
    sortedEvent() {
      let filteredEvent = this.events;
      if (this.keyword) {
        filteredEvent = this.events?.filter(item =>
          this.getEventLabel(item).toLowerCase().includes(this.keyword.toLowerCase())
        );
      }
      return filteredEvent?.sort((a, b) => this.getEventLabel(a).localeCompare(this.getEventLabel(b)));
    },
    eventToDisplay() {
      return this.sortedEvent?.slice(0, this.pageSize);
    },
    idDefaultConnector() {
      return this.connectorExtension?.componentOptions?.defaultConnector;
    },
  },
  methods: {
    backToConnectorList() {
      this.$root.$emit('close-connector-detail');
    },
    openConnectorSettings() {
      this.$root.$emit('open-connector-settings', this.connector, this.connectorExtension);
    },
    loadMore() {
      this.pageSize += this.pageSize;
    },
    getEventLabel(event) {
      return this.$t(`gamification.event.title.${event.title}`);
    }
  }
};
</script>