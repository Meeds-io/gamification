<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
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
  <v-card
    :loading="loading"
    class="pa-0"
    flat>
    <v-flex :id="id" class="usersLeaderboardChartParent" />
  </v-card>
</template>
<script>
export default {
  props: {
    username: {
      type: String,
      default: function() {
        return null;
      },
    },
    chartType: {
      type: String,
      default: function() {
        return 'pie';
      },
    },
    domains: {
      type: Array,
      default: function() {
        return [];
      },
    },
    open: {
      type: Boolean,
      default: false,
    },
  },
  data () {
    return {
      id: `Chart${parseInt(Math.random() * 100000)}`,
      loading: false,
      chartData: null,
      chart: null,
      echarts: null,
      colors: [
        '#4ad66d', '#ffe169', '#ff8fa3', '#20a8ea', '#C155F4', '#F7A35B', '#A0C7FF', '#FD6A6A', '#059d98', '#b7efc5',
        '#dbb42c', '#c9184a', '#1273d4', '#E65ABC', '#00FF56', '#B1F6FF', '#FFFF46', '#26a855', '#f10000', '#208b3a',
        '#c9a227', '#ffccd5', '#134d9b', '#E66CDC', '#58D68B', '#5CE6D3', '#f16a27', '#ac1c1e', '#eda3ff', '#1a7431',
        '#a47e1b', '#ff4d6d', '#62b0de', '#FF97D0', '#92e03a', '#f44336', '#3d6d8a', '#E0A5FF', '#FF9DB8', '#808080'
      ],
    };
  },
  watch: {
    open() {
      if (this.open) {
        this.refresh();
      }
    },
  },
  mounted() {
    this.refresh();
  },
  methods: {
    refresh() {
      this.loadChartData()
        .then(() => this.init());
    },
    loadChartData() {
      if (this.chartData) {
        return this.$nextTick().then(() => this.chartData);
      }

      this.loading = true;
      return new Promise((resolve, reject) => {
        fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard/stats?username=${this.username}`, {
          credentials: 'include',
        })
          .then(resp => resp?.ok && resp.json())
          .then(chartData => {
            this.chartData = chartData;
            chartData.forEach(statData => {
              const domain = this.domains.find(tmpDomain => tmpDomain.title === statData.label);
              statData.name = domain?.label || this.$t('exoplatform.gamification.gamificationinformation.domain.others');
            });
            this.$nextTick().then(() => resolve(this.chartData));
          })
          .catch(e => reject(e))
          .finally(() => this.loading = false);
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
        window.setTimeout(() => {
          if (this.open) {
            this.chart = echarts.init(element, null, {
              width: 250,
              height: 250,
            });
            this.chart.setOption({
              tooltip: {
                trigger: 'item',
                formatter: '{b}: {c} ({d}%)'
              },
              color: this.colors,
              series: [{
                type: 'pie',
                radius: ['50%', '70%'],
                label: {show: false},
                data: this.chartData,
              }]
            }, true);
          }
        }, 50);
      });
    }
  }
};
</script>