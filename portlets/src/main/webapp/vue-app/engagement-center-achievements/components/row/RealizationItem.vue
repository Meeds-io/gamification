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
              target="_blank"
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
      <td class="d-flex text-truncate justify-center">
        <v-tooltip bottom>
          <template #activator="{ on }">
            <component
              v-on="on"
              v-bind="programUrl && {
                href: programUrl
              }"
              :is="programUrl && 'a' || 'div'"
              class="text-truncate my-auto">
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
      </td>
      <td class="wrap text-truncate align-center">
        <v-tooltip bottom>
          <template #activator="{ on, attrs }">
            <div
              v-bind="attrs"
              v-on="on">
              <date-format
                :format="dateFormat"
                :value="createdDate" />
            </div>
          </template>
          <span>           
            <date-format
              :format="tooltipDateFormat"
              :value="createdDate" />
          </span>
        </v-tooltip>
      </td>
      <td v-if="isAdministrator">
        <div class="d-flex">
          <user-avatar
            :name="earnerFullName"
            :avatar-url="earnerAvatarUrl"
            :profile-id="earnerRemoteId"
            :popover="earnerRemoteId"
            :size="28"
            extra-class="d-inline-block"
            link-style />
          <v-card
            v-if="hover && earnerId"
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
                  @click="filterByEarner">
                  <v-icon size="14">{{ isFilteredByEarner && 'fa-search-minus' || 'fa-search-plus' }}</v-icon>
                </v-btn>
              </template>
              <span>{{ isFilteredByEarner && $t('realization.label.removeThisEarnerToFilter') || $t('realization.label.addThisEarnerToFilter') }}</span>
            </v-tooltip>
          </v-card>
        </div>
      </td>
      <td class="text-truncate align-center">
        {{ score }}
      </td>
      <td v-if="isAdministrator" :class="loading ? 'align-center' : ''">
        <v-progress-circular
          v-if="loading"
          indeterminate
          color="primary"
          size="20"
          class="ms-3 my-auto" />
        <div v-else-if="reviewer" class="d-flex">
          <user-avatar
            :name="reviewerFullName"
            :avatar-url="reviewerAvatarUrl"
            :profile-id="reviewerRemoteId"
            :popover="reviewerRemoteId"
            :size="28"
            extra-class="d-inline-block"
            link-style />
          <v-card
            v-if="hover && reviewerId"
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
                  @click="filterByReviewer">
                  <v-icon size="14">{{ isFilteredByReviewer && 'fa-search-minus' || 'fa-search-plus' }}</v-icon>
                </v-btn>
              </template>
              <span>{{ isFilteredByReviewer && $t('realization.label.removeThisReviewerToFilter') || $t('realization.label.addThisReviewerToFilter') }}</span>
            </v-tooltip>
          </v-card>
        </div>
        <div v-else class="text-color">{{ $t('realization.label.notReviewed') }}</div>
      </td>
      <td class="text-truncate align-center">
        <v-tooltip v-if="canceled" bottom>
          <template #activator="{ on, attrs }">
            <span
              class="not-clickable"
              v-bind="attrs"
              v-on="on"> - </span>
          </template>
          <span>{{ statusLabel }}</span>
        </v-tooltip>
        <div v-else-if="isAdministrator" class="text-center">
          <v-btn
            :aria-label="accepted ? statusLabel : acceptLabel"
            :class="accepted && 'success-color-background not-clickable' || 'light-black-background'"
            class="mx-2"
            height="16px"
            width="16px"
            fab
            dark
            depressed
            v-on="canUpdateStatus ? { click: () => updateRealizationStatus('ACCEPTED') } : {}">
            <v-tooltip
              z-index="4"
              max-width="300"
              bottom>
              <template #activator="{ on, attrs }">
                <v-icon
                  v-bind="attrs"
                  v-on="on"
                  size="10"
                  dark>
                  fas fa-check
                </v-icon>
              </template>
              <span>{{ accepted ? statusLabel : acceptLabel }}</span>
            </v-tooltip>
          </v-btn>
          <v-btn
            :aria-label="pending ? statusLabel : reviewLabel"
            :class="pending ? 'orange darken-2 not-clickable' : 'light-black-background'"
            class="mx-2"
            height="16px"
            width="16px"
            fab
            dark
            depressed
            v-on="canUpdateStatus ? { click: () => updateRealizationStatus('PENDING') } : {}">
            <v-tooltip
              z-index="4"
              max-width="300"
              bottom>
              <template #activator="{on, attrs }">
                <v-icon
                  v-bind="attrs"
                  v-on="on"
                  size="10"
                  dark>
                  fas fa-question
                </v-icon>
              </template>
              <span>{{ pending ? statusLabel : reviewLabel }}</span>
            </v-tooltip>
          </v-btn>
          <v-btn
            :aria-label="rejected ? statusLabel : rejectLabel"
            :class="rejected && 'error-color-background not-clickable' || 'light-black-background'"
            class="mx-2"
            height="16px"
            width="16px"
            fab
            dark
            depressed
            v-on="canUpdateStatus ? { click: () => updateRealizationStatus('REJECTED') } : {}">
            <v-tooltip
              z-index="4"
              max-width="300"
              bottom>
              <template #activator="{ on, attrs }">
                <v-icon
                  v-bind="attrs"
                  v-on="on"
                  size="10"
                  dark>
                  fas fa-times
                </v-icon>
              </template>
              <span>{{ rejected ? statusLabel : rejectLabel }}</span>
            </v-tooltip>
          </v-btn>
        </div>
        <v-tooltip v-else bottom>
          <template #activator="{ on, attrs }">
            <v-btn
              class="mx-2 not-clickable"
              height="16px"
              width="16px"
              fab
              dark
              depressed
              :class="statusIconClass"
              v-bind="attrs"
              v-on="on">
              <v-icon size="10" dark>{{ statusIcon }}</v-icon>
            </v-btn>
          </template>
          <span>{{ statusLabel }}</span>
        </v-tooltip>
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
    filteredReviewerIds: {
      type: Array,
      default: null,
    },
    extensions: {
      type: Array,
      default: null,
    }
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
    statusValue: null,
    loading: false,
    canUpdateStatus: false,
  }),
  computed: {
    rejectLabel() {
      return this.canUpdateStatus ? this.$t('realization.label.reject') : this.cannotUpdateStatusLabel;
    },
    acceptLabel() {
      return this.canUpdateStatus ? this.$t('realization.label.accept') : this.cannotUpdateStatusLabel;
    },
    reviewLabel() {
      return this.canUpdateStatus ? this.$t('realization.label.review') : this.cannotUpdateStatusLabel;
    },
    createdDate() {
      return this.realization?.sendingDate || this.realization?.createdDate;
    },
    earner() {
      return this.realization?.earner;
    },
    earnerFullName() {
      return this.earner?.fullName;
    },
    earnerAvatarUrl() {
      return this.earner?.avatarUrl;
    },
    earnerRemoteId() {
      return this.earner?.remoteId;
    },
    earnerId() {
      return this.earner?.id;
    },
    reviewer() {
      return this.realization?.reviewer;
    },
    reviewerFullName() {
      return this.reviewer?.fullName;
    },
    reviewerAvatarUrl() {
      return this.reviewer?.avatarUrl;
    },
    reviewerRemoteId() {
      return this.reviewer?.remoteId;
    },
    reviewerId() {
      return this.reviewer?.id;
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
    isFilteredByReviewer() {
      return this.reviewerId && this.filteredReviewerIds && this.filteredReviewerIds.find(id => id === this.reviewerId);
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
    accepted() {
      return this.status === 'ACCEPTED';
    },
    pending() {
      return this.status === 'PENDING';
    },
    rejected() {
      return this.status === 'REJECTED' || this.status === 'CANCELED';
    },
    canceled() {
      return this.status === 'CANCELED' || this.status === 'DELETED';
    },
    statusIcon() {
      switch (this.status) {
      case 'ACCEPTED':
        return 'fas fa-check';
      case 'PENDING':
        return 'fas fa-question';
      default:
        return 'fas fa-times';
      }
    },
    statusIconClass() {
      switch (this.status) {
      case 'ACCEPTED':
        return 'success-color-background';
      case 'PENDING':
        return 'orange darken-2';
      default:
        return 'error-color-background';
      }
    },
    actionTypeIcon() {
      return this.isAutomaticType ? 'fas fa-cogs' : 'fas fa-hand-pointer';
    },
    actionLabelClass() {
      return !this.realization.url && 'defaultCursor' || '';
    },
    canAccept() {
      return this.status === 'REJECTED';
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
    updateStatusExtension() {
      return this.extensions.find(extension => extension?.canUpdateStatus);
    },
    createdDateInSecond() {
      const dateObject = new Date(this.realization?.createdDate);
      return Math.floor(dateObject.getTime() / 1000);
    },
    canUpdateStatusCall() {
      return this.updateStatusExtension?.canUpdateStatus;
    },
    cannotUpdateStatusLabel() {
      return this.$t(`${this.updateStatusExtension?.cannotUpdateStatusLabel}`);
    },
  },
  watch: {
    pending() {
      this.computeCanUpdateStatus();
    },
    createdDateInSecond() {
      this.computeCanUpdateStatus();
    },
    canUpdateStatusCall() {
      this.computeCanUpdateStatus();
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
    this.statusValue = this.status === 'DELETED' || this.status === 'CANCELED' ? 'REJECTED' : this.status;
  },
  methods: {
    updateRealizationStatus(status) {
      if (status === this.statusValue) {
        return;
      }
      this.loading = true;
      return this.$realizationService.updateRealizationStatus(this.realization.id, status)
        .then(() => {
          this.statusValue = status;
          const reviewer = {remoteId: eXo.env.portal.userName};
          const updatedRealization = Object.assign(this.realization, {
            status,
            sendingDate: this.pending ? this.realization?.createdDate: null,
            createdDate: this.pending ? new Date().toISOString() : this.realization?.createdDate,
            reviewer
          });
          this.$emit('updated', updatedRealization);
        }).finally(() => this.loading = false);
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
    filterByReviewer() {
      if (this.isFilteredByReviewer) {
        this.$root.$emit('realization-filter-reviewer-remove', this.reviewerId);
      } else {
        this.$root.$emit('realization-filter-reviewer-add', this.reviewer);
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
    computeCanUpdateStatus() {
      this.updateStatusExtension?.canUpdateStatus(this.createdDateInSecond);
      if (this.pending) {
        this.canUpdateStatus = true;
      } else if (this.canUpdateStatusCall) {
        const result = this.canUpdateStatusCall(this.createdDateInSecond);
        if ('then' in result) {
          result.then(b => this.canUpdateStatus = b);
        } else {
          this.canUpdateStatus = !!result;
        }
      } else {
        this.canUpdateStatus = false;
      }
    },
  }
};
</script>