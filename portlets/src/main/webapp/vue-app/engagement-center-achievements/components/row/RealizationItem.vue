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
  <v-hover v-slot="{hover}">
    <tr :id="`GamificationRealizationItem${realization.id}`">
      <td>
        <div class="d-flex">
          <div v-if="isAutomaticType" class="text-truncate">
            <engagement-center-realizations-automatic-action-value
              v-if="actionValueExtension"
              :action-label="actionLabel"
              :action-u-r-l="realizationLink"
              :event-name="eventName" />
            <a
              v-else
              :href="realization.url"
              :class="actionLabelClass"
              class="text-color">
              <span class="actionDescription">
                {{ actionLabel }}
              </span>
            </a>
          </div>
          <engagement-center-realizations-manual-action-value
            v-else
            :action-label="actionLabel"
            :action-u-r-l="realizationLink"
            class="text-truncate" />
          <v-tooltip
            v-if="actionLabelChanged"
            z-index="4"
            max-width="300"
            bottom>
            <template #activator="{ on, attrs }">
              <v-icon
                size="14"
                class="primary--text ms-1"
                v-bind="attrs"
                v-on="on">
                fas fa-info-circle
              </v-icon>
            </template>
            <span>{{ $t('realization.label.previouslyNamed') }}: {{ realizationActionLabel }}</span>
          </v-tooltip>
          <v-card
            v-if="hover && ruleId"
            color="transparent"
            width="28"
            class="position-relative my-auto ms-2"
            flat>
            <v-tooltip
              z-index="4"
              max-width="300"
              bottom>
              <template #activator="{ on, attrs }">
                <v-btn
                  v-bind="attrs"
                  v-on="on"
                  class="btn absolute-vertical-center"
                  icon
                  absolute
                  small
                  @click="filterByAction">
                  <v-icon size="14">{{ isFilteredByAction && 'fa-search-minus' || 'fa-search-plus' }}</v-icon>
                </v-btn>
              </template>
              <span>{{ isFilteredByAction && $t('realization.label.removeThisActionToFilter') || $t('realization.label.addThisActionToFilter') }}</span>
            </v-tooltip>
          </v-card>
        </div>
      </td>
      <td>
        <div class="d-flex">
          <v-tooltip bottom>
            <template #activator="{ on }">
              <component
                v-on="on"
                v-bind="programUrl && {
                  href: programUrl
                }"
                :is="programUrl && 'a' || 'div'"
                class="text-truncate">
                <div class="text-truncate">{{ programTitle }}</div>
              </component>
            </template>
            <span>{{ programTitle }}</span>
          </v-tooltip>
          <v-tooltip
            v-if="programLabelChanged"
            z-index="4"
            max-width="300"
            bottom>
            <template #activator="{ on, attrs }">
              <v-icon
                size="14"
                class="primary--text ms-1"
                v-bind="attrs"
                v-on="on">
                fas fa-info-circle
              </v-icon>
            </template>
            <span>{{ $t('realization.label.previouslyNamed') }}: {{ programLabel }}</span>
          </v-tooltip>
          <v-card
            v-if="hover && programId"
            color="transparent"
            width="28"
            class="position-relative my-auto ms-2"
            flat>
            <v-tooltip
              z-index="4"
              max-width="300"
              bottom>
              <template #activator="{ on, attrs }">
                <v-btn
                  v-bind="attrs"
                  v-on="on"
                  class="btn absolute-vertical-center"
                  icon
                  absolute
                  small
                  @click="filterByProgram">
                  <v-icon size="14">{{ isFilteredByProgram && 'fa-search-minus' || 'fa-search-plus' }}</v-icon>
                </v-btn>
              </template>
              <span>{{ isFilteredByProgram && $t('realization.label.removeThisProgramToFilter') || $t('realization.label.addThisProgramToFilter') }}</span>
            </v-tooltip>
          </v-card>
        </div>
      </td>
      <td class="wrap">
        <v-tooltip bottom>
          <template #activator="{ on, attrs }">
            <div
              v-bind="attrs"
              v-on="on">
              <date-format
                :format="dateFormat"
                :value="realization.createdDate" />
            </div>
          </template>
          <span>           
            <date-format
              :format="tooltipDateFormat"
              :value="realization.createdDate" />
          </span>
        </v-tooltip>
      </td>
      <td v-if="isAdministrator" class="d-flex align-center justify-center">
        <user-avatar
          :name="earnerFullName"
          :avatar-url="earnerAvatarUrl"
          :profile-id="earnerRemoteId"
          :popover="earnerRemoteId"
          :size="28"
          extra-class="d-inline-block"
          link-style
          avatar />
        <v-card
          v-if="hover && earnerId"
          color="transparent"
          class="position-relative my-auto"
          flat>
          <v-tooltip
            z-index="4"
            max-width="300"
            bottom>
            <template #activator="{ on, attrs }">
              <v-btn
                v-bind="attrs"
                v-on="on"
                class="btn absolute-vertical-center ms-2"
                icon
                absolute
                small
                @click="filterByEarner">
                <v-icon size="14">{{ isFilteredByEarner && 'fa-search-minus' || 'fa-search-plus' }}</v-icon>
              </v-btn>
            </template>
            <span>{{ isFilteredByEarner && $t('realization.label.removeThisEarnerToFilter') || $t('realization.label.addThisEarnerToFilter') }}</span>
          </v-tooltip>
        </v-card>
      </td>
      <td class="text-truncate align-center">
        <v-tooltip bottom>
          <template #activator="{ on, attrs }">
            <v-icon
              :class="statusIconClass"
              class="me-1"
              size="16"
              v-bind="attrs"
              v-on="on">
              {{ actionTypeIcon }}
            </v-icon>
          </template>
          <span>{{ isAutomaticTypeLabel }}</span>
        </v-tooltip>
      </td>
      <td class="text-truncate align-center">
        {{ score }}
      </td>
      <td class="text-truncate align-center">
        <v-tooltip bottom>
          <template #activator="{ on, attrs }">
            <v-icon
              :class="statusIconClass"
              class="me-1"
              size="16"
              v-bind="attrs"
              v-on="on">
              {{ statusIcon }}
            </v-icon>
          </template>
          <span>{{ statusLabel }}</span>
        </v-tooltip>
      </td>
      <td v-if="isAdministrator" class="text-truncate actions align-center">
        <v-menu
          v-if="hasActions"
          v-model="menu"
          :left="!$vuetify.rtl"
          :right="$vuetify.rtl"
          bottom
          offset-y
          attach>
          <template #activator="{ on, attrs }">
            <v-btn
              icon
              small
              class="me-2"
              v-bind="attrs"
              v-on="on">
              <v-icon size="16" class="icon-default-color">fas fa-ellipsis-v</v-icon>
            </v-btn>
          </template>
          <v-list dense class="pa-0">
            <template>
              <v-list-item
                v-if="canAccept"
                dense
                @click="updateRealizationStatus('ACCEPTED')">
                <v-icon size="13" class="icon-default-color">fas fa-check</v-icon>
                <v-list-item-title class="text-justify ps-3">
                  {{ $t('realization.label.accept') }}
                </v-list-item-title>
              </v-list-item>
              <v-list-item
                v-if="canReject"
                dense
                @click="updateRealizationStatus('REJECTED')">
                <v-icon size="13" class="icon-default-color">fas fa-ban</v-icon>
                <v-list-item-title class="text-justify ps-3">
                  {{ $t('realization.label.reject') }}
                </v-list-item-title>
              </v-list-item>
            </template>
          </v-list>
        </v-menu>
      </td>
    </tr>
  </v-hover>
