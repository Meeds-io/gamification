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

</style>