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
  <section>
    <h5 class="mt-0">Achievements</h5>
    <div>
      <div class="points"><span><a href="MyConnection Achivements" @click.prevent="gotoLink()"> {{ reputation.score }} Points </a></span> </div>
    </div>
  </section>
</template>
<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import axios from 'axios';

    Vue.use(BootstrapVue);

    const initialData = () => {
        return {
            reputation: {
                score: null,
                rank: ''
            },

        }
    }

    export default {
        data: initialData,
        created() {
            const url = window.location.pathname
            axios.get(`/portal/rest/gamification/reputation/status`, { params: { 'url': url } })
                .then(response => {

                    this.reputation = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })



        },
        //isPointsEnabled: false,

        methods: {

            gotoLink() {
                window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/achievements/${eXo.env.portal.profileOwner}`;

            },
        }


    }
</script>

<style scoped>
    .points {
        text-align: center;
        color: #333;
        font-family: open_sansregular;
        font-size: 14px;
        background: url(/gamification-portlets/skin/images/ico-points.png)0 no-repeat;
        position: relative;
        margin: 0 auto;
        display: table;
        height: 50px;
    }

    .points span {
        padding-left: 56px;
        position: relative;
        line-height: 44px;
    }

    .rep-points {
        padding: 0px 20px;
        clear: both;
        min-height: 50px;
        margin: 0 auto;
    }

    .rep-points img {
        float: left;
    }
    .active {pointer-events: none;
        cursor: default;
    }
</style>