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
  <div class="EngagementCenterAssignItem">
    <v-menu
      id="assigneeMenu"
      v-model="globalMenu"
      :content-class="menuId"
      :close-on-content-click="false"
      :nudge-left="0"
      :max-width="300"
      attach
      transition="scale-transition"
      offset-y
      allow-overflow
      eager
      bottom
      :disabled="disabledUnAssign">
      <template #activator="{ on }">
        <div class="d-flex align-" v-on="on">
          <a
            id="EngagementCenterAssignmentBtn"
            :disabled="disabledUnAssign"
            :class="assignButtonClass"
            class="challengeAssignBtn align-end mt-n8">
            <i class="uiIcon uiAddAssignIcon"></i>
            <span class="text-decoration-underline">{{ $t('challenges.label.assign') }}</span>
          </a>
        </div>
      </template>
      <v-card class="pb-4 assignChallengeMenu pa-4">
        <v-card-text class="pb-0 d-flex justify-space-between">
          <span>{{ $t('challenges.label.assignTo') }} :</span>
          <a class="ml-4" @click="assignToMe()">
            <i class="uiIcon uiAssignToMeIcon"></i>
            <span>{{ $t('challenges.label.addMe') }}</span>
          </a>
        </v-card-text>

        <exo-identity-suggester
          ref="challengeSpaceSuggester"
          v-model="invitedChallengeAssignee"
          :search-options="searchOptions"
          :ignore-items="ignoredMembers"
          :type-of-relations="relationsType"
          :width="220"
          name="challengeSpaceAutocomplete"
          include-users />
      </v-card>
    </v-menu>
    <div class="assigneeName">
      <v-chip
        :close="!disabledUnAssign"
        v-for="user in assigneeObj"
        :key="user"
        class="identitySuggesterItem mx-1 mb-2"
        @click:close="removeUser(user)">
        <v-avatar left>
          <v-img :src="user.avatarUrl" />
        </v-avatar>
        <span class="text-truncate">
          {{ user.fullName }}
        </span>
      </v-chip>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: Array,
      default: () => [],
    },
    globalMenu: {
      type: Boolean,
      default: false,
    },
    audience: {
      type: Object,
      default: () => null
    },
    multiple: {
      type: Boolean,
      default: false,
    },
    onlyManager: {
      type: Boolean,
      default: false,
    },
    allowedUsers: {
      type: Array,
      default: null,
    },
  },
  data() {
    return {
      assigneeObj: [],
      invitedChallengeAssignee: [],
      currentUser: eXo.env.portal.userName,
      menu: false,
      space: {},
      menuId: `AssigneeMenu${parseInt(Math.random() * 10000)}`,
      disabledUnAssign: false,
    };
  },
  computed: {
    relationsType(){
      if (this.audience) {
        return 'member_of_space';
      }
      return '';
    },
    searchOptions() {
      const options = {currentUser: ''};
      const spacePrettyName = this.audience?.remoteId || this.audience?.prettyName;
      if (spacePrettyName) {
        options.spaceURL = spacePrettyName;
      }
      return options;
    },
    assignButtonClass(){
      return this.assigneeObj &&  this.assigneeObj.length && 'mt-2';
    },
    ignoredMembers() {
      if (this.assigneeObj){
        return this.assigneeObj.map(obj => `organization:${obj.remoteId}`);
      }
      return '';
    },
  },
  watch: {
    assigneeObj() {
      this.$emit('input', this.assigneeObj);
    },
    invitedChallengeAssignee() {
      const found = this.assigneeObj.find(attendee => attendee.remoteId === (this.invitedChallengeAssignee && this.invitedChallengeAssignee.remoteId));
      if (!found && this.invitedChallengeAssignee && this.invitedChallengeAssignee.remoteId) {
        let newUser = {};
        this.$identityService.getIdentityByProviderIdAndRemoteId('organization',this.invitedChallengeAssignee.remoteId).then(user => {
          newUser= {
            id: user.profile.id,
            remoteId: user.profile.username,
            fullName: user.profile.fullname,
            avatarUrl: user.profile.avatar,
          };
          if (!this.multiple){
            this.assigneeObj =[];
          }
          this.assigneeObj.push(newUser);
          this.$emit('add-manager',newUser.id);
          this.$emit('input', this.assigneeObj);
          this.invitedChallengeAssignee = null;
          this.globalMenu = false;
        }).finally(() => {
          if (newUser){
            this.$emit('add-item',newUser.id);
          }
        });
      }
    },
  },
  mounted() {
    $(document).on('click', (e) => {
      if (e.target && !$(e.target).parents(`.${this.menuId}`).length && !$(e.target).parents('.v-autocomplete__content').length) {
        this.globalMenu = false;
      }
    });
  },
  created() {
    this.assigneeObj = [];
    this.invitedChallengeAssignee = [];
    this.assigneeObj = this.value || [];
    this.space = this.audience;
    document.addEventListener('audienceChanged', event => {
      if (event && event.detail) {
        this.space = event.detail.space;
      } else {
        this.space = {};
      }
    });
  },
  methods: {
    assignToMe() {
      if (!this.isAssigned(this.currentUser)){
        this.$identityService.getIdentityByProviderIdAndRemoteId('organization',this.currentUser).then(user => {
          const newManager= {
            id: user.profile.id,
            remoteId: user.profile.username,
            fullName: user.profile.fullname,
            avatarUrl: user.profile.avatar,
          };
          if (!this.multiple){
            this.assigneeObj =[];
          }
          this.assigneeObj.push(newManager);
          this.$emit('add-item',newManager.id);
          this.$emit('input', this.assigneeObj);
        });
      }
      this.globalMenu = false;
    },
    isAssigned( username) {
      return this.assigneeObj && this.assigneeObj.findIndex(manager => {
        return manager.remoteId === username;
      }) >= 0 ? true : false;
    },
    removeUser(user) {
      if (this.multiple){
        const index =  this.assigneeObj.findIndex(manager => {
          return manager.remoteId === user.remoteId;
        });
        this.assigneeObj.splice(index, 1);
      } else {
        this.assigneeObj = [];
      }
      this.$emit('remove-user', user.id);
      this.$emit('input', this.assigneeObj);
    },
    reset() {
      this.assigneeObj = [];
    },
    setUp(assigneeObj) {
      this.assigneeObj = assigneeObj;
    }
  }
};
</script>
