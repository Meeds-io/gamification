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
  <div class="d-flex flex-column full-height full-width">
    <div class="flex d-flex flex-grow-0 flex-shrink-1">
      <div v-if="overviewDisplay && !hasZeroPoints && initialized" class="d-flex flex-grow-1 full-width">
        <div class="widget-text-header text-capitalize-first-letter text-truncate">
          {{ $t('gamification.overview.contributionsTitle') }}
        </div>
      </div>
      <v-layout
        v-if="!overviewDisplay"
        row
        wrap
        align-start
        mx-4
        mt-4>
        <v-flex
          d-flex
          xs1>
          <v-icon
            color="grey darken-2"
            size="20"
            @click="$emit('isProfileStats')">
            {{ $vuetify.rtl && 'mdi-arrow-right' || 'mdi-arrow-left' }}
          </v-icon>
        </v-flex>
        <v-flex
          d-flex
          xs10
          justify-center>
          <div>
            <span class="pe-6 text-uppercase subtitle-2 profile-card-header text-color">{{ $t('homepage.profileStatus.weeklyPoints') }}</span>
          </div>
        </v-flex>
        <v-flex
          v-if="!hasZeroPoints"
          class="text-right"
          d-flex
          xs1>
          <i
            class="uiIconInformation clickable primary--text my-auto ms-3 pb-2"
            @click="openHistoryDrawer"></i>
        </v-flex>
      </v-layout>
    </div>
    <v-list
      v-if="hasZeroPoints"
      height="180"
      class="d-flex flex-shrink-0 flex-grow-1 align-center justify-center">
      <div class="d-flex flex-column align-center justify-center">
        <v-icon color="secondary" size="54">fa-chart-pie</v-icon>
        <span class="subtitle-1 font-weight-bold mt-7">{{ $t('gamification.overview.weeklyAchievements') }}</span>
      </div>
    </v-list>
    <v-card
      v-else
      :loading="displayLoadingChart"
      class="flex full-width flex-grow-1"
      flat>
      <div class="d-flex flex-row full-height full-width subtitle-2 text-color">
        <div v-if="overviewDisplay" class="col-5 ps-0 pe-1">
          <v-tooltip
            v-for="program in programLabels"
            :key="program.name"
            bottom>
            <template #activator="{ on }">
              <h6
                class="text-truncate-2 font-weight-light my-1"
                v-on="on">
                <div class="d-inline-block">
                  <v-card
                    :color="program.color"
                    min-width="30px"
                    width="30px"
                    height="17px"
                    class="me-1 mb-n1"
                    flat />
                </div>
                {{ program.name }}
              </h6>
            </template>
            <span>{{ program.name }}</span>
          </v-tooltip>
        </div>
        <div
          v-if="show"
          id="echartUserPoints"
          :style="pieChartDimensions"
          class="flex my-auto"></div>
      </div>
    </v-card>
  </div>
