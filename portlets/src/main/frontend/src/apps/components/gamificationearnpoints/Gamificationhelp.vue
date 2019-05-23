<template>


            <div id="uiHowEarnPoint">

        <h1 class="how-title">How can I earn points ?</h1>

                <!-- Tabulation of domains -->
       <div class="tab">

          <button class="tablinks active">   <a href="gamification-earn-points">All Elements </a> </button>

           <button class="tablinks" v-on:click="openCategory($event, 'soc')">Social</button>
           <button class="tablinks" v-on:click="openCategory($event, 'Team')">Team Work</button>
           <button class="tablinks" v-on:click="openCategory($event, 'Know')">Knowledge</button>
           <button class="tablinks" v-on:click="openCategory($event, 'feed')">Feedback</button>
           <button class="tablinks" v-on:click="openCategory($event, 'reward')">Reward</button>



       </div>


         <div  >
             <div class="card card-body">
                <div class="clearfix ">
                    <!-- Social Domain Rules -->

                    <div class="card">
                        <div class="card-header" id="headingOne">
                            <h5 class="mb-0"><button aria-controls="collapseOne" aria-expanded="true" class="btn btn-link collapsed" data-target="#collapseOne" data-toggle="collapse" type="button">Social&nbsp;</button></h5>
                        </div>

                        <div aria-labelledby="headingOne" class="in collapse show"  id="collapseOne" style="height: auto;">

                                <div class="tabcontent" id="soc" name="soc" type="hidden">
                                <div class="alignright">

                                        <div class="uiBox">

                                        <h6 class="title clearfix">Social Activities</h6>

                                        <div class="noContent">
                                            <div class="row":key="rule.id" v-for="rule in orderedRules" v-if="rule.area === 'Social' ">

                                                <div class="first-col">{{ rule.description}}</div>

                                                <div class="second-col">{{ rule.score}} Pts</div>
                                            </div>
                                        </div>
                                    </div>
                            </div>
                        </div>
                    </div>
                    </div>

                    <div class="card">
                        <div class="card-header" id="headingTwo">
                            <!-- Teamwork Domain Rules -->

                            <h5 class="mb-0">
                                <button aria-controls="collapseTwo" aria-expanded="false" class="btn btn-link collapsed" data-target="#collapseTwo" data-toggle="collapse" type="button">Team work</button></h5>
                        </div>

                        <div aria-labelledby="headingTwo" class="in collapse show" id="collapseTwo">

                            <div class="tabcontent" id="Team" name="Team">
                                <div class="alignright">
                                    <div class="uiBox">

                                        <h6 class="title clearfix">Teamwork Activities</h6>

                                        <div class="noContent">


                                            <div class="row":key="rule.id" v-for="rule in orderedRules" v-if="rule.area === 'Teamwork'">
                                                <div class="first-col">{{ rule.description}}</div>

                                                <div class="second-col">{{ rule.score}} Pts</div>
                                            </div>
                                        </div>
                                        <!-- page iterator -->
                                </div>
                            </div>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header" id="headingThree">
                            <!-- Knowledge Domain Rules -->

                            <h5 class="mb-0">
                                <button aria-controls="collapseThree" aria-expanded="false" class="btn btn-link collapsed" data-target="#collapseThree" data-toggle="collapse" type="button">Knowledge</button></h5>
                        </div>

                        <div aria-labelledby="headingThree" class="in collapse show"  id="collapseThree">

                            <div class="tabcontent" id="Know" name="Know">

                                <div class="clearfix">

                                    <div class="alignright">

                                        <div class="uiBox">

                                            <h6 class="title clearfix">Knowledge Activities</h6>

                                            <div class="noContent">




                                                <div class="row":key="rule.id" v-for="rule in orderedRules" v-if="rule.area === 'Knowledge'">

                                                    <div class="first-col">{{ rule.description}}</div>

                                                    <div class="second-col">{{ rule.score}} Pts</div>
                                                </div>


                                            </div>
                                    </div>
                                </div>


                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="card">
                        <div class="card-header" id="headingFOur">
                            <h5 class="mb-0"><button aria-controls="collapseFOur" aria-expanded="false" class="btn btn-link collapsed" data-target="#collapseFOur" data-toggle="collapse" type="button">Feedback</button></h5>
                        </div>

                        <div aria-labelledby="headingFOur" class="in collapse show" id="collapseFOur">

                            <div class="tabcontent" id="feed" name="feed">

                                <div class="clearfix">
                                    <!-- Feedback Domain Rules -->

                                    <div class="alignright">

                                        <div class="uiBox">

                                            <h6 class="title clearfix">Feedback Activities</h6>

                                            <div class="noContent">




                                                <div class="row":key="rule.id" v-for="rule in orderedRules" v-if="rule.area === 'Feedback'">

                                                    <div class="first-col">{{ rule.description}}</div>

                                                    <div class="second-col">{{ rule.score}} Pts</div>
                                                </div>


                                            </div>
                                        </div>
                                    </div>


                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="card">
                        <div class="card-header" id="headingFive">
                            <h5 class="mb-0"><button aria-controls="collapseFOur" aria-expanded="false" class="btn btn-link collapsed" data-target="#collapseFive" data-toggle="collapse" type="button">Reward</button></h5>
                        </div>

                        <div aria-labelledby="headingFive" class="in collapse show" data-parent="" id="collapseFive">

                            <div class="tabcontent" id="reward" name="reward">

                                <div class="clearfix">
                                    <!-- Reward Domain Rules -->

                                    <div class="alignright">

                                        <div class="uiBox">

                                            <h6 class="title clearfix">Reward Activities</h6>

                                            <div class="noContent">




                                                <div class="row":key="rule.id" v-for="rule in orderedRules" v-if="rule.area === 'reward'">

                                                    <div class="first-col">{{ rule.description}}</div>

                                                    <div class="second-col">{{ rule.score}} Pts</div>
                                                </div>


                                            </div>
                                        </div>
                                    </div>


                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- end all elements --></div>
            </div>
        </div>



    </div>

</template>

<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
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
            categoryName:['soc','Know','Team','feed']
               }
    },
    computed: {
        orderedRules: function () {
            return _.orderBy(this.rules, ['score','description'], ['asc', 'asc'])


        }
    },

    methods: {

        openCategory:function ($evt,categoryName){

            var i,tabcontent,tablinks;
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
            $evt.currentTarget.className += " active";




        }

        },


    created() {
        var url = window.location.pathname
        axios.get(`/rest/gamification/gameficationinformationsboard/history/all`, { params: { 'url': url } })
            .then(response => {
                this.users = response.data;
            });
        axios.get(`/rest/gamification/rules/all`)
            .then(response => {
                this.rules = response.data;
            })
            .catch(e => {
                this.errors.push(e)
            })
    }
       }



</script>

<style scoped>
    #uiHowEarnPoint .how-title {
        background: #578dc9;
        color: #fff;
        text-align: center;
        font-size: 26px;
        font-family: myriad pro;
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
        padding: 5px 10px;
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
</style>
