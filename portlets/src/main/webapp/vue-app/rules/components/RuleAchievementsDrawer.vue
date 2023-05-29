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
  <exo-drawer
    id="AchievementsDrawer"
    ref="drawer"
    v-model="drawer"
    :go-back-button="backIcon"
    allow-expand
    right>
    <template #title>
      {{ $t('engagementCenter.label.achievementsDrawerTitle') }}
    </template>
    <template v-if="drawer" #content>
      <engagement-center-rule-header
        :rule="rule"
        :action-value-extensions="actionValueExtensions"
        class="pa-4"
        reduced />
      <v-list-item>
        <v-list-item-title class="text-color font-weight-bold">
          {{ $t('rules.latestAchievements') }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-for="realization in realizations"
        :key="realization.user">
        <v-list-item-content class="d-inline">
          <exo-user-avatar
            :profile-id="realization.user"
            :size="44"
            bold-title
            link-style
            popover>
            <template slot="subTitle">
              <a :href="getLinkActivity(realization.activityId)">
                <relative-date-format
                  class="text-capitalize-first-letter text-light-color text-truncate"
                  :value="realization.createDate" />
              </a>
            </template>
          </exo-user-avatar>
        </v-list-item-content>
        <v-list-item-action>
          <v-tooltip :disabled="$root.isMobile" bottom>
            <template #activator="{ on }">
              <div v-on="on">
                <v-btn
                  :href="`${basePath}/activity?id=${realization.activityId}`"
                  :disabled="!realization.activityId"
                  icon>
                  <v-icon
                    size="16"
                    class="icon-default-color">
                    fas fa-eye
                  </v-icon>
                </v-btn>
              </div>
            </template>
            <span>{{ realization.activityId && $t('program.winner.label.checkActivity') || noActivityLabel }}</span>
          </v-tooltip>
        </v-list-item-action>
      </v-list-item>
    </template>
    <template v-if="hasMore" slot="footer">
      <v-btn
        :loading="loading"
        class="btn ma-auto"
        text
        block
        @click="loadMore">
        {{ $t('rules.loadMore') }}
      </v-btn>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  props: {
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
    },
  },
  data() {
    return {
      drawer: false,
      backIcon: false,
      rule: null,
      showLoadMoreButton: false,
      realizationsCount: 0,
      pageSize: 10,
      limit: 10,
      loading: true,
      realizations: [],
      earnerType: 'USER',
      status: 'ACCEPTED',
      basePath: `${eXo.env.portal.context}/${eXo.env.portal.portalName}`,
    };
  },
  computed: {
    noActivityLabel() {
      return this.$t(`program.winner.label.${this.automaticRule ? 'noActivity' : 'activityDeleted'}`);
    },
    automaticRule() {
      return this.rule?.type === 'AUTOMATIC';
    },
    ruleId() {
      return this.rule?.id;
    },
    hasMore() {
      return this.realizationsCount > this.limit;
    },
  },
  watch: {
    loading() {
      if (this.loading) {
        this.$refs.drawer.startLoading();
      } else {
        this.$refs.drawer.endLoading();
      }
    },
  },
  created() {
    this.$root.$on('open-achievements-drawer', this.open);
  },
  methods: {
    getLinkActivity(id) {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${id}`;
    },
    open(rule, backIcon) {
      this.backIcon = backIcon;
      this.limit = this.pageSize;
      this.rule = rule;
      this.realizationsCount = rule?.realizationsCount;
      this.realizations = rule?.realizations || [];
      this.retrieveRealizations();
      this.$refs.drawer.open();
    },
    loadMore() {
      this.limit += this.pageSize;
      this.retrieveRealizations();
    },
    retrieveRealizations() {
      this.loading = true;
      this.$realizationService.getRealizations({
        earnerType: this.earnerType,
        status: this.status,
        ruleIds: [this.ruleId],
        sortBy: 'date',
        sortDescending: true,
        offset: this.offset,
        limit: this.limit,
        returnSize: true,
      })
        .then(data => {
          this.realizationsCount = data.size || 0;
          if (data.realizations?.length) {
            this.realizations = data.realizations
              .map(realization => {
                const user = realization?.earner?.remoteId;
                if (user) {
                  const activityId = realization.activityId || (realization.objectType === 'activity' && realization.objectId);
                  return {
                    id: realization.id,
                    user,
                    activityId,
                    createDate: realization.createdDate,
                  };
                }
              }).filter(a => !!a);
          } else {
            this.realizations = [];
          }
        })
        .finally(() => this.loading = false);
    },
  }
};
</script>