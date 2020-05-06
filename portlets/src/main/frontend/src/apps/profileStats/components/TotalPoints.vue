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
            <span class="pr-6 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.profileStatus.totalPoints') }}</span>
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
      <div id="echartTotalPoint" style="width:320px; height:220px; "></div>
    </div>
  </v-layout>
</template>

<script>
  import {getGamificationPointsStats, getGamificationPoints} from '../profilStatsAPI'
  export default {

    data() {
      return {
        totalPoints: 0,
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
        getGamificationPoints().then(
          (data) => {
            this.totalPoints = data.points;
            this.option.title[0].subtext = data.points;
          }      
        )
      },
      getGamificationPointsStats() {
        getGamificationPointsStats().then(
          (data) => {
            this.option.series[0].data = JSON.parse(JSON.stringify(data).split('"label":').join('"name":'));
            for(let i=0;i<data.length;i++) {
              const optionSeriesName = this.option.series[0].data[i].name;
              this.option.series[0].data[i].name = optionSeriesName.charAt(0).toUpperCase() + optionSeriesName.slice(1);
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
          const chartContainerId = document.getElementById('echartTotalPoint');
          const chart = echarts.init(chartContainerId);
          chart.setOption(option, true);
         });
      },
      openHistoryDrawer() {
        this.$root.$emit('open-achievement', this.totalPoints);
      },
    }
  }
</script>