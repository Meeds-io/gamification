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
  <tr :id="`GamificationRealizationItem${realization.id}`">
    <td>
      <div class="d-flex">
        <div v-if="isAutomaticType" class="width-fit-content">
          <engagement-center-realizations-automatic-action-value
            v-if="actionValueExtension"
            :action-label="actionLabel"
            :action-u-r-l="realizationLink"
            :action-icon="actionIcon" />
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
          class="width-fit-content" />
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
      </div>
    </td>
    <td>
      <div class="d-flex">
        <v-tooltip bottom>
          <template #activator="{ on }">
            <a
              v-on="on"
              :href="programUrl"
              class="width-fit-content">
              <div class="text-truncate">{{ programTitle }}
              </div>
            </a>
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
    <td v-if="isAdministrator" class="text-truncate align-center">
      <exo-user-avatar
        :identity="earner"
        :size="28"
        extra-class="d-inline-block"
        link-style
        popover
        avatar />
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
  },
  data: () => ({
    menu: false,
    tooltipDateFormat: {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    },
  }),
  computed: {
    earner() {
      return this.realization?.earner?.profile;
    },
    realizationLink() {
      return this.realization?.link;
    },
    isAutomaticType() {
      return this.realization?.action?.type === 'AUTOMATIC';
    },
    actionLabel() {
      if (this.isAutomaticType) {
        const key = `exoplatform.gamification.gamificationinformation.rule.title.${this.realization.action.title}`;
        if (this.$te(key)) {
          return this.$t(key);
        }
      }
      return this.realization.action.title;
    },
    eventName() {
      return this.realization?.action?.event;
    },
    program() {
      return this.realization?.program;
    },
    programTitle() {
      return this.program?.title || '-';
    },
    programUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs/${this.program?.id}`;
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
    actionIcon() {
      return this.actionValueExtension?.icon;
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
    extendedActionValueComponent() {
      return this.actionValueExtension && {
        componentName: 'action-value',
        componentOptions: {
          vueComponent: this.actionValueExtension.vueComponent,
        },
      } || null;
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
    this.retrieveRealizationLink();
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
    retrieveRealizationLink() {
      if (this.status === 'DELETED' || this.status === 'CANCELED') {
        this.$set(this.realization, 'link', null);
      } else if (!this.isAutomaticType) {
        this.$set(this.realization, 'link', `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${this.realization?.objectId}`);
      } else if (!this.objectType && this.objectId) {
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