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

</style>