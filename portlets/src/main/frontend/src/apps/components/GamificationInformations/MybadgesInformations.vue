<template>

    <b-container fluid class="p-4" id="reputation-badge-container">

        <div>
            <h3> Social Badges</h3>

            <b-col md="12" class="text-center no-padding" :key="badge" v-for="badge in badges" v-if="badge.zone === 'Social'"  >

                <div class="protected content bg-indigo" >
                    <div class="container text-center no-reveal">
                        <div class="box">
                            <b-img :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1" fluid height="200px"
                                   thumbnail width="200px"/>

                        </div>
                        <div class="title-badges">{{badge.title}}</div>

                    </div>



                <div class="container text-center no-reveal" :key="badge" v-for="badge in allbadges" v-if="badge.domain === 'Social'" >
                    <div class="box">

                        <div class="wave -one"></div> <div class="wave -two"></div><b-img thumbnail fluid :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail"  class="m-1" width="200px" height="200px" />


                    </div>
                    <div class="title-badges">{{badge.title}}</div>

                </div>

                </div>


            </b-col>

        </div>
        <div>
<hr>
 <b-col md="12" class="text-center no-padding" v-for="badge in badges" :key="badge" v-if="badge.zone === 'Knowledge'">
   <h4>knowledge Badges</h4>
     <div class="protected content bg-indigo">
         <div class="container text-center no-reveal">
             <div class="box">
                 <div class="wave -one"></div>
        <b-img :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1" fluid height="200px"
               thumbnail width="200px"/>

             </div>
         </div>

     </div>
                <div class="title-badges">{{badge.title}}</div>
            <b-popover :content="`${badge.description}`" :placement="'top'" :target="'reputation'+badge.id"
                       triggers="hover focus">
                <div class="level-badges">
                    {{badge.level}}<br><span>Level</span>
                </div>
                <div class="title-badges">{{badge.title}}</div>
                <div class="cat-badges">{{badge.zone}}</div>
                <div class="date-badges">{{badge.createdDate}}</div>
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
    </b-col>


        </div>

    </b-container>


</template>
<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import { Popover } from 'bootstrap-vue/es/components';
    import { Image } from 'bootstrap-vue/es/components';
    import axios from 'axios';

    Vue.use(BootstrapVue);
    Vue.use(Popover);
    Vue.use(Image);

    const initialData = () => {
        return {
            badges: [],
            allbadges: []


        }
    }

    export default {
        data: initialData,
        methods: {
            showBadgeDetail(badgeDTO) {
                axios.get(`/rest/gamification/reputation/update`, badgeDTO)
                    .then(response => {

                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            },
            bgBadges: function (badge) {

                return badge.endScore == 0;

            }


        },


        created() {
            var url = window.location.pathname
            console.log(url)
            axios.get(`/rest/gamification/reputation/badges`, { params: {  } })
                .then(response => {

                    this.badges = response.data;
                });
              axios.get(`/rest/gamification/reputation/try`)
                .then(response => {

                    this.allbadges = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
        }
    }
</script>

<style scoped>
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
    }

    #reputation-badge-container {
        padding-right: 10px !important;
        padding-left: 10px !important;
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
        background: #00F260;  /* fallback for old browsers */
        background: -webkit-linear-gradient(to right, #0575E6, #00F260);  /* Chrome 10-25, Safari 5.1-6 */
        background: linear-gradient(to right, #0575E6, #00F260); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */

    }

    .protected .box {
        width: 90px;
        height: 90px;
        margin: 180px auto 20px;
        border-radius: 100%;
        border: 4px solid white;
        background: white;
        box-shadow: 0 2px 150px rgba(0, 0, 0, 0.2);
        background: linear-gradient(to left top, #06f07d, #18bff6);
        position: relative;
        overflow: hidden;
        transform: translateZ(0); }

    .protected .wave {
        opacity: 0.4;
        position: absolute;
        top: 3%;
        left: 50%;
        background: rgba(15, 16, 35, 0.56);
        width: 200px;
        height: 200px;
        margin-left: -100px;
        margin-top: -155px;
        transform-origin: 50% 48%;
        border-radius: 43%;
        animation: drift 2.5s infinite linear; }

    .protected .wave.-three {
        animation: drift 5s infinite linear; }

    .protected .wave.-two {
        animation: drift 9s infinite linear;
        opacity: 1;
        background: #230f1982; }

    .protected .box:after {
        content: '';
        display: block;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        z-index: 11;
        transform: translate3d(0, 0, 0); }

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


</style>