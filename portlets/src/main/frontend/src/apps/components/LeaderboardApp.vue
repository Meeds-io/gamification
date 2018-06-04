<template>
  <!-- when using a component in a template Vue.js expects the DOM element to be
    lowercased and dasherized. e.g. RulesList -> rules-list -->

    <leaderboard></leaderboard>
</template>
<script>

    import Leaderboard from './leaderboard/Leaderboard'
  export default {
    components: {
      Leaderboard
    }
  }
</script>

<style>

  a:not([href]):not([tabindex]), a:not([href]):not([tabindex]):focus, a:not([href]):not([tabindex]):hover{
    color:#fff ;
  }
   .fade{
    opacity:1 !important;
    }
    .fade:not(.show) {
        opacity: 1 !important;
    }
    .popover .arrow {
           width: auto !important;
           height: auto !important;
           margin: 0 !important;
    }
    .bs-popover-auto[x-placement^=top] .arrow, .bs-popover-top .arrow {
        bottom: calc((.5rem + 1px) * -2.1) !important;
    }
    .bs-popover-auto[x-placement^=left] .arrow, .bs-popover-left .arrow {
        right: calc((.5rem + 1px) * -2.2) !important;
     }
   .bs-popover-auto[x-placement^=bottom] .arrow, .bs-popover-bottom .arrow {
       top: calc((.5rem + 1px) * -2.2) !important;
   }
   .bs-popover-auto[x-placement^=left] .arrow, .bs-popover-right .arrow {

     right: calc((.5rem + 1px) * -2.2) !important;
    }
.d2b-tooltip {
    z-index: 99999999555555 !important;
}
.d2b-chart{
  width: 219px !important;height: 160px !important;
}
.d2b-chart-frame{
  width: 239px;
    height: 200px;
}
.d2b-legend-frame{
  bottom: 10px;
    left: 10px;
    width: 219px;
}
.d2b-legend-svg-icon{
  width: 12px !important;
    height: 12px !important;
}
.popover{font-family: Helvetica,arial,sans-serif !important;}
body {
    margin: 0 !important;
    font-family: Helvetica,arial,sans-serif !important;
    font-size: 14px !important;
    line-height: 20px !important;
    color: #333 !important;
    background-color: #fff !important;
}
.bs-popover-auto[x-placement^=left], .bs-popover-left{
    left: -211px!important;  
}
.vue-avatar--wrapper{
     margin: 3px 6px;
}

.list-group-item{
    padding: 5px;
    font-size: 14px;
    color: #4d5466;
    font-family: myriad pro;
}



</style>