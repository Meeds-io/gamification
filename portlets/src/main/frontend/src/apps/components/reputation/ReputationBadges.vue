<template>

    <b-container fluid class="p-4" id="reputation-badge-container">

        <div>
            <b-col md="4" class="text-center no-padding" v-for="badge in badges" :key="badge">
                <b-img thumbnail fluid :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1" width="70" height="70" />

                <b-popover :target="'reputation'+badge.id" :placement="'top'" triggers="hover focus" :content="`${badge.description}`">
                    <div class="level-badges">
                        {{badge.level}}<br><span>Level</span>
                    </div>
                    <div class="title-badges">{{badge.title}}</div>
                    <div class="cat-badges">{{badge.zone}} </div>
                    <div class="date-badges">{{badge.createdDate}}</div>
                    <div class="desc-badges">{{badge.description}}</div>
                    <div class="prog-point">
                        <div class="first-number">{{badge.startScore}}</div>
                        <hr class="interval">
                        <div class="last-number" v-if="badge.endScore == 0" v-bind:class="{'bg-red': bgBadges(badge)}"> ∞
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
            axios.get(`/rest/gamification/reputation/badges`, { params: { 'url': url  } })
                .then(response => {

                    this.badges = response.data;
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

    @media (min-width: 768px) {

        .col-md-4 {
            -ms-flex: 0 0 33.333333%;
            flex: 0 0 33.333333%;
            width: 28%;
            float: left;
        }
    }
</style>