</template>
<script>
import {getGamificationPointsStats, getGamificationPoints} from '../../profileStats/profilStatsAPI.js';
export default {
  props: {
    overviewDisplay: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      userPoints: 0,
      widgetWidth: 0,
      refreshedWidth: 0,
      refreshing: false,
      period: 'WEEK',
      programLabels: [],
      seriesData: [],
      loading: true,
      show: false,
      colors: [
        '#4ad66d', '#ffe169', '#ff8fa3', '#20a8ea', '#C155F4', '#F7A35B', '#A0C7FF', '#FD6A6A', '#059d98', '#b7efc5',
        '#dbb42c', '#c9184a', '#1273d4', '#E65ABC', '#00FF56', '#B1F6FF', '#FFFF46', '#26a855', '#f10000', '#208b3a',
        '#c9a227', '#ffccd5', '#134d9b', '#E66CDC', '#58D68B', '#5CE6D3', '#f16a27', '#ac1c1e', '#eda3ff', '#1a7431',
        '#a47e1b', '#ff4d6d', '#62b0de', '#FF97D0', '#92e03a', '#f44336', '#3d6d8a', '#E0A5FF', '#FF9DB8', '#808080'
      ],
    };
  },
  computed: {
    option() {
      return {
        title: [{
          text: this.$t('overview.myContributions.Total'),
          textStyle: {
            fontStyle: 'normal',
            color: '#4d5466',
            fontWeight: 'normal',
            fontSize: '16',
          },
          subtext: `${this.userPoints}`,
          subtextStyle: {
            fontStyle: 'normal',
            color: '#4d5466',
            fontWeight: 'bold',
            fontSize: '18',
            lineHeight: 22,
          },
          left: 'center',
          top: '43%',
        }],
        tooltip: { 
          trigger: 'item',
          formatter(param) {
            return `${param.data.label} (${param.percent}%)`;
          },
          position: ['center', 'center'],
        },
        series: [
          {
            type: 'pie',
            radius: ['50%', '90%'],
            center: ['50%', '50%'],
            label: {
              show: false
            },
            color: this.colors,
            data: this.seriesData,
          },
        ]
      };
    },
    pieChartDimensions() {
      return this.overviewDisplay ? 'width:100%; height:100%;' : 'width:100%; height:220px;';
    },
    displayLoadingChart() {
      return !this.overviewDisplay && !this.initialized;
    },
    hasZeroPoints() {
      return this.userPoints === 0 && this.initialized;
    },
    initialized() {
      return this.show && !this.loading;
    },
    actionsPageURL() {
      return eXo.env.portal.portalName === 'public' && '/portal/public/overview/actions' || `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions`;
    },
  },
  watch: {
    initialized() {
      if (this.initialized) {
        this.$nextTick().then(() => this.initChart(this.option));
      }
    },
    widgetWidth(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.forceRefreshWidget();
      }
    },
  },
  created() {
    this.loading = true;
    Promise.all([
      this.getGamificationPointsStats(),
      this.getGamificationPoints()
    ]).finally(() => this.loading = false);
  },
  mounted() {
    this.widgetWidth = this.$el?.offsetWidth || this.widgetWidth;
    this.show = true;
    window.addEventListener('resize', this.onResize, {
      passive: true,
    });
    document.addEventListener('left-menu-stickiness', this.onResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize);
    document.removeEventListener('left-menu-stickiness', this.onResize);
  },
  methods: {
    onResize() {
      this.widgetWidth = this.$el.offsetWidth;
    },
    forceRefreshWidget() {
      if (this.refreshing) {
        return;
      }
      this.refreshedWidth = this.widgetWidth;

      this.show = false;
      this.refreshing = true;
      window.setTimeout(() => {
        this.refreshing = false;
        if (this.refreshedWidth === this.widgetWidth) {
          this.show = true;
        } else {
          this.forceRefreshWidget();
        }
      }, 200);
    },
    getGamificationPoints() {
      return this.$nextTick().then(() =>
        getGamificationPoints(this.period)
          .then(points => {
            this.userPoints = points && Number(points) || 0;
            if (this.userPoints !== 0) {
              this.$emit('seeAll', true);
            }
          })
      );
    },
    getGamificationPointsStats() {
      return getGamificationPointsStats(this.period)
        .then((data) => {
          const seriesData = [];
          const programLabels = [];
          for (let i=0;i < data.length; i++) {
            let serie;
            if (i > 4) {
              serie = {
                name: i,
                label: this.$t('exoplatform.gamification.gamificationinformation.domain.others'),
                value: seriesData[4].value + data[i].value
              };
              seriesData[4] = serie;
            } else {
              serie = {
                name: i,
                label: data[i].label,
                value: data[i].value
              };
              seriesData.push(serie);
            }
            if (i <= 4) {
              programLabels.push({
                name: serie.label,
                color: this.colors[i],
              });
            } else {
              programLabels[4].name = serie.label;
            }
          }
          this.programLabels = programLabels;
          this.seriesData = seriesData;
          return this.$nextTick();
        });
    },
    initChart(option) {
      return new Promise((resolve) => {
        window.require(['SHARED/eCharts'], echarts => {
          resolve(echarts);

          this.$emit('loaded', false);  
          this.$nextTick().then(() => {
            const chartContainer = document.getElementById('echartUserPoints');
            if (!chartContainer) {
              window.setTimeout(() => {
                this.initChart(option);
              }, 50);
              return;
            }
            const chart = echarts.init(chartContainer);
            chart.setOption(option, true);
          });
        });
      });
    },
    openHistoryDrawer() {
      this.$root.$emit('open-achievement', this.userPoints);
    },
  }
};
</script>