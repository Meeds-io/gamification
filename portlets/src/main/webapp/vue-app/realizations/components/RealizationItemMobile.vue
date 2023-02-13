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
  <v-list-item class="d-block">
    <v-list-content>
      <v-row>
        <v-col class="col-sm-4 col-4">
          <span class="font-weight-bold"> {{ headers[2].text }}</span>
        </v-col>
        <v-col class="col-sm-8 col-8">
          <date-format
            :format="dateFormat"
            :value="realization.createdDate" />
        </v-col>
      </v-row>
      <v-row v-if="isAdministrator" class="text-truncate align-center">
        <v-col class="col-sm-4 col-4">
          <span class="font-weight-bold d-flex"> {{ headers[3].text }}</span>
        </v-col>
        <v-col class="col-sm-8 col-8">
          <exo-user-avatar
            :identity="earner"
            :size="28"
            link-style
            popover />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col-sm-4 col-4">
          <span class="font-weight-bold"> {{ headers[1].text }}</span>
        </v-col>
        <v-col class="col-sm-8 col-8">
          <v-tooltip bottom>
            <template #activator="{ on }">
              <a v-on="on" @click="openProgramDetail">
                <div class="text-truncate">{{ programTitle }}
                </div>
              </a>
            </template>
            <span v-html="programTitle"></span>
          </v-tooltip>
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col-sm-4 col-4">
          <span class="font-weight-bold"> {{ headers[0].text }}</span>
        </v-col>
        <v-col class="col-sm-8 col-8">
          <div v-if="isAutomaticType">
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
            :action-u-r-l="actionURL" />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col-sm-4 col-4">
          <span class="font-weight-bold"> {{ isAdministrator ? headers[6].text : headers[5].text }}</span>
        </v-col>
        <v-col class="col-sm-8 col-8">
          <v-icon
            :class="statusIconClass"
            class="me-1"
            size="16">
            {{ statusIcon }}
          </v-icon>
        </v-col>
      </v-row>
      <v-divider class="my-2" />
    </v-list-content>
  </v-list-item>
</template>
<script>
export default {
  props: {
    headers: {
      type: Object,
      default: null,
    },
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
    eventName() {
      return this.realization?.action?.event;
    },
    actionValueExtension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.eventName)) || null;
      }
      return null;
    },
    actionIcon() {
      return this.actionValueExtension?.icon;
    },
  },
  methods: {
    openProgramDetail() {
      this.$root.$emit('open-program-detail', this.program);
      window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs/${this.program.id}`);
    },
  }
};
</script>