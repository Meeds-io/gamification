<template>
    <div id="user-reputation-container">
        <reputation-points></reputation-points>
        <reputation-badges></reputation-badges>
    </div>
</template>
<script>

    import ReputationBadges from './reputation/ReputationBadges'
    import ReputationPoints from './reputation/ReputationPoints'

  export default {
    components: {
      ReputationBadges,
      ReputationPoints

    }
  }
</script>