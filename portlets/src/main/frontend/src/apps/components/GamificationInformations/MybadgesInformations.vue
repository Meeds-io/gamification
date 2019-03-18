<template>

    <b-container fluid class="p-4" id="reputation-badge-container">

        <div>
            <h3> Social Badges </h3>

            <b-col md="12" class="text-center no-padding" >


                <div class="protected content bg-indigo" :key="prog" v-for="prog in progs" v-if="prog.label=== 'Social'" >

                    <div class="container text-center no-reveal" :key="badge" v-for="badge in badges" v-if="badge.zone === 'Social'" >


                        <div class="box" v-if=" prog.value > badge.startScore">



                            <b-img :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1" fluid height="200px"

                                   thumbnail width="200px"/>

                        </div>

                        <div class="box" v-else-if=" prog.value > badge.startScore || prog.value < badge.endScore " :id="'rep'+badge.id">
                            <b-img thumbnail fluid :id="'reputation'+badge.id" :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="greytwo" width="200px" height="200px" />


                            <progress-bar
                                    :id="'rep'+badge.id"
                                    class="cylinder-progress"
                                    :stroke="defaultOptions.progress.color"
                                    :stroke-width="progressWidth"
                                    :animation="defaultOptions.progress.animation"
                                    :options="defaultOptions"
                                    :value=" (prog.value * 100)/((badge.endScore))"

                            />



                            <div class="title-badge">{{badge.title}}</div>
                            <b-popover :content="`${badge.description}`" :placement="'top'" :target="'rep'+badge.id"
                                       triggers="hover focus">
                                <div class="level-badges">{{badge.level}}<br><span>Level</span>
                                </div>
                                <div class="title-badges">{{badge.title}}</div>
                                <div class="cat-badges">{{badge.domain}}</div>
                                <div class="desc-badges">{{badge.description}}</div>

                                <div class="prog-pointS">
                                    <div class="first-number">{{badge.startScore}}</div>

                                    <progress-bar
                                            :id="'rep'+badge.id"
                                            class="cylinder-progresss"
                                            :stroke="defaultOptions.progress.color"
                                            :stroke-width="progressWidth"
                                            :animation="defaultOptions.progress.animation"
                                            :options="defaultOptions"
                                            :value=" (prog.value * 100)/((badge.endScore))"

                                    />
                                    <div class="last-numberS" v-bind:class="{'bg-red': bgBadges(badge)}" v-if="badge.endScore == 0">

                                    </div>

                                    <div class="last-numberS" v-else>{{badge.endScore}}</div>
                                </div>
                            </b-popover>

                        </div>


                        <div class="box" v-else-if="prog.value < badge.startScore" >



                            <b-img thumbnail fluid :id="'reputation'+badge.id" :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="grey" width="200px" height="200px" />

                        </div>

                        <div class="title-badge">{{badge.title}}</div>
                        <b-popover :content="`${badge.description}`" :placement="'top'" :target="'reputation'+badge.id"
                                   triggers="hover focus">
                            <div class="level-badges">{{badge.level}}<br><span>Level</span>
                            </div>
                            <div class="title-badges">{{badge.title}}</div>
                            <div class="cat-badges">{{badge.domain}}</div>
                            <div class="desc-badges">{{badge.description}}</div>
                            <div class="prog-point">
                                <div class="first-number">{{badge.startScore}}</div>
                                <hr class="interval">
                                <div class="last-number" v-bind:class="{'bg-red': bgBadges(badge)}" v-if="badge.endScore == 0">
                                    ∞
                                </div>
                                <div class="last-number" v-else>{{badge.endScore}}</div>
                            </div>
                        </b-popover>
                    </div>





                </div>

            </b-col>

        </div>
        <br>
        <hr>
        <div>

            <h3> Knowledge Badges </h3>

            <b-col md="12" class="text-center no-padding" :key="prog" v-for="prog in progs" v-if="prog.label=== 'Knowledge'">


                <div class="protected content bg-indigo" >
                    <div class="container text-center no-reveal" :key="badge" v-for="badge in badges" v-if="badge.zone === 'Knowledge' " >

                        <div class="box" v-if=" prog.value > badge.startScore ">

                            <b-img :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1" fluid height="200px"

                                   thumbnail width="200px"/>

                        </div>

                        <div class="boxpercent" v-else-if="prog.value < badge.endScore " :id="'rep'+badge.id">

                            <b-img thumbnail fluid :id="'reputation'+badge.id" :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="greytwo" width="200px" height="200px" />


                            <progress-bar
                                    :id="'rep'+badge.id"
                                    class="cylinder-progress"
                                    :stroke="defaultOptions.progress.color"
                                    :stroke-width="progressWidth"
                                    :animation="defaultOptions.progress.animation"
                                    :options="defaultOptions"
                                    :value=" (prog.value * 100)/((badge.endScore))"

                            />
                            <div class="title-badge">{{badge.title}}</div>
                            <b-popover :content="`${badge.description}`" :placement="'top'" :target="'rep'+badge.id"
                                       triggers="hover focus">
                                <div class="level-badges">{{badge.level}}<br><span>Level</span>
                                </div>
                                <div class="title-badges">{{badge.title}}</div>
                                <div class="cat-badges">{{badge.domain}}</div>
                                <div class="desc-badges">{{badge.description}}</div>

                                <div class="prog-pointS">
                                    <div class="first-number">{{badge.startScore}}</div>


                                    <progress-bar
                                            :id="'rep'+badge.id"
                                            class="cylinder-progress"
                                            :stroke="defaultOptions.progress.color"
                                            :stroke-width="progressWidth"
                                            :animation="defaultOptions.progress.animation"
                                            :options="defaultOptions"
                                            :value=" (prog.value * 100)/((badge.endScore))"

                                    />


                                    <div class="last-numberS" v-bind:class="{'bg-red': bgBadges(badge)}" v-if="badge.endScore == 0">
                                        {{badge.startScore}}
                                    </div>
                                    <div class="last-numberS" v-else>{{badge.endScore}}</div>
                                </div>
                            </b-popover>

                        </div>




                        <div class="box" v-else >



                            <b-img thumbnail fluid :id="'reputation'+badge.id" :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="grey" width="200px" height="200px" />

                        </div>




                        <div class="title-badge">{{badge.title}}</div>
                        <b-popover :content="`${badge.description}`" :placement="'top'" :target="'reputation'+badge.id"
                                   triggers="hover focus">
                            <div class="level-badges">{{badge.level}}<br><span>Level</span>
                            </div>
                            <div class="title-badges">{{badge.title}}</div>
                            <div class="cat-badges">{{badge.domain}}</div>
                            <div class="desc-badges">{{badge.description}}</div>
                            <div class="prog-point">
                                <div class="first-number">{{badge.startScore}}</div>
                                <hr class="interval">
                                <div class="last-number" v-bind:class="{'bg-red': bgBadges(badge)}" v-if="badge.endScore == 0">
                                    {{badge.startScore}}
                                </div>
                                <div class="last-number" v-else>{{badge.startScore}}
                                </div>
                            </div>
                        </b-popover>

                    </div>




                </div>



            </b-col>

        </div>




    </b-container>


