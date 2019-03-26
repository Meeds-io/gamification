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

            <!--
           <div v-else-if="currentTab === 'Gamificationhelp'">
               <Gamificationhelp></Gamificationhelp>
           </div>-->

           <div v-else-if="currentTab === 'MyBadges'">
               <MybadgesInformations></MybadgesInformations>
           </div>




       </div>




   </div>
</template>

<!--    GamificationInformations portlets  -->

<script>

    import MypointsHistoryList from './GamificationInformations/MypointsHistoryList'
    import GamificationInformationStyle from  '../../../../webapp/Style/GamificationInformationStyle.css'

    import MybadgesInformations from "./GamificationInformations/MybadgesInformations"
    import Gamificationhelp from "./GamificationInformations/Gamificationhelp"


    import TotalPointsFilter from "./GamificationInformations/TotalPointsFilter"
    import Tabs from 'vue-tabs-with-active-line';
    const TABS = [{
        title: 'My Points',
        value: 'MyPoints',
    },

    //,{
      //  title: 'HEP',
      //  value: 'Gamificationhelp',
  //  },

        {
        title: 'My Badges',
        value: 'MyBadges',
    }];
    export default {

        components:   {
            MybadgesInformations,
            Tabs,
            MypointsHistoryList,
            //Gamificationhelp,
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
            if ((!eXo && eXo.env) || !eXo.env.portal || !eXo.env.portal.userName || !eXo.env.portal.userName.length) {
                this.isGamificationEnabled = false;
                return;
            }
            if (eXo.env.portal.profileOwner && eXo.env.portal.profileOwner !== eXo.env.portal.userName) {
                this.isGamificationEnabled = false;
                return;
            } else {
                this.isGamificationEnabled = true;
                this.initMenuApp();
            }
        }

    }

</script>


