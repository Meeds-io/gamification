<template class="">
 <table class="uiGrid table table-hover table-striped rule-table" hover striped>
    <thead>
        <tr>
            <th class="rule-name-col"></th>
            <th class="rule-name-col">{{ this.$t('exoplatform.gamification.gamificationinformation.Event') }}</th>
            <th class="rule-desc-col">{{ this.$t('exoplatform.gamification.gamificationinformation.Date') }}</th>
            <th class="rule-price-col"> {{ this.$t('exoplatform.gamification.gamificationinformation.Points') }} <a class="ico-info actionIco" data-v-2e935f06="" href="../intranet/gamification-earn-points" target="_blank" rel="tooltip"
                                                  :title="this.$t('exoplatform.gamification.leaderboard.Howearnpoints') " >
                <i data-v-2e935f06="" class="uiIconInformation"></i></a></th>
            <th class="rule-enable-col">{{ this.$t('exoplatform.gamification.gamificationinformation.Domain') }}</th>
        </tr>
        </thead>
        <tbody>


        <tr :key="user.receiver" v-for="(user, index) in users">



           <td>  <div class="desc-user">
                           <a :href="user.profileUrl"> <avatar :username="user.fullname" :size="35" :src="user.avatarUrl"></avatar></a>
                       </div></td>
            <td :key="rule.id" v-for="rule in rules" v-if=" rule.title === user.actionTitle">
                <a v-bind:href="user.objectId" >

                    {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${rule.title}`,rule.title) }}

                </a> </td>



            <td>{{user.createdDate}}</td>
            <td>{{user.actionScore}}</td>
            <td>{{ $t(`exoplatform.gamification.gamificationinformation.domain.${user.domain}`,user.domain) }}</td>

        </tr>




        </tbody>
     <div id="ActivitiesLoader" v-if="users.length>1" class="btn btn-block" @click="showMore()">
         {{ this.$t('exoplatform.gamification.leaderboard.showMore') }}
     </div>
        
    </table>


</template>

<script>
    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import {Image, Popover} from 'bootstrap-vue/es/components';
    import axios from 'axios';
    import Avatar from 'vue-avatar'
    import TotalPointsFilter from "./TotalPointsFilter";

    Vue.use(BootstrapVue);
    Vue.use(Popover);
    Vue.use(Image);


    const initialData = () => {
        return {
            users: [],
            selected: null,
            show: false,
            loadCapacity: 10,
            title: '',
            rules: [],
            id: null,
            description: '',
            actionTitle:'',
            isFiltered: false,
            selected: null,
            activeBtn: 'btn1',
            domain: 'null',
            show: false,
            selectedPeriod: 'ALL',
        }
    };
    export default {
        data: initialData,
        components: {
            TotalPointsFilter,
            Avatar,
        },
        directives: {
            mouseover: {
                mounted: function () {
                    jQuery('[data-toggle="popover"]').popover({
                        html: true,
                        content: $('#popover')
                    }).on('mouseenter', function () {
                        true;
                    })
                        .on('mouseleave', function () {
                            false;
                        });
                },
            }
        },
        localFiltering() {
            return this.hasProvider ? !!this.noProviderFiltering : true
        },
        watch: {
            domain() {
                this.loadCapacity=10
            }
        },
        methods: {
            filter() {
                let self = this;
                axios.get(`/rest/gamification/leaderboard/filter`, { params: { 'domain': self.domain, 'period': self.selectedPeriod } })
                    .then(response => {
                        this.users = response.data;

                    })
                    .catch(e => {
                        console.warn(e)

                    })

            },
            showMore() {
                var url = window.location.pathname;
                let self = this;
                self.loadCapacity += 10;
                axios.get(`/rest/gamification/gameficationinformationsboard/history/all`, { params: { 'domain': self.domain, 'period': self.selectedPeriod, 'capacity': self.loadCapacity,'url': url} })
                    .then(response => {
                        this.users = response.data;
                    })
                    .catch(e => {
                        console.warn(e)
                    })
            },
            popOpen() {
                jQuery(".popover").popover({ trigger: "hover", html: true, animation: false })
                    .on("mouseenter", function () {
                        true;
                    }).on("mouseleave", function () {
                    false;
                });
            },
            disableByRef() {
                if (this.disabled) {
                    this.$refs.popover.$emit('enable')
                } else {
                    this.$refs.popover.$emit('disable')
                }
            },
            mouseOver() {
                jQuery(this).popover("show");
            },
            isActive(value) {
                return;
                this.active === value
            },
            toggleClass() {
                this.isActive = !this.isActive;
            },
        },
        created() {
            var url = window.location.pathname;
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

</style>
