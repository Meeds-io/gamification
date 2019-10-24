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

    .UIToolbarContainer .NormalContainerBlock .ToolbarContainer .TRContainer {
        background: #333333;
    }
    .UIToolbarContainerDark .uiDropdownWithIcon > a, .UIToolbarContainer .uiDropdownWithIcon > a > i {
        color: #ffffff !important;
    }
    .UIToolbarContainerDark .uiDropdownWithIcon.dropdown > a:hover,
    .UIToolbarContainerDark .uiHelpPLFToolbarPortlet > a:hover,
    .UIToolbarContainerDark .uiDropdownWithIcon.dropdown.open > a,
    .UIToolbarContainerDark .uiHelpPLFToolbarPortlet.open > a{
        background: none;
        border-bottom: solid 3px #578dc9;
        box-sizing: border-box;
    }
     a, a:hover, a:focus {
          color: inherit;
          text-decoration: none;
     }
     a{
         text-decoration: none;
         background-color: transparent;
         -webkit-text-decoration-skip: objects;
     }
    .btn-primary:hover {
        background-color: #476a9c;
    }

    .dropdown-toggle:after {
        display: none;
    }

    .dropdown-menu {
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