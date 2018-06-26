<template>


    <manage-rules></manage-rules>
</template>
<script>

    import ManageRules from './rule/ManageRules'
  export default {
    components: {
      ManageRules
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
    .alert-dismissible .close{
      padding:0.5rem 1.25rem !important;
    }
    .alert .close{
      line-height: 16px;   
    }
    .btn-primary:hover {
      background-color: #476a9c;
    }

</style>