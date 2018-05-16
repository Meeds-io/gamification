<template>
    <div id="user-reputation-container" class="uiBox">
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


<style>

    a:not([href]):not([tabindex]), a:not([href]):not([tabindex]):focus, a:not([href]):not([tabindex]):hover{
        color:#fff ;
    }

  h4{
    color: #4d5466 !important;
    font-family: Helvetica,arial,sans-serif !important;
    font-size: 17px;
 }
h5 {
  color: #212529 !important;
    font-family: Helvetica,arial,sans-serif!important;
    font-size: 20px;
    padding: 5px 0px 10px 0px;
    text-align: center;
}
.uiBox{
    padding:5px;
}
    .left-border-row{
        border-left:0px !important;
    }
</style>