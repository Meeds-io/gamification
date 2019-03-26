<template>
    <div id="uiHowEarnPoint">
        <h1 class="how-title">How can I earn points ?</h1>

        <div class="tab"><button class="tablinks active" onclick="openCategory(event, 'all')">All Domains</button><button class="tablinks" onclick="openCategory(event, 'soc')">Social</button><button class="tablinks" onclick="openCategory(event, 'Knowledge')">Knowledge</button><button class="tablinks" onclick="openCategory(event, 'Team')">Team Work</button></div>

        <div class="tabcontent" id="all" style="display:block">
            <div class="card card-body"><!-- all elements -->
                <div class="clearfix ">
                    <div class="card">
                        <div class="card-header" id="headingOne">
                            <h5 class="mb-0"><button aria-controls="collapseOne" aria-expanded="true" class="btn btn-link collapsed" data-target="#collapseOne" data-toggle="collapse" type="button">Social&nbsp;</button></h5>
                        </div>

                        <div aria-labelledby="headingOne" class="in collapse show" data-parent="#accordionExample" id="collapseOne" style="height: auto;">
                            <div class="card-body">
                                <div class="alignLeft">
                                    <div class="uiBox"  :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Social'">
                                        <h6 class="title clearfix">Activities rewards</h6>

                                        <div class="noContent" :key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">
                                            <div class="row">
                                                <div class="first-col">{{ rule.description}}</div>

                                                <div class="second-col">{{ rule.score}} Pts</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="alignLeft">
                                    <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Social'">
                                        <h6 class="title clearfix">Comments rewards</h6>

                                        <div class="noContent" :key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">

                                            <div class="row">
                                                <div class="first-col">{{ rule.description}}</div>

                                                <div class="second-col">{{ rule.score}} Pts</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="clearfix">
                                    <div class="alignLeft">
                                        <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Social'">
                                            <h6 class="title clearfix">Other rewards</h6>

                                            <div class="noContent" :key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">

                                                <div class="row">
                                                    <div class="first-col">{{ rule.description}}</div>

                                                    <div class="second-col">{{ rule.score}} Pts</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="alignLeft">
                                        <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Social'">
                                            <h6 class="title clearfix">Likes rewards</h6>

                                            <div class="noContent":key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">


                                                <div class="row">
                                                    <div class="first-col">{{ rule.description}}</div>

                                                    <div class="second-col">{{ rule.score}} Pts</div>
                                                </div>
                                                <!-- page iterator --></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header" id="headingTwo">
                            <h5 class="mb-0"><button aria-controls="collapseTwo" aria-expanded="false" class="btn btn-link collapsed" data-target="#collapseTwo" data-toggle="collapse" type="button">Team work</button></h5>
                        </div>

                        <div aria-labelledby="headingTwo" class="collapse" data-parent="#accordionExample" id="collapseTwo" style="height:0px;">
                            <div class="card-body">
                                <div class="alignLeft">
                                    <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Teamwork'">
                                        <h6 class="title clearfix">Team work</h6>

                                        <div class="noContent" :key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">


                                            <div class="row">
                                                <div class="first-col">{{ rule.description}}</div>

                                                <div class="second-col">{{ rule.score}} Pts</div>
                                            </div>
                                        </div>
                                        <!-- page iterator --></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header" id="headingThree">
                            <h5 class="mb-0"><button aria-controls="collapseThree" aria-expanded="false" class="btn btn-link collapsed" data-target="#collapseThree" data-toggle="collapse" type="button">Knowledge</button></h5>
                        </div>

                        <div aria-labelledby="headingThree" class="collapse" data-parent="#accordionExample" id="collapseThree">
                            <div class="card-body">
                                <div class="clearfix">
                                    <div class="alignLeft">
                                        <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Knowledge'">
                                            <h6 class="title clearfix">Forum</h6>

                                            <div class="noContent">


                                                <div class="row">
                                                    <div class="first-col">{{ rule.description}}</div>

                                                    <div class="second-col">{{ rule.score}} Pts</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="alignLeft">
                                        <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Knowledge'">
                                            <h6 class="title clearfix">Wiki</h6>

                                            <div class="noContent":key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">


                                                <div class="row">
                                                    <div class="first-col">{{ rule.description}}</div>

                                                    <div class="second-col">{{ rule.score}} Pts</div>
                                                </div>
                                            </div>
                                            <!-- page iterator --></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- end all elements --></div>
            </div>
        </div>


        <div class="tabcontent" id="soc"><!-- Social -->
            <div class="card card-body">
                <div class="alignLeft">
                    <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Social'">
                        <h6 class="title clearfix">Activities rewards</h6>

                        <div class="noContent" :key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">


                            <div class="row">
                                <div class="first-col">{{ rule.description}}</div>

                                <div class="second-col">{{ rule.score}} Pts</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="alignLeft">
                    <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Social'">
                        <h6 class="title clearfix">Comments rewards</h6>

                        <div class="noContent":key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">


                            <div class="row">
                                <div class="first-col">{{ rule.description}}</div>

                                <div class="second-col">{{ rule.score}} Pts</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="clearfix">
                    <div class="alignLeft">
                        <div class="uiBox":key="rule.id" v-for="rule in rules" v-if="rule.area !=''">
                            <h6 class="title clearfix">Other rewards</h6>
                            <div class="noContent" :key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">
                                <div class="row">
                                    <div class="first-col">{{ rule.description}}</div>

                                    <div class="second-col">{{ rule.score}} Pts</div>
                                </div>


                            </div>
                        </div>
                    </div>

                    <div class="alignLeft">
                        <div class="uiBox":key="rule.id" v-for="rule in rules" v-if="rule.area === 'Social'">
                            <h6 class="title clearfix">Likes rewards</h6>

                            <div class="noContent" :key="user.receiver" v-for="(user, index) in users" v-if=" rule.title === user.actionTitle">


                                <div class="row">
                                    <div class="first-col">{{ rule.description}}</div>

                                    <div class="second-col">{{ rule.score}} Pts</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- end Social --></div>
        </div>



        <div class="tabcontent" id="Team">
            <div class="card card-body"><!--team work -->
                <div class="card-body">
                    <div class="alignLeft">
                        <div class="uiBox":key="rule.id" v-for="rule in rules" v-if="rule.area === 'Teamwork'">
                            <h6 class="title clearfix">Team work</h6>

                            <div class="noContent">


                                <div class="row">
                                    <div class="first-col">{{ rule.description}}</div>

                                    <div class="second-col">{{ rule.score}} Pts</div>
                                </div>
                            </div>
                            <!-- page iterator --></div>
                    </div>
                </div>
                <!--end team work--></div>
        </div>


        <div class="tabcontent" id="Knowledge" >
            <div class="card card-body"><!--knowledge -->
                <div class="card-body">
                    <div class="alignLeft">
                        <div class="uiBox":key="rule.id" v-for="rule in rules" v-if="rule.area === 'Knowledge'">
                            <h6 class="title clearfix">Forum</h6>

                            <div class="noContent">
                                <div class="row">
                                    <div class="first-col">{{ rule.description}}</div>

                                    <div class="second-col">{{ rule.score}} Pts</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="alignLeft">
                        <div class="uiBox" :key="rule.id" v-for="rule in rules" v-if="rule.area === 'Knowledge'">
                            <h6 class="title clearfix">Wiki</h6>

                            <div class="noContent">
                                <div class="row">
                                    <div class="first-col">{{ rule.description}}</div>

                                    <div class="second-col">{{ rule.score}} Pts</div>
                                </div>
                            </div>
                            <!-- page iterator --></div>
                    </div>
                </div>
                <!--end knowledge--></div>
        </div>
    </div>


