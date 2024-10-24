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
  <exo-drawer
    id="programDetailDrawer"
    ref="drawer"
    v-model="drawer"
    :right="!$vuetify.rtl"
    :go-back-button="goBackButton">
    <template #title>
      <span
        :title="$t('rule.detail.letsSeeWhatToDo')"
        class="text-truncate">
        {{ $t('programs.label.programSummary') }}
      </span>
    </template>
    <template v-if="!$root.isAnonymous" #titleIcons>
      <v-btn
        :href="programLink"
        icon>
        <v-icon size="24">fa-external-link-alt</v-icon>
      </v-btn>
    </template>
    <template v-if="program && drawer" #content>
      <v-card
        class="pa-5"
        transparent
        flat>
        <div class="text-header-title dark-grey-color text-truncate-2">
          {{ program.title }}
        </div>
        <v-avatar
          id="programImageCover"
          height="auto"
          max-width="100%"
          min-width="100%"
          class="align-start flex-grow-0 content-box-sizing border-color rounded position-relative mt-2"
          tile>
          <img
            id="engagementCenterImgSelectorImg"
            :src="program.coverUrl"
            class="full-width"
            role="presentation"
            alt="">
        </v-avatar>
        <div class="text-header mt-5">
          {{ $t('programs.details.label.description') }}
        </div>
        <div
          class="text-color text-wrap text-start text-break rich-editor-content mt-2"
          v-sanitized-html="program.description">
        </div>
        <div v-if="!$root.isAnonymous" class="d-flex flex-column mt-5">
          <div class="d-flex flex-row">
            <div class="text-header flex-start text-start flex-grow-1 flex-shrink-1 text-truncate">
              {{ $t('programs.details.label.audienceSpace') }}
            </div>
            <div class="text-header flex-end text-end flex-grow-0 flex-shrink-0">
              {{ $t('programs.details.label.programOwners') }}
            </div>
          </div>
          <div class="d-flex flex-row">
            <div class="flex-start text-start flex-grow-1 flex-shrink-1 text-truncate">
              <exo-space-avatar
                v-if="program.space"
                :space="program.space"
                :size="32"
                class="text-truncate mt-2"
                popover />
              <div v-else-if="!program.spaceId">
                {{ $t('programs.details.label.programOpenToParticipate') }}
              </div>
            </div>
            <div class="flex-end text-end flex-grow-0 flex-shrink-0">
              <div v-if="owners.length">
                <engagement-center-avatars-list
                  :avatars="owners"
                  :max-avatars-to-show="3"
                  :avatars-count="ownersCount"
                  :size="25"
                  class="d-flex justify-sm-end pt-2"
                  @open-avatars-drawer="$root.$emit('open-owners-drawer', owners, true)" />
              </div>
              <v-chip v-else class="ms-sm-auto mt-2">
                {{ $t('programs.label.rewardAdmins') }}
              </v-chip>
            </div>
          </div>
        </div>
      </v-card>
      <gamification-rules-overview-widget
        v-if="drawer"
        :program-id="programId"
        class="mb-4 mx-1"
        hide-empty-placeholder
        go-back-button
        load-size
        @rules-size="rulesSize = $event"
        @has-more="hasMore = $event">
        <template #title>
          <div class="text-header text-truncate">
            {{ $t('programs.label.programActions') }}
          </div>
        </template>
        <template v-if="hasMore" #action>
          <v-btn
            height="auto"
            min-width="auto"
            class="pa-0"
            text
            @click="$refs.listDrawer.open()">
            <span class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
          </v-btn>
        </template>
      </gamification-rules-overview-widget>
      <gamification-rules-overview-list-drawer
        v-if="hasMore"
        ref="listDrawer"
        :program-id="programId" />
    </template>
  </exo-drawer>
</template>

<script>
export default {
  props: {
    administrators: {
      type: Array,
      default: null
    },
  },
  data: () => ({
    drawer: false,
    program: null,
    goBackButton: false,
    hasMore: false,
    rulesSize: 0,
  }),
  computed: {
    programLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs/${this.program.id}`;
    },
    programId() {
      return this.program?.id;
    },
    space() {
      return this.program?.space;
    },
    spaceManagers() {
      return this.space?.managers;
    },
    spaceManagersList() {
      return (this.spaceManagers || []).map(owner => ({
        userName: owner
      }));
    },
    addedOwners() {
      return (this.program?.owners || [])
        .map(owner => ({
          userName: owner.remoteId
        }));
    },
    administratorUsernames() {
      return (this.administrators || this.program?.administrators || []).map(admin => ({
        userName: admin
      }));
    },
    owners() {
      return [...this.addedOwners, ...this.spaceManagersList, ...this.administratorUsernames]
        .filter((v, i, array) => array.findIndex(v2 => v?.userName === v2?.userName) === i);
    },
    ownersCount() {
      return this.owners?.length;
    },
  },
  created() {
    this.$root.$on('program-detail-drawer', this.open);
  },
  methods: {
    open(program, goBackButton) {
      this.program = program;
      this.goBackButton = goBackButton || false;
      this.hasMore = false;
      this.rulesSize = 0;
      this.$refs.drawer.open();
      this.$nextTick()
        .then(() => this.collectProgramVisit());
    },
    close() {
      this.$refs.drawer.close();
      this.$nextTick().then(() => this.program = null);
    },
    collectProgramVisit() {
      if (this.programId) {
        document.dispatchEvent(new CustomEvent('exo-statistic-message', {
          detail: {
            module: 'gamification',
            subModule: 'program',
            userId: eXo.env.portal.userIdentityId,
            userName: eXo.env.portal.userName,
            spaceId: this.spaceId || 0,
            operation: 'viewProgram',
            timestamp: Date.now(),
            parameters: {
              programId: this.programId,
              programTitle: this.programTitle,
              programBudget: this.programBudget,
              programType: this.program.type,
              portalName: eXo.env.portal.portalName,
              portalUri: eXo.env.server.portalBaseURL,
              pageUrl: window.location.pathname,
              pageTitle: eXo.env.portal.pageTitle,
              pageUri: eXo.env.portal.selectedNodeUri,
            },
          }
        }));
      }
    },
  }
};
</script>