<template>

    <b-container fluid class="p-4" id="reputation-badge-container">
        <b-row>
            <b-col v-for="badge in badges" :key="badge">

                <b-btn :id="badge.id"  variant="primary">
{{ badge.id }}
                                      </b-btn>
                <b-popover :target="badge.id" container="reputation-badge-container" :placement="top" :badge="badge" title="title" triggers="hover focus"
                    :content="badge.description">
                </b-popover>
            </b-col>

        </b-row>

         <b-row>
               <b-col md="4" class="py-4 text-center"
                         v-for="placement in placements" :key="placement">
                      <b-btn :id="'exPopover1-'+placement" variant="primary">
                        {{ placement }}
                      </b-btn>
                      <b-popover :target="'exPopover1-'+placement"
                                 :placement="placement"
                                 title="Popover!"
                                 triggers="hover focus"
                                 :content="`Placement ${placement}`">
                      </b-popover>
                    </b-col>
            </b-row>

        <!--       
        <h5 class="my-3">Placement</h5>
        <b-row>
            <b-col md="4" class="py-4 text-center" v-for="placement in placements" :key="placement">
                <b-btn :id="'exPopover1-'+placement" variant="primary">
                    {{ placement }}
                </b-btn>
                <b-popover :target="'exPopover1-'+placement" :placement="placement" title="Popover!" triggers="hover focus" :content="`Placement ${placement}`">
                </b-popover>
            </b-col>
        </b-row>
    -->


    </b-container>
</template>
<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import { Popover } from 'bootstrap-vue/es/components';
    import axios from 'axios';
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    Vue.use(BootstrapVue);
    Vue.use(Popover);

    const initialData = () => {
        return {
            badges: [],
            placements: [
                'topright',
                'bottomright',
                'righttop',
                'rightbottom',
            ]

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
            }


        },

        created() {
            axios.get(`/rest/gamification/reputation/badge/all`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    //this.posts = response.data
                    console.log(JSON.stringify(response.data))
                    this.badges = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
        }
    }
</script>

<style scoped>

</style>