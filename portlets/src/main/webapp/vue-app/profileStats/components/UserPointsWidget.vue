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
  <v-layout
    row
    wrap
    mx-0>
    <v-flex
      d-flex
      xs12
      my-4>
      <v-layout
        v-if="overviewDisplay"
        row
        wrap
        mx-2
        align-start
        px-2>
        <v-flex
          d-flex>
          <div>
            <span class="pe-6 subtitle-2 profile-card-header">{{ $t('overview.myContributions.points') }}</span>
          </div>
        </v-flex>
      </v-layout>
      <v-layout
        v-else
        row
        wrap
        mx-2
        align-start
        px-2>
        <v-flex
          d-flex
          xs1>
          <v-icon
            color="grey darken-2"
            size="20"
            @click="toProfileStats()">
            {{ $vuetify.rtl && 'mdi-arrow-right' || 'mdi-arrow-left' }}
          </v-icon>
        </v-flex>
        <v-flex
          d-flex
          xs10
          justify-center>
          <div>
            <span class="pe-6 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.profileStatus.weeklyPoints') }}</span>
          </div>
        </v-flex>
        <v-flex
          class="text-right"
          d-flex
          xs1>
          <i
            class="uiIconInformation clickable primary--text my-auto ms-3 pb-2"
            @click="openHistoryDrawer"></i>
        </v-flex>
      </v-layout>
    </v-flex>
    <div style="margin:auto;">
      <div id="echartUserPoints" :style="pieChartDimensions"></div>
    </div>
  </v-layout>
</template>

<script>
import {getGamificationPointsStats, getGamificationPoints} from '../profilStatsAPI';
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
      period: 'WEEK',
      option: {
        title: [{
          text: 'Total',
          left: '73%',
          textStyle: {
            fontStyle: 'normal',
            color: '#4d5466',
            fontWeight: 'normal',
            fontSize: this.overviewDisplay ? '14' : '16',
          },
          subtext: '',
          subtextStyle: {
            fontStyle: 'normal',
            color: '#4d5466',
            fontWeight: 'bold',
            fontSize: this.overviewDisplay ? '14' : '18',
          },
          top: '40%',
          textAlign: 'center'
        }],
        tooltip: { 
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 1,
          top: 20,
          formatter: (name) => {
            return name.length > 20 ? `${name.substring(0, 20)  }...` : name;
          },
        },
        series: [
          {
            type: 'pie',
            radius: ['45%', '88%'],
            center: ['75%', '50%'],
            label: {
              normal: {
                show: false
              },

            },
            color: ['#4ad66d', '#ffe169', '#ff8fa3', '#20a8ea', '#C155F4', '#F7A35B', '#A0C7FF', '#FD6A6A', '#059d98', '#b7efc5',
              '#dbb42c', '#c9184a', '#1273d4', '#E65ABC', '#00FF56', '#B1F6FF', '#FFFF46', '#26a855', '#f10000', '#208b3a',
              '#c9a227', '#ffccd5', '#134d9b', '#E66CDC', '#58D68B', '#5CE6D3', '#f16a27', '#ac1c1e', '#eda3ff', '#1a7431',
              '#a47e1b', '#ff4d6d', '#62b0de', '#FF97D0', '#92e03a', '#f44336', '#3d6d8a', '#E0A5FF', '#FF9DB8', '#808080']
          }, 
        ]
      }
    };
  },
  computed: {
    pieChartDimensions() {
      return this.overviewDisplay ? 'width:370px; height:182px;' : 'width:400px; height:220px;';
    },
  },
  created() {
    this.getGamificationPointsStats();
    this.getGamificationPoints();
  },
  methods: {
    getGamificationPoints() {
      getGamificationPoints(this.period).then(
        (data) => {
          this.userPoints = data.points;
          this.option.title[0].subtext = data.points;
        }      
      );
    },
    getGamificationPointsStats() {
      getGamificationPointsStats(this.period).then(
        (data) => {
          this.option.series[0].data = JSON.parse(JSON.stringify(data).split('"label":').join('"name":'));
          for (let i=0;i<data.length;i++) {
            const optionSeriesName = this.option.series[0].data[i].name;
            if (this.$t(`exoplatform.gamification.gamificationinformation.domain.${optionSeriesName.charAt(0).toUpperCase()}${optionSeriesName.slice(1)}`).includes('exoplatform.gamification.gamificationinformation.domain')){
              this.option.series[0].data[i].name = optionSeriesName.charAt(0).toUpperCase()+optionSeriesName.slice(1);
            } else {
              this.option.series[0].data[i].name = this.$t(`exoplatform.gamification.gamificationinformation.domain.${optionSeriesName.charAt(0).toUpperCase()}${optionSeriesName.slice(1)}`);
            }
          }
          this.option.legend.data = this.option.series[0].data.name;
          this.initChart(this.option);
        }
      );
    },
    toProfileStats() {
      this.$emit('isProfileStats');
    },
    initChart(option) {
      $(document).ready(function(){
        const chartContainerId = document.getElementById('echartUserPoints');
        window.require(['SHARED/eCharts'], echarts => {
          const chart = echarts.init(chartContainerId);
          chart.setOption(option, true);
        });
      });
    },
    openHistoryDrawer() {
      this.$root.$emit('open-achievement', this.userPoints);
    },
  }
};
</script>