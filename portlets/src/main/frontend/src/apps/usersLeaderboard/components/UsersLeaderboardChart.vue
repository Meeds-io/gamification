<template>
  <v-card flat class="pa-0">
    <v-card-text class="pa-0">
      <v-flex :id="id" class="usersLeaderboardChartParent" />
    </v-card-text>
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
  },
  data () {
    return {
      chartData: null,
      id: `Chart${parseInt(Math.random() * 10000)
        .toString()
        .toString()}`,
    }
  },
  computed: {
    colors() {
      return this.domains && this.domains.map(domain => domain.color) || [];
    },
    chartSettings() {
      return this.chartData && {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)'
        },
        color: this.chartData.map(data => data.color),
        series: [{
          type: 'pie',
          radius: ['50%', '70%'],
          data: this.chartData,
        }]
      };
    }
  },
  watch: {
    chartSettings() {
      window.setTimeout(() => {
        this.init();
      }, 200);
    },
  },
  mounted() {
    const hexChars = '0123456';
    this.domains.forEach(domain => {
      if (!domain.color) {
        let color = '#';
        for (let i = 0; i < 6; i++) {
          color += hexChars[Math.floor(Math.random() * hexChars.length)];
        }
        domain.color = color;
      }
    });
    this.loadChartData();
  },
  methods: {
    loadChartData() {
      fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard/stats?username=${this.username}`, {
        credentials: 'include',
      })
        .then(resp => resp && resp.ok && resp.json())
        .then(chartData => {
          chartData.forEach(statData => {
            const domain = this.domains.find(tmpDomain => tmpDomain.title === statData.label);
            statData.name = domain.label;
            statData.color = domain.color;
          });
          this.chartData = chartData;
        });
    },
    init() {
      if (!this.chartSettings) {
        return;
      }
      const chart = echarts.init($(`#${this.id}`)[0]);
      chart.setOption(this.chartSettings, true);
    }
  }
}
</script>