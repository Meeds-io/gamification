<template>
    <section>
        <h5 class="mt-0">Achievements</h5>
        <div>

            <div class="points"><span>{{reputation.points}} Points</span></div>
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
                points: null,
                rank: ''
            },

        }
    }

    export default {
        data: initialData,
        methods: {


        },

        created() {
            var url = window.location.pathname
            axios.get(`/rest/gamification/reputation/point/status`, { params: { 'url': url } })
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
        background: url(/gamification-portlets/skin/images/ico-points.png)left center no-repeat;
        padding-top: 13px;
        min-height: 50px;
        position: relative;
        margin: 0 auto;
        display: table;
    }

    .points span {
        padding-left: 56px;
        top: 16px;
        position: relative;
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
</style>