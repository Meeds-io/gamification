<template class="">



       <table class="uiGrid table table-hover table-striped rule-table" hover striped>

        <thead>
        <tr>
            <th class="rule-name-col"></th>
            <th class="rule-name-col">Action</th>
            <th class="rule-desc-col">Date</th>
            <th class="rule-price-col"> Points <a class="ico-info actionIco" data-v-2e935f06="" href="gamification-earn-points" target="_blank" rel="tooltip"
               data-original-title="How can I earn points ?" >
                <i data-v-2e935f06="" class="uiIconInformation"></i></a></th>
            <th class="rule-enable-col">Domain</th>
        </tr>
        </thead>
        <tbody>


        <tr :key="user.receiver" v-for="(user, index) in users">



          <td>  <div class="desc-user">
                <a :href="user.profileUrl"><avatar :size="35" :src="user.avatarUrl"></avatar></a>
            </div></td>
            <td :key="rule.id" v-for="rule in rules" v-if=" rule.title === user.actionTitle">
                <a v-bind:href="user.objectId" >{{ rule.description}} </a> </td>
            <td>{{user.createdDate}}</td>
            <td>{{user.actionScore}}</td>
            <td>{{user.domain}}</td>

        </tr>




        </tbody>

           <div id="ActivitiesLoader" v-if="users.length>1" class="btn btn-block">
               <b-link @click.prevent="showMore()" href="#">Load More</b-link>
           </div>
    </table>


</template>

<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import { Popover } from 'bootstrap-vue/es/components';
    import { Image } from 'bootstrap-vue/es/components';
    import axios from 'axios';
    import Avatar from 'vue-avatar'

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
            isFiltered: false


        }
    };

    export default {
        data: initialData,


        components: {
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

            showMore() {
                let self = this;
                self.loadCapacity += 10;

                axios.get(`/rest/gamification/gameficationinformationsboard/history/all`, { params: { 'capacity': self.loadCapacity} })
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
                return
                this.active === value
            },
            toggleClass() {
                this.isActive = !this.isActive;
            },


        },

        created() {
            axios.get(`/rest/gamification/gameficationinformationsboard/history/all`)
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
    .user-GamificationInformations-portlet .uiIconViewByChart {
        color: #4d5466 !important;
        font-size: 18px;
        top: 9px;
        padding-right: 10px;
        opacity: 0.4;
    }
    .list-group-item:hover .uiIconViewByChart {
        opacity: 1;
    }
    .desc-user a {
        color: #4d5466 !important;
    }
    .desc-user a:hover,
    .desc-user a:focus {
        color: #578dc9 !important;
    }
    .current-rank .desc-user {
        padding-left: 10px;
        width: 50%;
    }
    .user-GamificationInformations-portlet .img-thumbnail {
        width: 40px;
        padding: 0;
        height: 40px;
        background-color: #fff;
        border: 1px solid #dee2e6;
        border-radius: .25rem;
    }
    h5 {
        text-align: center;
    }
    select {
        height: calc(2.25rem + 2px);
        font-size: 14px;
        transition: background-color .15s ease-in-out, border-color .15s ease-in-out, box-shadow .15s ease-in-out;
    }
    .desc-user a:hover {
        color: #578dc9;
    }
    .list-lead .list-li::after {
        content: "";
        position: absolute;
        bottom: 0;
        width: 100%;
        height: 1px;
        background: #f4f5f5;
        left: 0;
        display: block;
    }
    .list-lead .list-li:last-child::after {
        display: none;
    }
    .show>.btn-secondary.dropdown-toggle:focus {
        box-shadow: none;
    }
    .uiGrid.table.table-hover.table-striped.rule-table {
        margin: 2%;
        max-width: 94%;
    }
    .show>.btn-secondary.dropdown-toggle {
        background-color: #578dc9;
        border: solid 1px #578dc9;
        color: #fff;
    }
    .custom-select {
        font-size: 14px;
    }
    .avatarCircle {
        width: 50px !important;
        height: 50px !important;
    }
    .actionIco {
        border: 1px solid transparent;
    }
    .actionIco:hover {
        background: none;
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffffff', endColorstr='#fff0f0f0', GradientType=0);
        border: 1px solid #e1e8ee;
        -webkit-border-radius: 3px;
        -moz-border-radius: 3px;
        border-radius: 3px;
        -webkit-box-shadow: 0 1px 2px 0 rgba(255, 255, 255, 0);
        -moz-box-shadow: 0 1px 2px 0 rgba(255, 255, 255, 0);
        box-shadow: 0 1px 2px 0 rgba(255, 255, 255, 0);
        color: #333;
    }
    .btn-block {
        display: block;
        width: 97%;
        padding-left: 0;
        padding-right: 0;
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
    }
    div#ActivitiesLoader {
        display: table-caption;
        caption-side: bottom;
    }
    a.ico-info.actionIco {
        position: relative;
        margin-top: -10px;
        margin-left: -5%;
        padding: 5px 0px;
        display: block;
        margin-right: 5%;
        top: 5px;
        float: right;
    }
    i.uiIconInformation {
        color: #999999;


    }
    .actionIco[data-v-2e935f06] {
        border: 1px solid transparent;
    }
    .uiGrid.table.table-hover.table-striped.rule-table {
        margin-left: 30px;
    }
    .uiGrid.table tr th {
        font-weight: bold;
        padding: 10px 10px;
        text-align: center;
    }
    .vue-avatar--wrapper {
        margin: 3px 3px;
        width: 35px !important;
        margin: auto;
    }
    .uiGrid.table tr td {
        padding: 5px 15px;
        text-align: center;
    }
</style>