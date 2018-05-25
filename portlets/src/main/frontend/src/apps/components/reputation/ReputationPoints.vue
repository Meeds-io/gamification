<template>
    <section >
        <h5 class="mt-0">Reputation</h5>
        <div>
            <b-progress :value="reputation.points" :max="reputation.max" show-progress animated></b-progress>
        </div>

    </section>
</template>
<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import axios from 'axios';
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    Vue.use(BootstrapVue);

    const initialData = () => {
        return {
            reputation: {
                points: null,
                max: ''            
            },

        }
    }

    export default {
        data: initialData,
        methods: {            
            

        },
        
        created() {
            axios.get(`/rest/gamification/reputation/point/status`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    //this.posts = response.data
                    console.log(JSON.stringify(response.data))
                    this.reputation = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
        }
    }
</script>

<style scoped>

</style>