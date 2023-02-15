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
  <v-expansion-panels
    :value="domainIndexes"
    multiple
    flat>
    <domain-challenges-list
      v-for="domain in domains"
      :key="domain.id"
      :domain="domain"
      :challenges="challengesByDomainId[domain.id]"
      :can-edit-challenge="canEditChallenge" />
  </v-expansion-panels>
</template>
<script>
export default {
  props: {
    domains: {
      type: Array,
      default: function() {
        return [];
      },
    },
    challengesByDomainId: {
      type: Object,
      default: function() {
        return {};
      },
    },
    canEditChallenge: {
      type: Boolean,
      default: false,
    }
  },
  computed: {
    domainIndexes() {
      return this.domains.map((_value, index) => index);
    },
  },
  methods: {
    announcementAdded(announcement) {
      this.listWinners.unshift({'userName': announcement.assignee});
      this.challenge.announcementsCount = this.challenge.announcementsCount + 1;
    },
  }
};
</script>
