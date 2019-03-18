<template>

    <div id="achivements" class="uiBox">




        <tabs
                :tabs="tabs"
                :currentTab="currentTab"
                @onClick="handleClick"
        />
        <div class="content">

            <div v-if="currentTab === 'MyPoints'">
                <total-points-filter></total-points-filter>

                <mypoints-history-list></mypoints-history-list>


            </div>
            <div v-else-if="currentTab === 'MyBadges'">
                <MybadgesInformations></MybadgesInformations>
            </div>

            <!--

             <div v-else-if="currentTab === 'Gamificationhelp'">
                 <Gamificationhelp></Gamificationhelp>
             </div> -->

        </div>




    </div>
</template>
<!--    GamificationInformations portlets  -->

<script>

    import MypointsHistoryList from './GamificationInformations/MypointsHistoryList'
    import MybadgesInformations from "./GamificationInformations/MybadgesInformations"
    //import Gamificationhelp from "./GamificationInformations/Gamificationhelp"
    import TotalPointsFilter from "./GamificationInformations/TotalPointsFilter"
    import Tabs from 'vue-tabs-with-active-line';
    const TABS = [{
        title: 'My Points',
        value: 'MyPoints',
    }, {
        title: 'My Badges',
        value: 'MyBadges',
    }];
    export default {

        components:   {
            MybadgesInformations,
            Tabs,
            MypointsHistoryList,
            //    Gamificationhelp,
            TotalPointsFilter,

        },
        data: () => ({
            isGamificationEnabled: false,
            tabs: TABS,
            currentTab: 'MyPoints',
        }),
        methods:{
            handleClick(newTab) {
                this.currentTab = newTab;
            },
            maximize() {
                window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/achievements/${eXo.env.portal.profileOwner}`;

            },

            initMenuApp() {
                if (!this.isGamificationEnabled ) {
                    return;
                }
                this.$nextTick(() => {
                    if ($('#myGamificationTab').length) {
                        return;
                    }
                    if (!$('.userNavigation .item').length) {
                        setTimeout(this.initMenuApp, 500);
                        return;
                    }
                    $('.userNavigation').append(` \
          <li id='myGamificationTab' class='item active'> \
            <a href='${eXo.env.portal.context}/${eXo.env.portal.portalName}/achievements/${eXo.env.portal.profileOwner}'>
              <div class='uiIconAppGamification uiIconDefaultApp' /> \
              <span class='tabName'>My Achievements</span> \
            </a> \
          </li>`);
                    $(window).trigger('resize');
                });
            },
        },
        created() {

                this.isGamificationEnabled = true;
                this.initMenuApp();


        }

    }

</script>


<style>

    .fade {
        opacity: 1 !important;
    }

    .fade:not(.show) {
        opacity: 1 !important;
    }

    .popover .arrow {
        width: auto !important;
        height: auto !important;
        margin: 0 !important;
    }

    .bs-popover-auto[x-placement^=top] .arrow,
    .bs-popover-top .arrow {
        bottom: calc((.5rem + 1px) * -2.1) !important;
    }

    .bs-popover-auto[x-placement^=left] .arrow.container-fluid .bs-popover-left .arrow {
        right: calc((.5rem + 1px) * -2.2) !important;
    }

    .bs-popover-auto[x-placement^=bottom] .arrow,
    .bs-popover-bottom .arrow {
        top: calc((.5rem + 1px) * -2.2) !important;
    }

    .bs-popover-auto[x-placement^=left] .arrow,
    .bs-popover-right .arrow {

        right: calc((.5rem + 1px) * -2.2) !important;
    }

    .d2b-tooltip {
        z-index: 99999999555555 !important;
    }



    .d2b-legend-frame {
        bottom: 2px !important;
        left: 11px !important;
        width: 226px !important;
        min-height: 50px !important;
        text-align: left !important;
    }

    .d2b-legend-svg-icon {
        width: 12px !important;
        height: 12px !important;
    }

    .popover {
        font-family: Helvetica, arial, sans-serif !important;
    }

    .bs-popover-auto[x-placement^=left],
    .bs-popover-left {
        left: 0px !important;
    }

    .vue-avatar--wrapper {
        margin: 3px 6px;
        width: 40px !important;
    }

    .d2b-legend:not(.d2b-vertical) .d2b-legend-icon {
        margin-top: -0.5px !important;
    }

    .d2b-legend:not(.d2b-vertical) .d2b-legend-item {
        display: block !important;
        width: 50% !important;
        float: left !important;
    }

    .bs-popover-auto[x-placement^=left] .arrow,
    .bs-popover-left .arrow {
        right: calc((.5rem + 1px) * -2.1) !important;
        width: .5rem;
        height: 1rem;
        margin: .3rem 0;
    }

    .d2b-legend:not(.d2b-vertical) .d2b-legend-item:not(:last-child) {
        margin-right: 0 !important;
    }
    .d2b-chart-frame .d2b-breadcrumbs-frame:not(.d2b-vertical).d2b-legend-frame, .d2b-chart-frame .d2b-legend-frame:not(.d2b-vertical).d2b-legend-frame {
        height: auto !important;
    }
    .table thead th{font-size: 0.9em;}

    .table td, .table th{

        padding: 8px;
        line-height: 1.42857143;
        vertical-align: top;
        text-align:center;

    }
    .table-hover tbody tr:hover{
        cursor: pointer;
    }
    .table-striped>tbody>tr:nth-of-type(odd){
        background-color: #f9f9f9;
    }
    .uiIconAppGamification:before {
        content: "\ebdb"!important;
    }



    .uiProfileMenu .userNavigation > .active > a > span, .uiSpaceMenu .spaceMenuTab > .active > a > span {
        color: #578dc9;
    }
    .uiProfileMenu .userNavigation > .active > a, .uiProfileMenu .userNavigation > .active > a:hover, .uiSpaceMenu .spaceMenuTab > .active > a, .uiSpaceMenu .spaceMenuTab > .active > a:hover {
        background: #ffffff;
        background-image: none;
        color: #578dc9;
        border-bottom: 4px solid #578dc9;
        border-radius: 0;
        box-shadow: none;
    }
    .uiProfileMenu .userNavigation > .active > a > div, .uiSpaceMenu .spaceMenuTab > .active > a > i {
        color: #578dc9;
    }
    .card .uiBox {

        border: 1px solid #dedede;
        border-radius: 7px;
        background: #fff;
        float: left;
        width: 90%;
        margin: 0 auto;
        text-align: center;
        clear: both;
        position: relative;
        margin-left: 5%;
        margin-bottom: 40px;
    }
    .row{
        padding: 17px 0;
        color: #333333;
        font-family: helvetica;
        font-size: 14px;
        width: 100%;
        border-bottom: 1px solid #f4f5f5;
        float: left;
        margin-left: 0;

    }
    button.btn.btn-link {
        text-decoration: none;
        width: max-content;
    }
    .first-col{
        width: 80%;
        color: #333333;
        font-family: helvetica;
        left: 15px;
        float: left;
        position: relative;
        font-size: 14px;
        margin-left: 0;
        text-align: left;

    }

    /*tabs */

    button.tabs__item {
        background: transparent;
        border: none;
        font-weight: 700;
        height: 45px;
        margin: 5px;
        font-size: 14px;
        color: #999;
        border-radius: 2px;
        padding: 15px;
        outline: none;
    }
    button.tabs__item.tabs__item_active {

        border-bottom: 3px solid #578dc9;
        color: #4e5467;
    }
</style>