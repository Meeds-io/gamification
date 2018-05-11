<!-- src/components/ProductList.vue -->
<template>
    <b-container fluid>
        <b-row>
            <b-col sm="12" >


                <table striped hover class="table table-hover table-striped rule-table">
                    <thead>
                        <tr>
                            <!--
                <th class="rule-image-col"></th>
                -->
                            <th class="rule-name-col">Title</th>
                            <th class="rule-desc-col">Description</th>
                            <th class="rule-price-col">score</th>
                            <th class="rule-area-col">Area</th>
                            <th class="rule-creation-date-col">Creation Date</th>
                            <th class="rule-modified-date-col">Last Modified Date</th>
                            <th class="rule-created-by-col">Created by</th>
                            <th class="rule-modified-by-col">Last modified by</th>
                            <th class="rule-enable-col">Enabled</th>
                            <th class="rule-action-col">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="rule in rules" track-by="id" v-on:click.prevent="onEdit(rule)">
                            <!--
                <td>
                    <img v-if="rule.imageUrl" v-bind:src="rule.imageUrl" alt="Rule image" class="rule-image">
                    <img v-else src="../assets/rule_placeholder.svg" alt="Rule image" class="rule-image">
                </td>
            -->
                            <td>{{rule.title}}</td>
                            <td class="rule-desc-col">{{rule.description}}</td>
                            <td>{{rule.score}}</td>
                            <td>{{rule.area}}</td>
                            <td>{{ moment(rule.createdDate).format('YYYY-MM-DD') }}</td>
                            <td>{{ moment(rule.lastModifiedDate).format('YYYY-MM-DD') }}</td>
                            <td>{{rule.createdBy}}</td>
                            <td>{{rule.lastModifiedBy}}</td>
                            <td>{{rule.enabled}}</td>
                            <td>
                                <a href="#" v-on:click.prevent.stop="onRemove(rule.id,rule.title)" data-placement="bottom" rel="tooltip" class="actionIcon" data-original-title="Supprimer"><i class="uiIconDelete uiIconLightGray"></i></a>
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
        background: #ffffff;
        border-top: 3px solid #d2d6de;
        margin-bottom: 20px;
        width: 100%;
        box-shadow: 0 1px 1px rgba(0,0,0,0.1);
        border-top-color: #3c8dbc;
        margin-top:3%;

    }

    .table thead th{font-size: 0.9em;}

    .table td, .table th{
        padding: 8px;
        line-height: 1.42857143;
        vertical-align: top;
        text-align:center;
    }
    .table-hover tbody tr:hover{
        cursor: pointer;
    }
    .table-striped>tbody>tr:nth-of-type(odd){
        background-color: #f9f9f9;
    }


</style>