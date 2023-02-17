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
          <rule-action-value
            v-if="actionValueExtension"
            :action-label="actionLabel"
            :action-u-r-l="actionURL"
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
        <challenge-action-value
          v-else
          :action-label="actionLabel"
          :action-u-r-l="actionURL"
          class="width-fit-content" />
        <v-tooltip
          v-if="ruleTitleChanged"
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
              @click="openProgramDetail"
              class="width-fit-content">
              <div class="text-truncate">{{ programTitle }}
              </div>
            </a>
          </template>
          <span v-html="programTitle"></span>
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
        <span>{{ isAcceptedLabel }}</span>
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
              v-if="canEdit"
              dense
              @click="editRealization">
              <v-icon size="13" class="dark-grey-color">fas fa-edit</v-icon>
              <v-list-item-title class="text-justify ps-3">
                {{ $t('realization.label.edit') }}
              </v-list-item-title>
            </v-list-item>
            <v-list-item
              v-if="canAccept"
              dense
              @click="updateRealizations('ACCEPTED')">
              <v-icon size="13" class="dark-grey-color">fas fa-check</v-icon>
              <v-list-item-title class="text-justify ps-3">
                {{ $t('realization.label.accept') }}
              </v-list-item-title>
            </v-list-item>
            <v-list-item
              v-if="canReject"
              dense
              @click="updateRealizations('REJECTED')">
              <v-icon size="13" class="dark-grey-color">fas fa-ban</v-icon>
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
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
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
    actionURL() {
      return this.realization?.url;
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
      return this.realization?.domain;
    },
    programTitle() {
      return this.program?.title || '-';
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
      return this.status === 'ACCEPTED' || this.status === 'EDITED';
    },
    canAccept() {
      return this.status === 'REJECTED';
    },
    isAccepted() {
      return this.status === 'ACCEPTED';
    },
    canEdit() {
      return this.realization.action && this.realization.action.type === 'MANUAL';
    },
    hasActions() {
      return this.canReject || this.canAccept || this.canEdit;
    },
    actionIcon() {
      return this.actionValueExtension?.icon;
    },
    extendedActionValueComponent() {
      return this.actionValueExtension && {
        componentName: 'action-value',
        componentOptions: {
          vueComponent: this.actionValueExtension.vueComponent,
        },
      } || null;
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
    isAcceptedLabel() {
      return this.isAccepted ? this.$t('realization.label.accepted') : this.$t('realization.label.rejected');
    },
    realizationActionLabel() {
      return this.realization?.actionLabel;
    },
    ruleTitleChanged() {
      if (this.isAutomaticType) {
        return this.realizationActionLabel !== this.eventName && this.realizationActionLabel !== this.actionLabel;
      }
      return this.realizationActionLabel !== this.actionLabel;
    },
    programLabel() {
      return this.realization?.domainLabel;
    },
    programLabelChanged() {
      return this.programLabel !== this.programTitle;
    }
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
  },
  methods: {
    updateRealizations(status) {
      this.$realizationsServices.updateRealization(this.realization.id, status)
        .then((updatedRealization) => this.$emit('updated', updatedRealization));
    },
    editRealization() {
      this.$root.$emit('realization-open-edit-drawer', this.realization, this.actionLabel);
    },
    openProgramDetail() {
      this.$programsServices.getProgramById(this.program.id)
        .then(program => {
          if (program && program.id) {
            this.$root.$emit('open-program-detail', program);
            window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs/${this.program.id}`);
          }
        });
    },
  }
};
</script>