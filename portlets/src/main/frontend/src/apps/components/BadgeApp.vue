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



     .UIToolbarContainerDark a.dropdown-toggle {
    color: #fff;
      text-decoration: none;
         font-size: 14px!important;
         font-family: Helvetica, arial, sans-serif;
}
 .UIToolbarContainerDark a.dropdown-toggle:hover, .UIToolbarContainerDark a.dropdown-toggle:focus{
      text-decoration: none;
       color: #fff;
}
     
     a:hover{
         text-decoration: none;
         background-color: transparent;
         -webkit-text-decoration-skip: objects;
     }
    .btn-primary:hover {
        background-color: #476a9c;
    }

    .dropdown-toggle:after {
        display: none;
        font-size: 14px !important;
        font-family: Helvetica, arial, sans-serif;
    }

    .dropdown-menu {
        font-size: 14px !important;
        font-family: Helvetica, arial, sans-serif;
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
    /* Fix/ PLF-8495 */
    .tooltip.fade.bottom.in {
        opacity: 1 !important;
        text-transform: uppercase !important;
    }
    .uiCompanyNavigationPortlet .uiCompanyNavigations > li > a, h5 {
        box-sizing: content-box;
        font-family: Helvetica, arial, sans-serif;
    }
    a:hover{
        text-decoration: none;
        background-color: transparent;
        -webkit-text-decoration-skip: objects;
    }
    .uiCompanyNavigationPortlet .title, .uiSpaceNavigationPortlet .title {
        font-size: 14px;
    }

</style>