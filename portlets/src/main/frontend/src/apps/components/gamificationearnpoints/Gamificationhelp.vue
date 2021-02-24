<!-- eslint-disable -->
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
  <div id="uiHowEarnPoint">
    <h1 class="how-title">{{ $t('exoplatform.gamification.leaderboard.Howearnpoints') }}</h1>

    <!-- Tabulation of domains -->
    <div class="tab">
      <button class="tablinks active">   <a href="gamification-earn-points">{{ $t('exoplatform.gamification.all') }}</a> </button>
      <button class="tablinks" @click="openCategory($event, 'soc')">{{ $t('exoplatform.gamification.gamificationinformation.domain.Social') }}</button>
      <button class="tablinks" @click="openCategory($event, 'Team')">{{ $t('exoplatform.gamification.gamificationinformation.domain.Teamwork') }}</button>
      <button class="tablinks" @click="openCategory($event, 'Know')">{{ $t('exoplatform.gamification.gamificationinformation.domain.Knowledge') }}</button>
      <button class="tablinks" @click="openCategory($event, 'feed')">{{ $t('exoplatform.gamification.gamificationinformation.domain.Feedback') }}</button>
      <button class="tablinks" @click="openCategory($event, 'reward')">{{ $t('exoplatform.gamification.gamificationinformation.domain.reward') }}</button>
    </div>


    <div>
      <div class="card card-body">
        <div class="clearfix ">
          <!-- Social Domain Rules -->

          <div class="card">
            <div id="headingOne" class="card-header">
              <h5 class="mb-0">
                <button
                  aria-controls="collapseOne"
                  aria-expanded="true"
                  class="btn btn-link collapsed"
                  data-target="#collapseOne"
                  data-toggle="collapse"
                  type="button">
                  {{ $t('exoplatform.gamification.gamificationinformation.domain.Social') }}
                </button>
              </h5>
            </div>

            <div
              id="collapseOne"
              aria-labelledby="headingOne"
              class="in collapse show"
              style="height: auto;">
              <div
                id="soc"
                class="tabcontent"
                name="soc"
                type="hidden">
                <div class="alignright">
                  <div class="uiBox">
                    <h6 class="title clearfix">{{ $t('exoplatform.gamification.gamificationinformation.domain.SocialActivities') }}</h6>

                    <div class="noContent">
                      <div
                        v-for="rule in orderedRules"
                        v-if="rule.area === 'Social' "
                        :key="rule.id"
                        class="row">
                        <div class="first-col">
                          {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${rule.event}`, rule.event) }}
                          -
                          {{ $t(`exoplatform.gamification.gamificationinformation.rule.description.${rule.title}`, rule.description) }}
                        </div>

                        <div class="second-col">{{ $t('exoplatform.gamification.gamificationinformation.domain.points', {0: rule.score}) }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="card">
            <div id="headingTwo" class="card-header">
              <!-- Teamwork Domain Rules -->

              <h5 class="mb-0">
                <button
                  aria-controls="collapseTwo"
                  aria-expanded="false"
                  class="btn btn-link collapsed"
                  data-target="#collapseTwo"
                  data-toggle="collapse"
                  type="button">
                  {{ $t('exoplatform.gamification.gamificationinformation.domain.Teamwork') }}
                </button>
              </h5>
            </div>

            <div
              id="collapseTwo"
              aria-labelledby="headingTwo"
              class="in collapse show">
              <div
                id="Team"
                class="tabcontent"
                name="Team">
                <div class="alignright">
                  <div class="uiBox">
                    <h6 class="title clearfix">{{ $t('exoplatform.gamification.gamificationinformation.domain.TeamworkActivities') }}</h6>

                    <div class="noContent">
                      <div
                        v-for="rule in orderedRules"
                        v-if="rule.area === 'Teamwork'"
                        :key="rule.id"
                        class="row">
                        <div class="first-col">
                          {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${rule.event}`, rule.event) }}
                          -
                          {{ $t(`exoplatform.gamification.gamificationinformation.rule.description.${rule.title}`, rule.description) }}
                        </div>

                        <div class="second-col">{{ $t('exoplatform.gamification.gamificationinformation.domain.points', {0: rule.score}) }}</div>
                      </div>
                    </div>
                    <!-- page iterator -->
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="card">
            <div id="headingThree" class="card-header">
              <!-- Knowledge Domain Rules -->

              <h5 class="mb-0">
                <button
                  aria-controls="collapseThree"
                  aria-expanded="false"
                  class="btn btn-link collapsed"
                  data-target="#collapseThree"
                  data-toggle="collapse"
                  type="button">
                  {{ $t('exoplatform.gamification.gamificationinformation.domain.Knowledge') }}
                </button>
              </h5>
            </div>

            <div
              id="collapseThree"
              aria-labelledby="headingThree"
              class="in collapse show">
              <div
                id="Know"
                class="tabcontent"
                name="Know">
                <div class="clearfix">
                  <div class="alignright">
                    <div class="uiBox">
                      <h6 class="title clearfix">{{ $t('exoplatform.gamification.gamificationinformation.domain.KnowledgeActivities') }}</h6>

                      <div class="noContent">
                        <div
                          v-for="rule in orderedRules"
                          v-if="rule.area === 'Knowledge'"
                          :key="rule.id"
                          class="row">
                          <div class="first-col">
                            {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${rule.event}`, rule.event) }}
                            -
                            {{ $t(`exoplatform.gamification.gamificationinformation.rule.description.${rule.title}`, rule.description) }}
                          </div>

                          <div class="second-col">{{ $t('exoplatform.gamification.gamificationinformation.domain.points', {0: rule.score}) }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>


          <div class="card">
            <div id="headingFOur" class="card-header">
              <h5 class="mb-0">
                <button
                  aria-controls="collapseFOur"
                  aria-expanded="false"
                  class="btn btn-link collapsed"
                  data-target="#collapseFOur"
                  data-toggle="collapse"
                  type="button">
                  {{ $t('exoplatform.gamification.gamificationinformation.domain.Feedback') }}
                </button>
              </h5>
            </div>

            <div
              id="collapseFOur"
              aria-labelledby="headingFOur"
              class="in collapse show">
              <div
                id="feed"
                class="tabcontent"
                name="feed">
                <div class="clearfix">
                  <!-- Feedback Domain Rules -->

                  <div class="alignright">
                    <div class="uiBox">
                      <h6 class="title clearfix">{{ $t('exoplatform.gamification.gamificationinformation.domain.FeedbackActivities') }}</h6>

                      <div class="noContent">
                        <div
                          v-for="rule in orderedRules"
                          v-if="rule.area === 'Feedback'"
                          :key="rule.id"
                          class="row">
                          <div class="first-col">
                            {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${rule.event}`, rule.event) }}
                            -
                            {{ $t(`exoplatform.gamification.gamificationinformation.rule.description.${rule.title}`, rule.description) }}
                          </div>

                          <div class="second-col">{{ $t('exoplatform.gamification.gamificationinformation.domain.points', {0: rule.score}) }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>


          <div class="card">
            <div id="headingFive" class="card-header">
              <h5 class="mb-0">
                <button
                  aria-controls="collapseFOur"
                  aria-expanded="false"
                  class="btn btn-link collapsed"
                  data-target="#collapseFive"
                  data-toggle="collapse"
                  type="button">
                  {{ $t('exoplatform.gamification.gamificationinformation.domain.Reward') }}
                </button>
              </h5>
            </div>

            <div
              id="collapseFive"
              aria-labelledby="headingFive"
              class="in collapse show"
              data-parent="">
              <div
                id="reward"
                class="tabcontent"
                name="reward">
                <div class="clearfix">
                  <!-- Reward Domain Rules -->

                  <div class="alignright">
                    <div class="uiBox">
                      <h6 class="title clearfix">{{ $t('exoplatform.gamification.gamificationinformation.domain.RewardActivities') }}</h6>

                      <div class="noContent">
                        <div
                          v-for="rule in orderedRules"
                          v-if="rule.area === 'reward'"
                          :key="rule.id"
                          class="row">
                          <div class="first-col">
                            {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${rule.event}`, rule.event) }}
                            -
                            {{ $t(`exoplatform.gamification.gamificationinformation.rule.description.${rule.title}`, rule.description) }}
                          </div>

                          <div class="second-col">{{ $t('exoplatform.gamification.gamificationinformation.domain.points', {0: rule.score}) }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- end all elements -->
        </div>
      </div>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
import Vue from 'vue';
import BootstrapVue from 'bootstrap-vue';
import axios from 'axios';
import _ from 'lodash';
Vue.use(BootstrapVue);


export default {

  data() {
    return {
      users: [],
      description: '',
      actionTitle: '',
      rules: [],
      categoryName: ['soc','Know','Team','feed']
    };
  },
  computed: {
    orderedRules: function () {
      return _.orderBy(this.rules, ['score','description'], ['asc', 'asc']);


    }
  },
  created() {
    axios.get('/portal/rest/gamification/gameficationinformationsboard/history/all', {
      params: {
        providerId: 'user',
        remoteId: eXo.env.portal.profileOwner,
      }
    })
      .then(response => {
        this.users = response.data;
      });
    axios.get('/portal/rest/gamification/rules/all')
      .then(response => {
        this.rules = response.data;
      })
      .catch(e => {
        this.errors.push(e);
      });
  },

  methods: {

    openCategory: function ($evt,categoryName){

      let i,tabcontent,tablinks;
      tabcontent = document.getElementsByClassName('tabcontent');
      for (i = 0; i < tabcontent.length; i++) {

        tabcontent[i].style.display = 'none';
      }
      tablinks = document.getElementsByClassName('tablinks');
      for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace('active', '');
        document.getElementById(categoryName).style.display = 'block';
      }
      document.getElementById(categoryName).style.display = 'block';
      $evt.currentTarget.className += ' active';




    }

  }
};



</script>

<style scoped>
    #uiHowEarnPoint .how-title {
        background: #578dc9;
        color: #fff;
        text-align: center;
        font-size: 26px;
        font-family: myriad pro, sans-serif;
        padding: 15px 0px;
        font-weight: normal;
        margin: 0;
    }

    #uiHowEarnPoint div#multiCollapse3 alignLeft {
        float: inherit !important;
    }

    #uiHowEarnPoint header {
        display: block;
        text-align: center;
        background: #578dc9;
        padding: 12px;
        margin: -1px;
        border: none;
    }

    #uiHowEarnPoint .nav-tabs {
        border-bottom: none;
    }

    #uiHowEarnPoint .nav-tabs>.active>a,
    .nav-tabs>.active>a:hover, .nav-tabs>.active>a:focus {
        color: #578dc9;
        background-color: #fff;
        border: none;
        border-radius: 20px;
        cursor: default;
    }


    #uiHowEarnPoint .tab button {
        background: #e1e8ee;
        color: #385989;
        text-align: center;
        font-weight: bold;
        overflow: hidden;
        border-radius: 20px;
        margin: 10px;
        overflow: hidden;
        padding: 5px 10px;
        border: none;
        outline: none;
        cursor: pointer;
    }

    #uiHowEarnPoint .tabcontent {
        padding: 6px 12px;
        border-top: none;
        overflow: hidden;
        background-color: #f1f1f1;
    }
    #uiHowEarnPoint #all .tabcontent {
        padding: 6px 12px;
        border-top: none;
        overflow: hidden;
        background-color: #f1f1f1;
    }
    #uiHowEarnPoint .tab button.active {
        background: #476a9c;
        color: #f0f0f0;
        border-radius: 20px;
        padding: 6px 10px;
        margin: auto 10px;
    }
    #uiHowEarnPoint .tab {
        text-align: center;
        background: #578dc9;
        padding: 10px;
    }



    #uiHowEarnPoint button.btn.btn-link{
        color:#385989;
        text-align: center;
        font-weight: bold;
        overflow: hidden;
        margin-left: 2%;
        border-radius: 20px;
        margin-bottom: 0px;
        transition: none;
        float:left;
        outline: none;
    }
    #uiHowEarnPoint #Team button.btn.btn-link.collapsed {
        margin-bottom: 70px;
        float:left;

    }
    #uiHowEarnPoint #all button.btn.btn-link.collapsed {
        margin-bottom: 70px;
        float:left;

    }
    #uiHowEarnPoint h5 button.btn.btn-link.collapsed {
        color: #578dc9;
        text-align: center;
        font-weight: bold;
        overflow: hidden;
        text-decoration: none;

        border: none;
        background: transparent;
        transition: none;
        float:left;
        display: block;
    }
    #uiHowEarnPoint button.btn.btn-link.collapsed::after {
        display: inline-block;
        width: 0;
        height: 0;
        margin-left: .255em;
        vertical-align: .255em;
        content: "";
        border-top: .3em solid;
        border-right: .3em solid transparent;
        border-bottom: 0;
        border-left: .3em solid transparent;


    }

    #uiHowEarnPoint button.btn.btn-link::after {
        display: inline-block;
        width: 0;
        height: 0;
        margin-left: .255em;
        vertical-align: .255em;
        content: "";
        border-top: 0;
        border-right: .3em solid transparent;
        border-bottom: .3em solid;
        border-left: .3em solid transparent;

    }


    #uiHowEarnPoint .uiBox .title{
        background: #578dc9;
        text-align: center;
        color: #fff !important;
        font-size: 16px;
        overflow: hidden;
        padding: 20px 0px;
        margin: 0;
        border-top-left-radius: 7px;
        border-top-right-radius: 7px;
        font-family: helvetica;
        font-weight: normal;
    }
    #uiHowEarnPoint .alignLeft {
        float: left;
        width: 50%;
        margin:0 auto;
        min-height: 240px;
        margin-top: 10px;
    }

    #uiHowEarnPoint .uiBox {

        border: 1px solid #dedede;
        border-radius: 7px;
        background: #fff;
        float: left;
        width: 90%;
        margin: 57px auto;
        text-align: center;
        clear: both;
        position: relative;
        margin-left: 5%;
        margin-bottom: 40px;
    }
    #uiHowEarnPoint .row{
        padding: 17px 0;
        color: #333333;
        font-family: helvetica;
        font-size: 14px;
        width: 100%;
        border-bottom: 1px solid #f4f5f5;
        float: left;
        margin-left: 0;

    }
    #uiHowEarnPoint .first-col{
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
    #uiHowEarnPoint .second-col{
        width: 20%;
        color: #4d5466;
        font-family: helvetica;
        text-align: center;
        float: right;
        font-size: 14px;
    }
    #uiHowEarnPoint .noContent .row:last-child{
        border-bottom:0px;
    }



    #uiHowEarnPoint h5 {
        text-align: center;
        display: flex;
    }

    #uiHowEarnPoint  a:active, a:hover {
        outline-width: 0;
    }

    #uiHowEarnPoint  a, a:hover, a:focus {
        text-decoration: none;

        color: #f0f0f0;
    }
    #uiHowEarnPoint  a{
        text-decoration: none;

        color: #385989;
    }
    #uiHowEarnPoint button.tablinks.active a {
        text-decoration: none;
        color: #e7eef9;
    }
    #uiHowEarnPoint button.btn.btn-link{

        display: none;
    }

</style>
