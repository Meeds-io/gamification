<template>
    <section >
        <h5 class="mt-0">Reputation</h5>
        <div>

               <div class="points">{{reputation.points}} Points</div>
           <!-- <b-progress :value="reputation.points" :max="reputation.max" show-progress animated></b-progress> -->
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
.points{
    text-align: center;
    color: #333;
    font-family: 'open_sansregular';
    position: relative;
    font-size: 14px;
    background:url('/gamification-portlets/skin/images/ico-points.png') left center no-repeat;
    padding-left: 56px;
    display: -webkit-inline-box;
    padding-top: 13px;
    min-height: 50px;
    position: relative;
    margin-left: 12%;
}
.rep-points{
    padding: 0px 20px;
    clear: both;
    min-height: 50px;
    margin: 0 auto;
}
.rep-points img{
    float: left; 
}


</style>