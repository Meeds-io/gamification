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
  <v-hover v-slot="{ hover }">
    <tr>
      <td class="no-border-bottom ps-0">
        <div @click="openRule" class="clickable d-flex flex-row ma-auto">
          <div class="d-flex flex-column col-2 col-sm-1 pa-0">
            <v-icon size="22" class="primary--text my-auto">
              {{ actionIcon }}
            </v-icon>
          </div>
          <div class="d-flex flex-column col-10 col-sm-11 pa-0 text-truncate">
            <engagement-center-rule-title :rule="rule" />
            <span class="d-sm-none text-caption text-sub-title">{{ rule.score }} {{ $t('challenges.label.points') }}</span>
          </div>
        </div>
      </td>
      <td
        cols="2"
        class="align-center no-border-bottom">
        <div v-if="hover || isMobile" class="d-flex">
          <div v-if="!automaticRule" class="align-center d-none d-sm-block">
            <v-btn
              icon
              class="me-2"
              @click="$root.$emit('rule-detail-drawer', rule, true)">
              <v-icon :size="iconSize">fas fa-bullhorn</v-icon>
            </v-btn>
          </div>
          <v-menu
            v-if="canManageRule"
            v-model="menu"
            :left="!$vuetify.rtl"
            :right="$vuetify.rtl"
            bottom
            offset-y
            attach>
            <template #activator="{ on, attrs }">
              <v-btn
                icon
                class="me-2"
                v-bind="attrs"
                v-on="on"
                @blur="closeMenu">
                <v-icon :size="iconSize" class="icon-default-color">fas fa-ellipsis-v</v-icon>
              </v-btn>
            </template>
            <v-list dense class="pa-0">
              <v-list-item
                dense
                @mousedown="$event.preventDefault()"
                @click="editRule">
                <v-layout class="me-3">
                  <v-icon size="13" class="dark-grey-color pb-2px">fas fa-edit</v-icon>
                </v-layout>
                <v-list-item-title class="d-flex">{{ $t('programs.details.rule.button.edit') }}</v-list-item-title>
              </v-list-item>
              <v-list-item
                v-if="automaticRule"
                dense
                @mousedown="$event.preventDefault()"
                @click="deleteRule">
                <v-layout class="me-3">
                  <v-icon size="13" class="dark-grey-color pb-2px">fas fa-trash-alt</v-icon>
                </v-layout>
                <v-list-item-title class="d-flex">
                  {{ $t('programs.details.rule.button.delete') }}
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>
      </td>
      <td class="no-border-bottom d-none d-sm-inline">
        <div class="align-center">
          {{ rule.score }}
        </div>
      </td>
      <td class="no-border-bottom  d-none d-sm-table-cell">
        <div
          :class="showAllAvatarList && 'AllUsersAvatar'"
          class="winners winnersAvatarsList d-flex flex-nowrap my-2 justify-center">
          <engagement-center-avatars-list
            v-if="haveParticipants"
            :avatars="ruleWinnerAvatars"
            :max-avatars-to-show="maxAvatarsToShow"
            :avatars-count="ruleWinnersCount"
            :size="27"
            @open-avatars-drawer="$root.$emit('open-winners-drawer', rule)" />
          <div v-else>
            <span>
              -
            </span>
          </div>
        </div>
      </td>
    </tr>
  </v-hover>
</template>

<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    canManageRule: {
      type: Boolean,
      default: false,
    },
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
    },
  },
  data() {
    return {
      menu: false,
      maxAvatarsToShow: 4,
    };
  },
  computed: {
    automaticRule() {
      return this.rule?.type === 'AUTOMATIC';
    },
    actionValueExtension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.rule.event)) || null;
      }
      return null;
    },
    actionIcon() {
      return this.automaticRule ? this.actionValueExtension?.icon : 'fas fa-trophy';
    },
    isMobile() {
      return this.$vuetify.breakpoint.xsOnly;
    },
    iconSize() {
      return this.isMobile ? 13 : 16;
    },
    ruleWinnerAvatars() {
      return this.rule?.announcements?.filter(announce => announce.assignee)
        .map(announce => ({
          userName: announce.assignee
        })) || [];
    },
    ruleWinnersCount() {
      return this.rule?.announcementsCount || this.rule?.announcements.length || 0;
    },
    haveParticipants() {
      return this.ruleWinnersCount !== 0;
    }
  },
  methods: {
    closeMenu() {
      this.menu = false;
    },
    editRule() {
      if (this.automaticRule) {
        this.$root.$emit('rule-form-drawer', this.rule);
      } else {
        this.$root.$emit('edit-manuel-rule', this.rule);
      }
    },
    openRule() {
      this.$root.$emit('rule-detail-drawer', this.rule);
    },
    deleteRule() {
      this.$emit('delete-rule', this.rule);
    }
  }

};
</script>