</template>
<script>
export default {
  props: {
    realization: {
      type: Object,
      default: null,
    },
    dateFormat: {
      type: Object,
      default: null,
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    },
    filteredRuleIds: {
      type: Array,
      default: null,
    },
    filteredProgramIds: {
      type: Array,
      default: null,
    },
    filteredEarnerIds: {
      type: Array,
      default: null,
    },
  },
  data: () => ({
    menu: false,
    realizationLink: null,
    tooltipDateFormat: {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    },
  }),
  computed: {
    earnerFullName() {
      return this.realization?.earner?.fullName;
    },
    earnerAvatarUrl() {
      return this.realization?.earner?.avatarUrl;
    },
    earnerRemoteId() {
      return this.realization?.earner?.remoteId;
    },
    earner() {
      return this.realization?.earner;
    },
    earnerId() {
      return this.realization?.earner?.id;
    },
    isAutomaticType() {
      return !!this.eventName;
    },
    program() {
      return this.realization?.program;
    },
    programId() {
      return this.program?.id;
    },
    rule() {
      return this.realization?.action;
    },
    ruleId() {
      return this.rule?.id;
    },
    isFilteredByAction() {
      return this.ruleId && this.filteredRuleIds && this.filteredRuleIds.find(id => id === this.ruleId);
    },
    isFilteredByProgram() {
      return this.programId && this.filteredProgramIds && this.filteredProgramIds.find(id => id === this.programId);
    },
    isFilteredByEarner() {
      return this.earnerId && this.filteredEarnerIds && this.filteredEarnerIds.find(id => id === this.earnerId);
    },
    actionLabel() {
      const actionLabel = this.realization?.action?.title || this.realizationActionLabel;
      if (actionLabel) {
        return actionLabel;
      } else {
        return this.eventName && this.$t(`gamification.event.title.${this.eventName}`);
      }
    },
    eventName() {
      return this.realization?.action?.event?.title;
    },
    programTitle() {
      return this.program?.title || this.programLabel;
    },
    programUrl() {
      return this.program && `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs/${this.program?.id}`;
    },
    score() {
      return this.realization?.score || '-';
    },
    status() {
      return this.realization.status;
    },
    statusIcon() {
      return this.status === 'ACCEPTED' ? 'fas fa-check-circle' : 'fas fa-times-circle';
    },
    statusIconClass() {
      return this.status === 'ACCEPTED' ? 'primary--text' : 'secondary--text';
    },
    actionTypeIcon() {
      return this.isAutomaticType ? 'fas fa-cogs' : 'fas fa-hand-pointer';
    },
    actionLabelClass() {
      return !this.realization.url && 'defaultCursor' || '';
    },
    canReject() {
      return this.status === 'ACCEPTED';
    },
    canAccept() {
      return this.status === 'REJECTED';
    },
    hasActions() {
      return this.canReject || this.canAccept;
    },
    objectId() {
      return this.realization?.objectId;
    },
    objectType() {
      return this.realization?.objectType;
    },
    getLink() {
      return this.actionValueExtension?.getLink;
    },
    actionValueExtensions() {
      return this.$root.actionValueExtensions;
    },
    actionValueExtension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.eventName)) || null;
      }
      return null;
    },
    isAutomaticTypeLabel() {
      return this.isAutomaticType ? this.$t('gamification.label.automatic') : this.$t('realization.label.manual');
    },
    statusLabel() {
      return this.$t(`realization.label.${this.status.toLowerCase()}`);
    },
    realizationActionLabel() {
      return this.realization?.actionLabel;
    },
    actionLabelChanged() {
      return this.realization?.actionLabelChanged;
    },
    programLabel() {
      return this.realization?.programLabel;
    },
    programLabelChanged() {
      return this.realization?.programLabelChanged;
    },
  },
  created() {
    // Workaround to fix closing menu when clicking outside
    $(document).mousedown(() => {
      if (this.menu) {
        window.setTimeout(() => {
          this.menu = false;
        }, 200);
      }
    });
    Promise.resolve(this.retrieveRealizationLink())
      .finally(() => {
        if (!this.realizationLink) {
          this.realizationLink = this.realization?.link || this.realization?.url;
        }
      });
  },
  methods: {
    updateRealizationStatus(status) {
      this.$realizationService.updateRealizationStatus(this.realization.id, status)
        .then(() => {
          const updatedRealization = Object.assign(this.realization, {
            status,
          });
          this.$emit('updated', updatedRealization);
        });
    },
    filterByProgram() {
      if (this.isFilteredByProgram) {
        this.$root.$emit('realization-filter-program-remove', this.programId);
      } else {
        this.$root.$emit('realization-filter-program-add', this.program);
      }
    },
    filterByAction() {
      if (this.isFilteredByAction) {
        this.$root.$emit('realization-filter-action-remove', this.ruleId);
      } else {
        this.$root.$emit('realization-filter-action-add', this.rule);
      }
    },
    filterByEarner() {
      if (this.isFilteredByEarner) {
        this.$root.$emit('realization-filter-earner-remove', this.earnerId);
      } else {
        this.$root.$emit('realization-filter-earner-add', this.earner);
      }
    },
    retrieveRealizationLink() {
      if (this.status === 'DELETED' || this.status === 'CANCELED') {
        this.$set(this.realization, 'link', null);
      } else if (!this.isAutomaticType) {
        this.$set(this.realization, 'link', `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.realization?.objectId}`);
      } else if (this.objectId?.startsWith?.('http')) {
        this.$set(this.realization, 'link', this.objectId);
      } else if (this.getLink) {
        const linkPromise = this.getLink(this.realization);
        if (linkPromise?.then) {
          return linkPromise;
        }
      }
    },
  }
};
</script>