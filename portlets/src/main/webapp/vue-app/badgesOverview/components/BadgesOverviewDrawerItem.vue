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
<template>
  <v-list-item
    :disabled="!isAquired"
    class="pa-0 BadgeItem"
    v-on="isCurrent && {
      click: () => $emit('open'),
    }">
    <v-list-item-avatar tile class="BadgeItemAvatarParent mx-2">
      <v-list-item-avatar
        :tile="!isCurrent"
        :class="isCurrent && 'BadgeItemAvatarProfile' || !isAquired && 'badgeNotAquired'"
        class="BadgeItemAvatar mx-2">
        <v-img :src="badgeAvatar" />
      </v-list-item-avatar>
    </v-list-item-avatar>
    <v-list-item-content>
      <v-list-item-title
        :class="{
          'text-disabled-color': !isAquired,
          'primary--text font-weight-bold': isCurrent,
        }">
        {{ title }}
      </v-list-item-title>
    </v-list-item-content>
    <v-list-item-action-text
      :class="{
        'text-disabled-color': !isAquired,
        'primary--text font-weight-bold': isCurrent,
      }"
      class="BadgeItemPoints text-body">
      {{ score }}
      {{ $t('exoplatform.gamification.gamificationinformation.Points') }}
    </v-list-item-action-text>
  </v-list-item>
</template>

<script>
export default {
  props: {
    badge: {
      type: Object,
      default: null,
    },
    currentUser: {
      type: Boolean,
      default: false,
    },
    currentScore: {
      type: Number,
      default: 0,
    },
  },
  computed: {
    isCurrent() {
      return this.badge.isCurrent;
    },
    isAquired() {
      return this.isCurrent || this.currentScore >= this.badge.neededScore;
    },
    badgeAvatar() {
      return this.badge.avatar || `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/reputation/badge/${this.badge.id}/avatar`;
    },
    title() {
      if (this.isCurrent) {
        return this.badge.title;
      } else {
        return this.getLabel('badge.title', this.badge.title);
      }
    },
    description() {
      if (this.isCurrent) {
        return null;
      } else {
        return this.getDescription('badge.description', this.badge.title.replace(' ', ''), this.badge.program, this.badge.description);
      }
    },
    score() {
      if (this.badge.neededScore) {
        return this.badge.neededScore;
      }
      return this.currentScore;
    },
  },
  methods: {
    getLabel(base, key) {
      const label = `${base}.${key}`;
      const translation = this.$t(label);
      return translation === label && key || translation;
    },
    getDescription(base, title, program, value) {
      const label = `${base}.${title}_${program?.title}`;
      return this.$te(label) ? this.$t(label) : value;
    }
  },
};
</script>