</template>
<script>
    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import { Popover } from 'bootstrap-vue/es/components';
    import { Image } from 'bootstrap-vue/es/components';
    import ProgressBar from 'vuejs-progress-bar'
    Vue.use(ProgressBar)
    import axios from 'axios';
    Vue.use(BootstrapVue);
    Vue.use(Popover);
    Vue.use(Image);
    const initialData = () => {
        return {
            badges: [],
            progs: [],
            value: '',
            defaultOptions: Object,




        }
    }
    export default {
        data: initialData,

        computed: {
            cylinder () {
                return this.defaultOptions.layout.type === 'cylinder'
            },
            line () {
                return this.defaultOptions.layout.type === 'line'
            },
            width () {
                return this.defaultOptions.layout.width
            },
            height () {
                return this.defaultOptions.layout.height
            },

            striped: true,
            animate: true

        },
        methods: {

            showBadgeDetail(badgeDTO) {
                axios.get(`/rest/gamification/update`, badgeDTO)
                    .then(response => {
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            },

            bgBadges: function (badge) {
                return badge.endScore == 0 ;
            },

            mergeDefaultOptionsWithProp: function (options) {
                var result = this.defaultOptions
                for (var option in options)
                {
                    if (options[option] !== null && typeof(options[option]) === 'object') {
                        for (var subOption in options[option]) {
                            if (options[option][subOption] !== undefined && options[option][subOption] !== null) {
                                result[option][subOption] = options[option][subOption]
                            }
                        }
                    } else {
                        result[option] = options[option]
                    }
                }
            },

            LightenColor: function (color, level) {
                var usePound = false;
                if (color[0] == "#") {
                    color = color.slice(1);
                    usePound = true;
                }

                var num = parseInt(color,16);
                var r = (num >> 16) + level;

                if (r > 255) r = 255;
                else if (r < 0) r = 0;

                var b = ((num >> 8) & 0x00FF) + level;

                if (b > 255) b = 255;
                else if (b < 0) b = 0;

                var g = (num & 0x0000FF) + level;

                if (g > 255) g = 255;
                else if (g < 0) g = 0;

                return (usePound?"#":"") + (g | (b << 8) | (r << 16)).toString(16);

            }
        },
        props: {
            options: {
                type: Object,
                required: false
            },
            value: {
                type: Number,
                required: false,
                default: 0
            }
        },
        created() {


            var url = window.location.pathname
            console.log(url)
            axios.get(`/rest/gamification/reputation/AllofBadges`, { params: { 'url': url } })
                .then(response => {
                    this.badges = response.data;

                })

            axios.get(`/rest/gamification/reputation/stats`)
                .then(response => {
                    this.progs = response.data;
                })

                .catch(e => {
                    this.errors.push(e)
                })



            this.defaultOptions = {
                text: {
                    color: '#FFFFFF',
                    shadowEnable: true,
                    shadowColor: '#000000',
                    fontSize: 14,
                    fontFamily: 'Helvetica',
                    dynamicPosition: false,
                    hideText: false
                },
                progress: {
                    color: 'rgba(0,255,0,0.2)',
                    filter:'contrast(19%)',
                    animation: 'drift 2s infinite linear',
                    background: 'm-1 img-thumbnail img-fluid',
                    opacity: 0.4,
                },
                layout: {
                    height: 90,
                    width: 90,
                    verticalTextAlign: 61,
                    horizontalTextAlign: 43,
                    zeroOffset: 0,
                    strokeWidth: 100,
                    progressPadding: 0,
                    type: 'line'
                }}





        }
    }
</script>

<style>
    @font-face {
        font-family: 'open_sanssemibold';
        src: url('/gamification-portlets/skin/fonts/opensans-semibold-webfont.woff2') format('woff2'),
        url('/gamification-portlets/skin/fonts/opensans-semibold-webfont.woff') format('woff');
        font-weight: normal;
        font-style: normal;
    }
    @font-face {
        font-family: 'open_sansbold';
        src: url('/gamification-portlets/skin/fonts/opensans-bold-webfont.woff2') format('woff2'),
        url('/gamification-portlets/skin/fonts/opensans-bold-webfont.woff') format('woff');
        font-weight: normal;
        font-style: normal;
    }
    @font-face {
        font-family: 'open_sansregular';
        src: url('/gamification-portlets/skin/fonts/opensans-regular-webfont.woff2') format('woff2'),
        url('/gamification-portlets/skin/fonts/opensans-regular-webfont.woff') format('woff');
        font-weight: normal;
        font-style: normal;
    }
    #user-reputation-portlet .img-thumbnail {
        border-radius: 50%;
        height: 60px;
        text-indent: -99999px;
    }

    m-1 img-thumbnail img-fluid:hover {
        background-position: 0 0;
    }

    #reputation-badge-container {
        padding-right: 10px !important;
        padding-left: 10px !important;
        text-align: center;
    }
    options{

        color: dimgray;
        shadowEnable: true;
        shadowColor: '#000000';
        fontSize: 14;
        fontFamily: 'Helvetica';
        dynamicPosition: false;
        hideText: false;
    }
    .grey{
        filter: grayscale(100%) contrast(19%);
        webkit-filter: grayscale(100%) contrast(19%);
    }
    .greytwo{
        filter: contrast(19%);
        webkit-filter:contrast(19%);

    }
    .title-badges {
        color: #333333;
        font-family: 'open_sanssemibold';
        font-size: 14px;
        font-weight: bold;
        padding: 5px 0px;
    }
    .cat-badges {
        font-family: 'open_sansregular';
        color: #333333;
        font-size: 11px;
    }
    .date-badges {
        font-family: 'open_sansregular';
        color: #333333;
        font-size: 11px;
        padding-bottom: 5px;
    }
    .desc-badges {
        padding: 0px 15px;
        font-family: 'open_sansregular';
        color: #333333;
        padding-bottom: 12px;
    }
    .level-badges {
        background: #4f6998;
        padding: 14px 14px 6px;
        font-size: 34px;
        color: #fff;
        font-weight: 700;
        display: inline-block;
        line-height: 10px;
        position: relative;
        border-radius: 50%;
        font-family: 'open_sansbold';
    }
    .level-badges span {
        font-size: 9px;
        font-weight: normal;
        font-family: 'open_sansregular';
    }
    .prog-point {
        background: #f0f8fe;
        min-height: 46px;
        padding: 5px 5px 2px 5px;
        border-bottom-left-radius: 7px;
        border-bottom-right-radius: 7px;
        top: 2px;
        position: relative;
    }
    .first-number {
        padding-top: 4px;
        border-radius: 50%;
        background: #60a352;
        color: #fff;
        font-family: 'open_sansregular';
        font-size: 12px;
        float: left;
        position: relative;
        z-index: 10;
        top: 7px;
        width: 30px;
        height: 30px;
        padding: 1px;
        line-height: 27px;
        text-align: center;
    }
    .last-number {
        font-size: 14px;
        float: left;
        background: #60a352;
        color: #fff;
        font-family: 'open_sansregular';
        border-radius: 50%;
        position: relative;
        z-index: 10;
        border: 3px solid #c7e5c8;
        float: right;
        width: 40px;
        height: 40px;
        top: -2px;
        line-height: 35px;
        text-align: center;
        position: relative;
        padding: 1px;
    }
    .current-number {
        font-size: 14px;
        float: left;
        background: #60a352;
        color: #fff;
        font-family: 'open_sansregular';
        border-radius: 50%;
        position: relative;
        z-index: 10;
        border: 3px solid #c7e5c8;
        float: right;
        width: 40px;
        height: 40px;
        top: -2px;
        line-height: 35px;
        text-align: center;
        position: relative;
        padding: 1px;
    }
    .bg-red {
        background: #fd7e14;
        border: none;
        width: 35px;
        height: 35px;
        font-size: 20px;
        top: 1px;
    }
    .interval {
        height: 2px;
        background: #60a352;
        float: left;
        position: absolute;
        bottom: 4px;
        width: 91%;
        left: 7px;
    }
    .intervalgrey {
        height: 2px;
        float: left;
        position: absolute;
        bottom: 4px;
        width: 91%;
        left: 7px;
    }
    .intervalperc {
        height: 2px;

        float: left;
        position: absolute;
        bottom: 4px;
        width: 91%;
        left: 7px;
    }
    img#reputation {
        padding: 10px;
    }
    @media (min-width: 768px) {
        .col-md-4 {
            -ms-flex: 0 0 33.333333%;
            flex: 0 0 33.333333%;
            width: 28%;
            float: left;
        }
    }
    /*new*/
    body
    {
        margin:0;
        padding:0;
    }

    .protected .box {
        width: 90px;
        height: 90px;
        margin: 60px auto 20px;
        border-radius: 100%;
        border: 4px solid white;
        background: white;
        box-shadow: 0 2px 150px rgba(0, 0, 0, 0.2);
        position: relative;
        overflow: hidden;
        transform: translateZ(0); }
    .protected .boxpercent {
        width: 90px;
        height: 90px;
        margin: 60px auto 20px;
        border-radius: 100%;
        border: 4px solid white;
        background: white;
        box-shadow: 0 2px 150px rgba(0, 0, 0, 0.2);
        position: relative;
        overflow: hidden;
        transform: translateZ(0);
    }


    line{
        margin-top: -2px;
    }




    .protected .box:after {
        content: '';
        display: block;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        z-index: 11;
        transform: translate3d(0, 0, 0);
    }
    .protected .boxpercent:after {
        content: '';
        display: block;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        z-index: 11;
        transform: translate3d(0, 0, 0);
    }
    .protected .title {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        z-index: 1;
        line-height: 300px;
        text-align: center;
        transform: translate3d(0, 0, 0);
        color: white;
        text-transform: uppercase;
        font-family: 'Playfair Display', serif;
        letter-spacing: 0.4em;
        font-size: 24px;
        text-shadow: 0 1px 0 rgba(0, 0, 0, 0.1);
        text-indent: 0.3em; }
    @keyframes drift {
        from {
            transform: rotate(0deg); }
        from {
            transform: rotate(360deg); }
    }

    .protected.content.bg-indigo {
        display: flex;
    }
    @media (max-width: 767px) {
        .protected.content.bg-indigo {
            display: flex;
            flex-wrap: wrap;
        }
    }



    .progress-bar {
        display: inline-block;
        align-content: stretch;

        height: 85px !important;
        width: 98px !important;
        transform: rotate(-90deg);
        margin-top: -99px;



    }
    .progress{
        animation: drift 2.5s infinite linear;
    }

    .progress-container {
        stroke-width: 2px;}
    .top {
        z-index: 2;
    }

    .progress-content {
        stroke-width: 2px;

    }
    .top {
        z-index:1;
    }


    #wave {
        position: relative;
        height: 70px;
        width: 600px;
        background: #e0efe3;
    }

    line:nth-child(1) {
        display:none;

    }
    text:nth-child(3) {
        visibility: hidden;
    }



    line:nth-child(2):after {
        content: "";
        display: block;
        position: absolute;
        border-radius: 100% 50%;
        width: 90px;
        height: 90px;
        background-color: rgba(0, 255, 0, 0.3);
        left: 0;
        top: 27px;
        stroke: rgba(0, 255, 0, 0.3) !important;


    }
    line:nth-child(1):after {

        stroke: rgba(0, 255, 0, 0.3) !important;


    }
    svg#line-progress {
        margin-top: 14px;
        transform: translateY(-20px);
    }
    svg#line-progresss {

        transform:rotate(90deg);
    }
    .popover .prog-pointS .progress-bar {
        transform: none;
        height: 10px !important;
        width: 197px !important;
        margin-top: 15px !important;

    }

    .popover .prog-pointS svg#line-progress {
        height: 10px !important;
        width: 197px !important;

    }

    .last-numberS {
        position: absolute !important;
        right: 0;
        bottom: 0;


        font-size: 14px;

        background: #60a352;
        color: #fff;
        font-family: 'open_sansregular';
        border-radius: 50%;
        z-index: 10;
        border: 3px solid #c7e5c8;

        width: 40px;
        height: 40px;
        line-height: 35px;
        text-align: center;
        padding: 1px;

    }
    .popover svg#line-progress {
        margin-top: 14px;
        transform: translateY(-12px);
    }
    .prog-pointS {
        background: #f0f8fe;
        min-height: 46px;
        padding: 5px 5px 2px 5px;
        border-bottom-left-radius: 7px;
        border-bottom-right-radius: 7px;
        top: 2px;
        position: relative;
    }
    .protected .box:hover{
        zoom:120%;

    }
    .protected .boxpercent:hover{
        zoom:120%;

    }

    progress:hover {
        filter:unset;
    }
    greytwo:hover{
        border-radius: 50%;
        height: 60px;
        text-indent: -99999px;


    }



</style>