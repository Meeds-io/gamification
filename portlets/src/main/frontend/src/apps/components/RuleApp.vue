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

     .UIToolbarContainerDark a.dropdown-toggle {
    color: #fff;
      text-decoration: none;
}
 .UIToolbarContainerDark a.dropdown-toggle:hover, .UIToolbarContainerDark a.dropdown-toggle:focus{
      text-decoration: none;
       color: #fff;
}
        
       
       a{
           text-decoration: none;
           background-color: transparent;
           -webkit-text-decoration-skip: objects;
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
    .dropdown-toggle:after{
        display:none;
     }
     .dropdown-menu{
        font-size: inherit;
     }
    .uiCompanyNavigationPortlet .title, .uiSpaceNavigationPortlet .title {
        font-size: 15px;
    }
    .uiBreadcumbsNavigationPortlet .uiBreadcumbsNavigations .navItemSelected {
        font-size: 14px;
    }
    table.uiGrid.table.table-hover.uiGrid.table tr th {
        font-size: 16px;
    }
    .uiGrid.table td {
        font-size: 15px;
    }
    .uiSpaceNavigationPortlet .searchInput:before {
        top: 27px;
    }
</style>
