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
  <div class="d-flex mb-1">
    <v-icon
      v-if="reduced || expanded"
      :size="reduced && 60 || 45"
      class="rule-icon align-start primary--text mb-1">
      {{ actionIcon }}
    </v-icon>
    <div v-else class="d-flex flex-column pe-4">
      <v-avatar
        :size="programCoverSize"
        class="rule-program-cover border-color primary--text"
        rounded>
        <v-img :src="programCoverUrl" />
      </v-avatar>
      <v-avatar
        :size="programCoverSize"
        class="rule-icon border-color grey lighten-2 mt-n4 ms-auto me-n4">
        <v-icon size="24" class="rule-icon primary--text">
          {{ actionIcon }}
        </v-icon>
      </v-avatar>
    </div>
    <div class="d-flex flex-column ms-2">
      <div class="rule-title font-weight-bold text-truncate-2 mx-0 my-auto">
        {{ ruleTitle }}
      </div>
      <div v-if="reduced" class="rule-score font-weight-bold text--secondary">
        {{ rule.score }} {{ $t('challenges.label.points') }}
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    expanded: {
      type: Boolean,
      default: false,
    },
    reduced: {
      type: Boolean,
      default: false,
    },
    actionValueExtensions: {
      type: Array,
      default: null,
    },
  },
  data: () => ({
    MAX_LENGTH: 1300,
    editor: false,
    comment: false,
    sending: false,
    templateParams: {},
    userId: eXo.env.portal.userIdentityId,
    username: eXo.env.portal.userName,
    programCoverSize: 35,
  }),
  computed: {
    ruleTitle() {
      const key = `exoplatform.gamification.gamificationinformation.rule.title.${this.rule.title}`;
      if (this.$te(key)) {
        return this.$t(key);
      } else {
        return this.rule.title;
      }
    },
    extension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.rule.event)) || null;
      }
      return null;
    },
    actionIcon() {
      return this.rule?.type === 'AUTOMATIC' ? this.extension?.icon : 'fas fa-trophy';
    },
    programCoverUrl() {
      return this.rule?.program?.coverUrl;
    },
  },
};
</script>