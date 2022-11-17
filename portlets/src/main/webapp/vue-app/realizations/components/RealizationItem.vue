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
    <td class="wrap">
      <date-format
        :format="dateFormat"
        :value="realization.createdDate" />
    </td>
    <td v-if="isAdministrator" class="text-truncate align-center">
      <exo-user-avatar
        :profile-id="earner"
        :size="28"
        extra-class="d-inline-block"
        link-style
        popover
        avatar />
    </td>
    <td class="text-truncate align-center">
      <v-icon
        class="primary--text"
        size="16">
        {{ actionTypeIcon }}
      </v-icon>
    </td>
    <td>
      <v-tooltip bottom>
        <template #activator="{ on }">
          <a v-on="on" @click="openProgramDetail">
            <div class="text-truncate">{{ programTitle }}
            </div>
          </a>
        </template>
        <span v-html="programTitle"></span>
      </v-tooltip>
    </td>
    <td>
      <div v-if="isAutomaticType">
        <extension-registry-component
          v-if="actionValueExtension"
          :component="extendedActionValueComponent"
          :params="actionValueComponentParams" />
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
        :action-u-r-l="actionURL" />
    </td>
    <td class="text-truncate align-center">
      {{ score }}
    </td>
    <td class="text-truncate align-center">
      <v-icon
        :class="statusIconClass"
        class="me-1"
        size="16">
        {{ statusIcon }}
      </v-icon>
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
  }),
  computed: {
    earner() {
      return this.realization.earner || '';
    },
    actionURL() {
      return this.realization?.url;
    },
    isAutomaticType() {
      return this.realization?.action?.type === 'AUTOMATIC';
    },
    actionLabel() {
      if (this.isAutomaticType) {
        const key = `exoplatform.gamification.gamificationinformation.rule.description.${this.realization.actionLabel}`;
        if (this.$te(key)) {
          return this.$t(key);
        }
      }
      return this.realization.actionLabel;
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
    canEdit() {
      return this.realization.action && this.realization.action.type === 'MANUAL';
    },
    hasActions() {
      return this.canReject || this.canAccept || this.canEdit;
    },
    extendedActionValueComponent() {
      return this.actionValueExtension && {
        componentName: 'action-value',
        componentOptions: {
          vueComponent: this.actionValueExtension.vueComponent,
        },
      } || null;
    },
    actionValueComponentParams() {
      return this.actionValueExtension && {
        actionURL: this.actionURL,
        actionLabel: this.actionLabel
      } || null;
    },
    actionValueExtension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.realization.actionLabel)) || null;
      }
      return null;
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
      this.$root.$emit('open-program-detail', this.program);
      window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs/${this.program.id}`);
    },
  }
};
</script>