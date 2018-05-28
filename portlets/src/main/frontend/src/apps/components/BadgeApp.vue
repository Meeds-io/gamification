<template>
    <manage-badges></manage-badges>
</template>
<script>

    import ManageBadges from './badge/ManageBadges'
  export default {
    components: {
      ManageBadges
    }
  }
</script>


<style>

    a:not([href]):not([tabindex]), a:not([href]):not([tabindex]):focus, a:not([href]):not([tabindex]):hover{
        color:#fff ;
    }

.alert{
           max-width: 98%;
           margin: 0 auto;
           top: 7px;
}
</style>