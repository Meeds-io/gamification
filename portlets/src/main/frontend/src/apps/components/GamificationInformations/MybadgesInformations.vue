<template>

    <b-container fluid class="p-4" id="reputation-badge-container">

        <div>
            <h3> Social Badges </h3>
            <b-col md="12" class="text-center no-padding" >
                <div class="protected content bg-indigo" :key="prog" v-for="prog in progs" v-if="prog.label=== 'Social'" >
                    <div class="container text-center no-reveal" :key="badge" v-for="badge in badges" v-if="badge.zone === 'Social'" >
                        <div class="box" v-if=" prog.value > badge.startScore">
                            <img :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1" fluid height="200px" thumbnail width="200px"/>
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
                                        <div class="last-number" v-else>{{badge.endScore}}
                                        </div>
                                    </div>
                                </b-popover>
                        </div>

                        <div class="box" v-else-if=" prog.value > badge.startScore || prog.value < badge.endScore ||prog.value < badge.startScore " :id="'rep'+badge.id">
                            <img thumbnail fluid :id="'reputation'+badge.id" :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="greytwo" width="200px" height="200px" />


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
                                    <div class="last-number" v-bind:class="{'bg-red': bgBadges(badge)}" v-if="badge.endScore == 0">
                                        ∞
                                    </div>

                                    <div class="last-numberS" v-else>{{badge.endScore}}</div>
                                </div>
                            </b-popover>
                        </div>
                    </div>

                </div>

            </b-col>

        </div><br><hr><div>

        <h3> Knowledge Badges </h3>

        <b-col md="12" class="text-center no-padding" :key="prog" v-for="prog in progs" v-if="prog.label=== 'Knowledge'">
            <div class="protected content bg-indigo" >
                <div class="container text-center no-reveal" :key="badge" v-for="badge in badges" v-if="badge.zone === 'Knowledge' " >
                    <div class="box" v-if=" prog.value > badge.startScore ">
                        <img :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1" fluid height="200px"

                               thumbnail width="200px"/>
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
                                <div class="last-number" v-else>{{badge.endScore}}
                                </div>
                            </div>
                        </b-popover>
                    </div>
                    <div class="box" v-else-if=" prog.value > badge.startScore || prog.value < badge.endScore ||prog.value < badge.startScore " :id="'rep'+badge.id">
                        <img thumbnail fluid :id="'reputation'+badge.id" :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="greytwo" width="200px" height="200px" />
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
                                <div class="last-number" v-bind:class="{'bg-red': bgBadges(badge)}" v-if="badge.endScore == 0">
                                    ∞
                                </div>

                                <div class="last-numberS" v-else>{{badge.endScore}}</div>
                            </div>
                            </b-popover>
                        </div>
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
            axios.get(`/rest/gamification/reputation/stats`, { params: { 'url': url }})
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