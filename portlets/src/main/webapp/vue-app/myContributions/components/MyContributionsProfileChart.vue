<!--

  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
  <v-card
    :loading="loading"
    class="d-flex flex-column full-width flex-grow-1"
    flat>
    <div class="position-absolute mt-n2">{{ title }}</div>
    <v-card
      :min-height="size - 50"
      :width="size - 35"
      class="position-relative flex-grow-1 flex-shrink-0 d-flex align-center align-self-center justify-center"
      flat>
      <v-card
        v-if="centralPoints"
        class="absolute-horizontal-center d-flex flex-column align-center justify-center transparent z-index-two"
        flat
        @click="$emit('open')">
        <h4 class="text-color my-0">{{ $t('overview.myContributions.Total') }}</h4>
        <h4 class="text-header primary--text my-0">
          {{ totalScore }}
        </h4>
      </v-card>
      <div
        :id="id"
        :min-width="size"
        :max-width="size"
        class="usersLeaderboardChartParent absolute-horizontal-center align-center justify-center"
        flat></div>
    </v-card>
    <div class="ps-0 pe-1 my-auto">
      <div
        v-for="(item, index) in programsToDisplay"
        :key="index"
        :id="`${id}-${item.id}`"
        class="text-truncate-2 my-1"
        flat>
        <div class="d-inline-block">
          <v-card
            :color="colors[index % colors.length]"
            min-width="30px"
            width="30px"
            height="17px"
            class="me-1 mb-n1 position-static"
            flat />
        </div>
        {{ item.label || $t('gamification.hiddenProgram') }}
      </div>
    </div>
  </v-card>
</template>
<script>
export default {
  props: {
    identityId: {
      type: String,
      default: null,
    },
    score: {
      type: String,
      default: null,
    },
    period: {
      type: String,
      default: () => 'week',
    },
    chartType: {
      type: String,
      default: () => 'pie',
    },
    size: {
      type: Number,
      default: () => 250,
    },
    centralPoints: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    id: `Chart${parseInt(Math.random() * 100000)}`,
    loading: false,
    chartData: null,
    programs: [],
    hoverProgramPercent: null,
    chart: null,
    echarts: null,
    colors: [
      '#4ad66d', '#ffe169', '#ff8fa3', '#20a8ea', '#C155F4', '#F7A35B', '#A0C7FF', '#FD6A6A', '#059d98', '#b7efc5',
      '#dbb42c', '#c9184a', '#1273d4', '#E65ABC', '#00FF56', '#B1F6FF', '#FFFF46', '#26a855', '#f10000', '#208b3a',
      '#c9a227', '#ffccd5', '#134d9b', '#E66CDC', '#58D68B', '#5CE6D3', '#f16a27', '#ac1c1e', '#eda3ff', '#1a7431',
      '#a47e1b', '#ff4d6d', '#62b0de', '#FF97D0', '#92e03a', '#f44336', '#3d6d8a', '#E0A5FF', '#FF9DB8', '#808080'
    ],
  }),
  computed: {
    title() {
      return this.$t(`gamification.myContributions.settings.${this.period.toLowerCase()}Period`);
    },
    programsToDisplay() {
      return this.limit
        && this.programs?.slice?.(0, this.limit)
        || [];
    },
    limit() {
      return this.$root.myContributionsDisplayLegend && this.$root.myContributionsProgramLimit || 0;
    },
    totalScore() {
      return this.score && this.format(this.score);
    },
  },
  mounted() {
    this.refresh();
  },
  methods: {
    refresh() {
      this.loading = true;
      this.loadChartData()
        .then(() => this.init())
        .finally(() => this.loading = false);
    },
    loadChartData() {
      if (this.chartData) {
        return this.$nextTick().then(() => this.chartData);
      }

      return new Promise((resolve, reject) => {
        this.programs = [];
        this.$leaderboardService.getStats(this.identityId, this.period, 0, eXo.env.portal.spaceId)
          .then(stats => {
            this.chartData = stats.map(s => {
              this.programs.push({
                id: s.programId,
                label: s.label,
                hidden: !s.label,
              });
              return {
                id: s.programId,
                name: s.label || this.$t('gamification.hiddenProgramTooltip'),
                label: s.label || this.$t('gamification.hiddenProgram'),
                value: s.value,
                hidden: !s.label,
              };
            });
            this.$nextTick().then(() => resolve(this.chartData));
          })
          .catch(e => reject(e));
      });
    },
    format(value) {
      return new Intl.NumberFormat(eXo.env.portal.language, {
        style: 'decimal',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(value);
    },
    init() {
      return new Promise((resolve, reject) => {
        if (this.echarts) {
          resolve(this.echarts);
        } else {
          try {
            window.require(['SHARED/eCharts'], echarts => {
              this.echarts = echarts;
              resolve(echarts);
            });
          } catch (e) {
            reject(e);
          }
        }
      }).then(echarts => {
        const element = document.querySelector(`#${this.id}`);
        this.chart = echarts.init(element, null, {
          width: this.size,
          height: this.size,
        });
        this.chart.setOption({
          tooltip: {
            trigger: 'item',
            appendToBody: true,
            formatter: params => `${params.name} <strong style="padding-left: 12px;">${parseInt(params.percent)}%</strong> (${this.format(params.value)})`,
            position: {
              top: 0,
              left: 0,
            },
          },
          color: this.colors,
          series: [{
            type: 'pie',
            radius: ['50%', '70%'],
            label: {
              show: false,
            },
            data: this.chartData,
          }]
        }, true);
      });
    },
  }
};
</script>