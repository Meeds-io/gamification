
<template>
    <b-container fluid>
        <b-row>
            <b-col sm="12" >
                <!-- <b-col md="6" class="my-1">
                    <b-form-group horizontal label="Filter" class="mb-0">
                      <b-input-group>
                        <b-form-input v-model="filter" placeholder="Type to Search" />
                        <b-input-group-append>
                          <b-btn :disabled="!filter" @click="filter = ''">Clear</b-btn>
                        </b-input-group-append>
                      </b-input-group>
                    </b-form-group>
                  </b-col> -->

                <table striped hover class="uiGrid table table-hover rule-table">
                    <thead>
                        <tr>
               
                            <th class="rule-name-col">Title</th>
                            <th class="rule-desc-col">Description</th>
                            <th class="rule-price-col">score</th>
                            <th class="rule-area-col">Domain</th>
                            <th class="rule-enable-col">Enabled</th>
                            <th class="rule-action-col">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="rule in rules" track-by="id" v-on:click.prevent="onEdit(rule)">
         
                            <td>{{rule.title}}</td>
                            <td class="rule-desc-col">{{rule.description}}</td>
                            <td>{{rule.score}}</td>
                            <td>{{rule.area}}</td>
                            <td>{{rule.enabled}}</td>
                            <td>
                                <a href="#" v-on:click.prevent.stop="onRemove(rule.id,rule.title)" data-placement="bottom" rel="tooltip" class="actionIcon" data-original-title="Supprimer" v-b-tooltip.hover title="Supprimer"><i class="uiIconDelete uiIconLightGray"></i></a>
                            </td>
                        </tr>
                        <tr v-if="!rules.length">
                            <td colspan="5" class="p-y-3 text-xs-center">
                                <strong>You should add some rules!</strong>
                            </td>
                        </tr>
                    </tbody>

                </table>
            </b-col>
            </b-row>
    </b-container>
</template>

<script>
    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    import moment from 'moment'
    Vue.use(BootstrapVue);

    Vue.prototype.moment = moment

    export default {
        props: ['rules'],
        data() {
            return {

            }
        },
        methods: {
            onEdit(rule) {
                this.$emit('edit', rule)
            },
            onRemove(id, title) {
                this.$emit('remove', id, title)
            }
        }
    }
</script>

<style scoped>
    .table{
        position: relative;
        border-radius: 3px;
        background: #fff;

        margin-bottom: 20px;
        width: 96%;
        box-shadow: 0 1px 1px rgba(0,0,0,.1);

        margin: 30px auto 0;
        margin-bottom: 30px;

    }

    .table thead th{font-size: 0.9em;}

    .uiGrid.table thead tr:first-child{
        height: 50px !important;
    }

    .table td, .table th{
        padding: 8px;
        line-height: 1.42857143;
        vertical-align: top;
        text-align:center;
        border: none;
    }
    .table-hover tbody tr:hover{
        cursor: pointer;
    }
    .table-striped>tbody>tr:nth-of-type(odd){
        background-color: #f9f9f9;
    }


</style>