</template>

<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import axios from 'axios';

    Vue.use(BootstrapVue);


export default {

    data() {
        return {
            users: [],
            description: '',
            actionTitle: '',
            rules: []
        }
    },


    methods: {

        openCategory(evt, categoryName){}

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
    .how-title {
        background: #578dc9;
        color: #fff;
        text-align: center;
        font-size: 26px;
        font-family: myriad pro;
        padding: 15px 0px;
        font-weight: normal;
        margin: 0;
    }

    div#multiCollapse3 .alignLeft {
        float: inherit !important;
    }

    header {
        display: block;
        text-align: center;
        background: #578dc9;
        padding: 12px;
        margin: -1px;
        border: none;
    }

    .nav-tabs {
        border-bottom: none;
    }

    .nav-tabs>.active>a, .nav-tabs>.active>a:hover, .nav-tabs>.active>a:focus {
        color: #578dc9;
        background-color: #fff;
        border: none;
        border-radius: 20px;
        cursor: default;
    }


    .tab button {
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

    .tabcontent {
        display: none;
        padding: 6px 12px;
        border: none;
        border-top: none;
    }

    .tab button.active {
        background: #476a9c;
        color: #f0f0f0;
        border-radius: 20px;
        padding: 5px 10px;
        margin: auto 10px;
    }
    .tab {
        text-align: center;
        background: #578dc9;
        padding: 10px;
    }



    button.btn.btn-link{
        color:#385989;
        text-align: center;
        font-weight: bold;
        overflow: hidden;
        margin-left: 2%;
        border-radius: 20px;
        margin-bottom: 0px;
        transition: none;
    }
    #Team button.btn.btn-link.collapsed {
        margin-bottom: 70px;
    }

    h5 button.btn.btn-link.collapsed {
        color: #578dc9;
        text-align: center;
        font-weight: bold;
        overflow: hidden;
        text-decoration: none;
        margin-left: 2%;
        border: none;
        background: transparent;
        transition: none;
    }





    .btn.btn-primary, .btn-primary {
        outline: none;
        background: #f8f8f8;
        color: #476a9c;
        border-radius: 20px;
        padding: 5px 10px;
        margin: auto 10px;
    }

    button.btn.btn-link.collapsed::after {
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

    button.btn.btn-link::after {
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




    .uiBox .title{
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

    .uiBox {

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
    .second-col{
        width: 20%;
        color: #4d5466;
        font-family: helvetica;
        text-align: center;
        float: right;
        font-size: 14px;
    }
    .noContent .row:last-child{
        border-bottom:0px;
    }

    @media (max-width: 1024px){
        #uiHowEarnPoint .alignLeft{
            margin:0px;
        }
    }
    @media (max-width: 480px){
        #uiHowEarnPoint .alignLeft{
            margin: 0px;
            width: 100%;
        }

        div#Team .alignLeft {
            float: inherit;
        }
        div#collapseTwo .alignLeft {
            float: inherit;
        }

    }

</style>
