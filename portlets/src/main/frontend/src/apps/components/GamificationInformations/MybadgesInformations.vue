<template>
  <div id="reputation-badge-container" class="p-4 ">
    <div>
      <h3> {{ this.$t(`badges.social`,badges.social) }} </h3>
      <div
        v-for="prog in progs"
        v-if="prog.label=== 'Social'"
        :key="prog"
        md="12"
        class="text-center no-padding col">
        <div class="protected content bg-indigo">
          <div
            v-for="badge in badges"
            v-if="badge.zone === 'Social'"
            :key="badge"
            class="text-center no-reveal badge-wrapper">
            <div v-if=" prog.value > badge.startScore">
              <popper
                triggers="hover focus"
                :target="'reputation'+badge.id"
                :options="{placement: 'top'}">
                <div class="popper" style="max-width: 250px">
                  <div class="level-badges">
                    {{ badge.level }}<br><span>Level</span>
                  </div>
                  <div class="title-badges">{{ badge.title }}</div>
                  <div class="cat-badges">{{ badge.domain }}</div>
                  <div class="desc-badges">{{ badge.description }}</div>
                  <div class="prog-point">
                    <div class="first-number">
                      {{ badge.startScore }}
                      <div v-if="badge.startScore >=1000">
                        {{ badge.startScore/1000 }} K
                      </div>
                      <div v-if="badge.startScore <1000"> {{ badge.startScore }}</div>
                    </div>
                    <hr class="interval">
                    <div
                      v-if="badge.endScore == 0"
                      class="last-number"
                      :class="{'bg-red': bgBadges(badge)}">
                      ∞
                    </div>
                    <div v-else class="last-number">
                      <div v-if="badge.endScore >=1000">{{ badge.endScore/1000 }} K</div>
                      <div v-if="badge.endScore <1000"> {{ badge.endScore }}</div>
                    </div>
                  </div>
                </div>
                <div slot="reference">
                  <div class="box">
                    <img
                      :id="'reputation'+badge.id"
                      :src="`/portal/rest/gamification/reputation/badge/${badge.id}/avatar`"
                      alt="Thumbnail"
                      class="m-1"
                      fluid
                      height="200px"
                      thumbnail
                      width="200px">
                  </div>
                </div>
              </popper>
            </div>
            <div
              v-else-if=" prog.value > badge.startScore || prog.value < badge.endScore ||prog.value < badge.startScore "
              :id="'rep'+badge.id">
              <popper
                triggers="hover focus"
                :options="{placement: 'top',modifiers: { offset: { offset: '0,10px' } }}">
                <div class="popper" style="max-width: 250px">
                  <div class="level-badges">
                    {{ badge.level }}<br><span>Level</span>
                  </div>
                  <div class="title-badges">{{ badge.title }}</div>
                  <div class="cat-badges">{{ badge.domain }}</div>
                  <div class="desc-badges">{{ badge.description }}</div>
                  <div class="prog-pointS">
                    <div class="first-number">
                      <div v-if="badge.startScore >=1000">
                        {{ badge.startScore/1000 }} K
                      </div>
                      <div v-if="badge.startScore <1000"> {{ badge.startScore }}</div>
                    </div>
                    <progress-bar
                      :id="'rep'+badge.id"
                      class="cylinder-progresss"
                      :stroke="defaultOptions.progress.color"
                      :stroke-width="progressWidth"
                      :animation="defaultOptions.progress.animation"
                      :options="defaultOptions"
                      :value=" (prog.value * 100)/((badge.endScore))" />
                    <div
                      v-if="badge.endScore == 0"
                      class="last-number"
                      :class="{'bg-red': bgBadges(badge)}">
                      ∞
                    </div>
                    <div v-else class="last-numberS">
                      <div v-if="badge.endScore >=1000">{{ badge.endScore/1000 }} K</div>
                      <div v-if="badge.endScore <1000"> {{ badge.endScore }}</div>
                    </div>
                  </div>
                </div>
                <div slot="reference">
                  <div class="box">
                    <img
                      :id="'reputation'+badge.id"
                      thumbnail
                      fluid
                      :src="`/portal/rest/gamification/reputation/badge/${badge.id}/avatar`"
                      alt="Thumbnail"
                      class="greytwo"
                      width="200px"
                      height="200px">
                    <progress-bar
                      :id="'rep'+badge.id"
                      class="cylinder-progress"
                      :stroke="defaultOptions.progress.color"
                      :stroke-width="progressWidth"
                      :animation="defaultOptions.progress.animation"
                      :options="defaultOptions"
                      :value=" (prog.value * 100)/((badge.endScore))" />
                  </div>
                </div>
              </popper>
            </div>
          </div>
        </div>
      </div>
    </div>
    <br>
    <hr>
    <div>
      <h3> {{ this.$t(`badges.knowledge`,badges.knowledge) }} </h3>
      <b-col
        v-for="prog in progs"
        v-if="prog.label=== 'Knowledge'"
        :key="prog"
        md="12"
        class="text-center no-padding">
        <div class="protected content bg-indigo">
          <div
            v-for="badge in badges"
            v-if="badge.zone === 'Knowledge' "
            :key="badge"
            class="text-center no-reveal badge-wrapper">
            <div v-if=" prog.value > badge.startScore">
              <popper
                triggers="hover focus"
                :target="'reputation'+badge.id"
                :options="{placement: 'top'}">
                <div class="popper" style="max-width: 250px">
                  <div class="level-badges">
                    {{ badge.level }}<br><span>Level</span>
                  </div>
                  <div class="title-badges">{{ badge.title }}</div>
                  <div class="cat-badges">{{ badge.domain }}</div>
                  <div class="desc-badges">{{ badge.description }}</div>
                  <div class="prog-point">
                    <div class="first-number">
                      {{ badge.startScore }}
                      <div v-if="badge.startScore >=1000">
                        {{ badge.startScore/1000 }} K
                      </div>
                      <div v-if="badge.startScore <1000"> {{ badge.startScore }}</div>
                    </div>
                    <hr class="interval">
                    <div
                      v-if="badge.endScore == 0"
                      class="last-number"
                      :class="{'bg-red': bgBadges(badge)}">
                      ∞
                    </div>
                    <div v-else class="last-number">
                      <div v-if="badge.endScore >=1000">{{ badge.endScore/1000 }} K</div>
                      <div v-if="badge.endScore <1000"> {{ badge.endScore }}</div>
                    </div>
                  </div>
                </div>

                <div slot="reference">
                  <div class="box">
                    <img
                      :id="'reputation'+badge.id"
                      :src="`/portal/rest/gamification/reputation/badge/${badge.id}/avatar`"
                      alt="Thumbnail"
                      class="m-1"
                      fluid
                      height="200px"
                      thumbnail
                      width="200px">
                  </div>
                </div>
              </popper>
            </div>
            <div
              v-else-if=" prog.value > badge.startScore || prog.value < badge.endScore ||prog.value < badge.startScore "
              :id="'rep'+badge.id">
              <popper
                triggers="hover focus"
                :options="{placement: 'top',modifiers: { offset: { offset: '0,10px' } }}">
                <div class="popper" style="max-width: 250px">
                  <div class="level-badges">
                    {{ badge.level }}<br><span>Level</span>
                  </div>
                  <div class="title-badges">{{ badge.title }}</div>
                  <div class="cat-badges">{{ badge.domain }}</div>
                  <div class="desc-badges">{{ badge.description }}</div>

                  <div class="prog-pointS">
                    <div class="first-number">
                      <div v-if="badge.startScore >=1000">
                        {{ badge.startScore/1000 }} K
                      </div>
                      <div v-if="badge.startScore <1000"> {{ badge.startScore }}</div>
                    </div>

                    <progress-bar
                      :id="'rep'+badge.id"
                      class="cylinder-progresss"
                      :stroke="defaultOptions.progress.color"
                      :stroke-width="progressWidth"
                      :animation="defaultOptions.progress.animation"
                      :options="defaultOptions"
                      :value=" (prog.value * 100)/((badge.endScore))" />
                    <div
                      v-if="badge.endScore == 0"
                      class="last-number"
                      :class="{'bg-red': bgBadges(badge)}">
                      ∞
                    </div>

                    <div v-else class="last-numberS">
                      <div v-if="badge.endScore >=1000">{{ badge.endScore/1000 }} K</div>
                      <div v-if="badge.endScore <1000"> {{ badge.endScore }}</div>
                    </div>
                  </div>
                </div>
                <div slot="reference">
                  <div class="box">
                    <img
                      :id="'reputation'+badge.id"
                      thumbnail
                      fluid
                      :src="`/portal/rest/gamification/reputation/badge/${badge.id}/avatar`"
                      alt="Thumbnail"
                      class="greytwo"
                      width="200px"
                      height="200px">
                    <progress-bar
                      :id="'rep'+badge.id"
                      class="cylinder-progress"
                      :stroke="defaultOptions.progress.color"
                      :stroke-width="progressWidth"
                      :animation="defaultOptions.progress.animation"
                      :options="defaultOptions"
                      :value=" (prog.value * 100)/((badge.endScore))" />
                  </div>
                </div>
              </popper>
            </div>
          </div>
        </div>
      </b-col>
    </div>
  </div>
