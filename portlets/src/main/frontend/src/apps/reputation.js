import Vue from 'vue'
import ReputationApp from './components/ReputationApp.vue'

new Vue({
    render: h => h(ReputationApp)
}).$mount('#reputation-portlet > .container');