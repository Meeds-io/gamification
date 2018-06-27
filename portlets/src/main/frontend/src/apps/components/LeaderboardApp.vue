<template>


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

    .title{
        color:#4d5466;
        font-size:16px;
    }

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
        width: 219px !important;height: 200px !important;
    }
    .d2b-chart-frame{
        width: 239px;
        height: 226px !important;
    }
    .d2b-legend-frame{
        bottom: 2px!important;
        left: 11px !important;
        width: 226px !important;
        min-height: 50px !important;
        text-align: left !important;
    }
    .d2b-legend-svg-icon{
        width: 12px !important;
        height: 12px !important;
    }
    .popover{
        font-family: Helvetica,arial,sans-serif !important;
    }
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
        color: #4d5466 !important;
        font-family: myriad pro;
        border: none;
        border-bottom: 1px solid #f4f5f5;
        border-radius:inherit !important;
    }
    .d2b-legend:not(.d2b-vertical) .d2b-legend-icon {
        margin-top: -0.5px !important;
    }
    .d2b-legend:not(.d2b-vertical) .d2b-legend-item {
        display: block !important;
        width: 50% !important;
        float: left !important;
    }

    .d2b-legend:not(.d2b-vertical) .d2b-legend-item:not(:last-child){
        margin-right:0 !important;
    }
    .avatarCircle{
        width: 50px !important;
        height: 50px !important;
    }

</style>