</template>
<script>
    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import {Image} from 'bootstrap-vue/es/components';
    import ProgressBar from 'vuejs-progress-bar'
    import Popper from 'vue-popperjs';
    import 'vue-popperjs/dist/vue-popper.css';
    import axios from 'axios';
    Vue.use(ProgressBar);
    Vue.use(BootstrapVue);
    Vue.use(Image);
    const initialData = () => {
        return {
            badges: [],
            progs: [],
            value: '',
            defaultOptions: Object,
        }
    };
    export default {
        components: {
            'popper': Popper
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
        created() {
            const url = window.location.pathname;
            console.log(url);
            axios.get(`/rest/gamification/reputation/AllofBadges`, { params: { 'url': url } })
                .then(response => {
                    this.badges = response.data;
                });
            axios.get(`/rest/gamification/reputation/stats`, { params: { 'url': url }})
                .then(response => {
                    this.progs = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                });
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
                const result = this.defaultOptions;
                for (const option in options)
                {
                    if (options[option] !== null && typeof(options[option]) === 'object') {
                        for (const subOption in options[option]) {
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
                let usePound = false;
                if (color[0] == "#") {
                    color = color.slice(1);
                    usePound = true;
                }
                const num = parseInt(color,16);
                let r = (num >> 16) + level;
                if (r > 255) {r = 255;}
                else if (r < 0) {r = 0;}
                let b = ((num >> 8) & 0x00FF) + level;
                if (b > 255) {b = 255;}
                else if (b < 0) {b = 0;}
                let g = (num & 0x0000FF) + level;
                if (g > 255) {g = 255;}
                else if (g < 0) {g = 0;}
                return (usePound?"#":"") + (g | (b << 8) | (r << 16)).toString(16);
            }
        }
    }
</script>

<style>
    @media screen and (min-width: 1210px) {
        .badge-wrapper {
            width: 16%;
        }
    }
    @media screen and (max-width: 1209px) and (min-width: 767px){
        .badge-wrapper {
            width: 15.5%;
        }
    }

    @media screen and (max-width: 766px) and (min-width: 400px){
        .badge-wrapper {
            width: 33%;
        }
    }

    @media screen and (max-width: 399px){
        .badge-wrapper {
            width: 100%;
        }
    }


</style>