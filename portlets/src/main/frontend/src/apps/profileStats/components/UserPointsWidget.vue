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
            mdi-arrow-left
          </v-icon>
        </v-flex>
        <v-flex
          d-flex
          xs10
          justify-center>
          <div>
            <span class="pr-6 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.profileStatus.weeklyPoints') }}</span>
          </div>
        </v-flex>
        <v-flex
          class="text-right"
          d-flex
          xs1>
          <i
            class="uiIconInformation clickable primary--text my-auto ml-3 pb-2"
            @click="openHistoryDrawer"></i>
        </v-flex>
      </v-layout>
    </v-flex>
    <div style="margin:auto;">
      <div id="echartUserPoints" style="width:320px; height:220px; "></div>
    </div>
  </v-layout>
</template>

<script>
  import {getGamificationPointsStats, getGamificationPoints} from '../profilStatsAPI'
  export default {

    data() {
      return {
        userPoints: 0,
        period: 'WEEK',
        option : {
          title: [{
            text: 'Total',
            left: '63%',
            textStyle: {
              fontStyle: 'normal',
              color: '#4d5466',
              fontWeight: 'normal',
              fontSize: '16',
            },
            subtext :``,
            subtextStyle: {
              fontStyle: 'normal',
              color: '#4d5466',
              fontWeight: 'bold',
              fontSize: '18',
            },
            top:'40%',
            textAlign: 'center'
          }],
          tooltip : { 
            trigger: 'item',
            formatter: '{b} : {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 5,
            top:12,
          },
          series : [
            {
              type: 'pie',
              radius: ['45%', '88%'],
              center: ['65%', '50%'],
              label: {
                normal: {
                  show: false
                },

              },
            }, 
          ]
        }
      };
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
        )
      },
      getGamificationPointsStats() {
        getGamificationPointsStats(this.period).then(
          (data) => {
            this.option.series[0].data = JSON.parse(JSON.stringify(data).split('"label":').join('"name":'));
            for(let i=0;i<data.length;i++) {
              const optionSeriesName = this.option.series[0].data[i].name;
              this.option.series[0].data[i].name = this.$t(`exoplatform.gamification.gamificationinformation.domain.${optionSeriesName.charAt(0).toUpperCase()}${optionSeriesName.slice(1)}`);
            }
            this.option.legend.data = this.option.series[0].data.name;
            this.initChart(this.option);
          }
        )
      },
      toProfileStats() {
        this.$emit('isProfileStats');
      },
      initChart(option) {
        $(document).ready(function(){
          const chartContainerId = document.getElementById('echartUserPoints');
          const chart = echarts.init(chartContainerId);
          chart.setOption(option, true);
         });
      },
      openHistoryDrawer() {
        this.$root.$emit('open-achievement', this.userPoints);
      },
    }
  }
</script>