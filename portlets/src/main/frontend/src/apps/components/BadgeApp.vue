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
    .UIToolbarContainer .uiDropdownWithIcon>a{
         color: #4d5466 !important;
     }
     a, a:hover, a:focus {
         color: inherit !important;
         text-decoration: inherit;
     }
     a{
          color: inherit;
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