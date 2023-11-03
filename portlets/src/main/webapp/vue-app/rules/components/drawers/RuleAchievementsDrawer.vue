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
        class="pa-4"
        reduced />
      <v-list-item>
        <v-list-item-title class="text-color font-weight-bold">
          {{ $t('rules.latestAchievements') }}
        </v-list-item-title>
      </v-list-item>
      <engagement-center-rule-achievement-item
        v-for="realization in realizations"
        :key="realization.id"
        :realization="realization"
        :rule="rule" />
    </template>
    <template v-if="hasMore" #footer>
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
      identityType: 'USER',
      status: 'ACCEPTED',
    };
  },
  computed: {
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
    document.addEventListener('open-achievements-drawer', this.openDrawer);
  },
  beforeDestroy() {
    document.removeEventListener('open-achievements-drawer', this.openDrawer);
  },
  methods: {
    getLinkActivity(id) {
      return `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${id}`;
    },
    openDrawer(event) {
      if (event?.detail?.rule) {
        this.open(event.detail.rule, event.detail.backIcon);
      }
    },
    open(rule, backIcon) {
      this.backIcon = backIcon;
      this.limit = this.pageSize;
      this.rule = rule;
      this.realizationsCount = rule?.realizationsCount;
      this.realizations = [];
      this.retrieveRealizations();
      this.$nextTick().then(() => {
        if (this.$refs.drawer) {
          this.$refs.drawer.open();
        }
      });
    },
    loadMore() {
      this.limit += this.pageSize;
      this.retrieveRealizations();
    },
    retrieveRealizations() {
      this.loading = true;
      this.$realizationService.getRealizations({
        identityType: this.identityType,
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
            data.realizations
              .filter(realization => !this.realizations.find(r => r.id === realization.id) && realization?.earner?.remoteId)
              .forEach(realization => {// Avoid blink effect when loading more, thus add one by one
                const activityId = realization.activityId || (realization.objectType === 'activity' && realization.objectId);
                this.realizations.push({
                  id: realization.id,
                  user: realization?.earner?.remoteId,
                  activityId,
                  createDate: realization.createdDate,
                });
                this.realizations = this.realizations.slice();
              });
          } else {
            this.realizations = [];
          }
        })
        .finally(() => this.loading = false);
    },
  }
};
</script>