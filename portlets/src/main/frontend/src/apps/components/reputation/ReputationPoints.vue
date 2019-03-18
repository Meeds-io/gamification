<template>
    <section>
        <h5  class="mt-0">Achievements</h5>
        <div>

    <div    class="points" ><span><a href ="" @click.prevent="gotoLink()" > {{reputation.score}} Points </a></span> </div>

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
        //isPointsEnabled: false,

        methods: {

            gotoLink() {
                window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/achievements/${eXo.env.portal.profileOwner}`;

            },
        },
        created() {
            var url = window.location.pathname
            axios.get(`/rest/gamification/reputation/status`, { params: { 'url': url } })
                .then(response => {

                    this.reputation = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })



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