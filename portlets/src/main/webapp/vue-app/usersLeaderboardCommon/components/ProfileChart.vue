<!--

  This file is part of the Meeds project (https://meeds.io/).
  
  Copyright (C) 2023 Meeds Association contact@meeds.io
  
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
    :min-height="size"
    class="flex full-width flex-grow-1"
    flat>
    <div class="d-flex flex-row overflow-hidden subtitle-2 text-color">
      <v-card
        :max-height="size"
        class="col-5 ps-0 pe-1 my-auto overflow-y-auto overflow-x-hidden"
        flat>
        <v-tooltip
          v-for="(label, index) in programLabels"
          :key="index"
          bottom>
          <template #activator="{ on }">
            <span
              class="text-truncate-2 caption font-weight-light my-1"
              v-on="on">
              <div class="d-inline-block">
                <v-card
                  :color="colors[index % colors.length]"
                  min-width="30px"
                  width="30px"
                  height="17px"
                  class="me-1 mb-n1"
                  flat />
              </div>
              {{ label || $t('gamification.hiddenProgram') }}
            </span>
          </template>
          <span>{{ label || $t('gamification.hiddenProgramTooltip') }}</span>
        </v-tooltip>
      </v-card>
      <v-flex
        :id="id"
        class="usersLeaderboardChartParent d-flex justify-end" />
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
    period: {
      type: String,
      default: () => 'WEEK',
    },
    chartType: {
      type: String,
      default: () => 'pie',
    },
    size: {
      type: Number,
      default: () => 250,
    },
    programs: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data: () => ({
    id: `Chart${parseInt(Math.random() * 100000)}`,
    loading: false,
    chartData: null,
    programLabels: [],
    chart: null,
    echarts: null,
    colors: [
      '#4ad66d', '#ffe169', '#ff8fa3', '#20a8ea', '#C155F4', '#F7A35B', '#A0C7FF', '#FD6A6A', '#059d98', '#b7efc5',
      '#dbb42c', '#c9184a', '#1273d4', '#E65ABC', '#00FF56', '#B1F6FF', '#FFFF46', '#26a855', '#f10000', '#208b3a',
      '#c9a227', '#ffccd5', '#134d9b', '#E66CDC', '#58D68B', '#5CE6D3', '#f16a27', '#ac1c1e', '#eda3ff', '#1a7431',
      '#a47e1b', '#ff4d6d', '#62b0de', '#FF97D0', '#92e03a', '#f44336', '#3d6d8a', '#E0A5FF', '#FF9DB8', '#808080'
    ],
  }),
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
        this.$leaderboardService.getStats(this.identityId, this.period)
          .then(stats => {
            let id = 0;
            this.chartData = stats.map(s => {
              const program = this.programs.find(p => p.id === s.programId);
              const label = program?.title || this.$t('gamification.hiddenProgram');
              this.programLabels.push(program?.title);
              return {
                name: id++,
                label,
                value: s.value,
              };
            });
            this.$nextTick().then(() => resolve(this.chartData));
          })
          .catch(e => reject(e));
      });
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
            z: 2000,
            confine: true,
            formatter(param) {
              return `${param.data.label} (${param.percent}%)`;
            },
          },
          color: this.colors,
          series: [{
            type: 'pie',
            radius: ['50%', '70%'],
            label: {show: false},
            data: this.chartData,
          }]
        }, true);
      });
    },
  }
};
</script>