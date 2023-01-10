<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
contact@meeds.io
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <manage-badges />
</template>
<script>

import ManageBadges from './ManageBadges.vue';

export default {
  components: {
    ManageBadges
  }
};
</script>

<style>

  body {
    background-color: var(--allPagesLightGrey, #f2f2f2) !important;
  }

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