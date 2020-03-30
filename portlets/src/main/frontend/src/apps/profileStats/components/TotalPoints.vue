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
            @click="toProfileStats()">mdi-arrow-left</v-icon>
        </v-flex>
        <v-flex
          d-flex
          xs11
          justify-center>
          <div>
            <span class="pr-6 text-uppercase subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.totalPoints') }}</span>
          </div>
        </v-flex>
      </v-layout>
    </v-flex>
    <div style="margin: auto">
      <v-chart :options="option" style="width: 315px; height: 220px"/>
    </div>
  </v-layout>
</template>

<script>
  import ECharts from 'vue-echarts';
  import 'echarts/lib/chart/pie';
  import 'echarts/lib/component/tooltip';
  import 'echarts/lib/component/graphic';
  import 'echarts/lib/component/legend';
  import {getGamificationPointsStats, getGamificationPoints} from '../profilStatsAPI'

  export default {
    components: {
      'v-chart': ECharts
    },
    data() {
      return {
        option : {
          tooltip : {
            trigger: 'item',
            formatter: '{b} : {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 5,
            top:12,
          },
          graphic:{
            type:'text',
            right:96,
            top: 95,
            style: {
              text: '',
              font: '16px arial',
              fill:'#4d5466',
              width: 30,
              height: 30
            }
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
            this.option.graphic.style.text = `Total\n ${data.points}`
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
            this.option.legend.data = this.option.series[0].data.name
          }
        )
      },
      toProfileStats() {
        this.$emit('isProfileStats');
      }
    }
  }
</script>