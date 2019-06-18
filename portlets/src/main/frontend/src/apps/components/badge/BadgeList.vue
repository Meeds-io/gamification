<template>
    <b-container fluid>
        <b-row>
            <b-col sm="12">

                <table class=" uiGrid table table-hover badge-table">
                    <thead>
                    <tr>
                        <th class="badge-title-col">Title</th>
                        <th class="badge-desc-col">Description</th>
                        <th class="badge-nedded-score-col">Needed Score</th>
                        <th class="badge-domain-col">Domain</th>
                        <th class="badge-icon-col">Icon</th>
                        <th class="badge-status-col">Status</th>
                        <th class="badge-created-by-col">Created by</th>
                        <th class="badge-action-col">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="badge in badges" track-by="id" v-on:click.prevent="onEdit(badge)">
                        <td class="badge-title-col">{{badge.title}}</td>
                        <td class="badge-desc-col">{{badge.description}}</td>
                        <td class="badge-needed-score-col">{{badge.neededScore}}</td>
                        <td class="badge-domain-col">{{badge.domain}}</td>
                        <td class="badge-icon-col"><img thumbnail fluid :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="m-1"  width="40" height="40"/></td>
                        <td class="badge-status-col">{{badge.enabled}}</td>
                        <td class="badge-created-date-col">{{badge.createdBy}}</td>
                        <td class="center actionContainer">
                            <a href="#" v-on:click.prevent.stop="onRemove(badge.id,badge.title)" data-placement="bottom" rel="tooltip" class="actionIcon"
                               data-original-title="Supprimer" v-b-tooltip.hover title="Supprimer">
                                <i class="uiIconDelete uiIconLightGray"></i>
                            </a>
                        </td>
                    </tr>
                    <tr v-if="!badges.length">
                        <td colspan="9" class="p-y-3 text-xs-center">
                            <strong>You should add some badges!</strong>
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
    import moment from 'moment'
    Vue.prototype.moment = moment
    import BootstrapVue from 'bootstrap-vue'
    Vue.use(BootstrapVue);
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    export default {
        props: ['badges'],
        methods: {
            onEdit(badge) {
                this.$emit('edit', badge)
            },
            onRemove(id, title) {
                this.$emit('remove', id, title)
            }
        }
    }
</script>
<style scoped>
    .table {
        position: relative;
        border-radius: 3px;
        background: #fff;
        margin-bottom: 20px;
        width: 96%;
        box-shadow: 0 1px 1px rgba(0,0,0,.1);
        margin: 30px auto 0;
        margin-bottom: 30px;
    }

    .table thead th {
        font-size: 0.9em;
    }

    .table td,
    .table th {
        padding: 8px;
        line-height: 1.42857143;
        vertical-align: top;
        text-align: center;
    }

    .table-hover tbody tr:hover {
        cursor: pointer;
    }

    .table-striped>tbody>tr:nth-of-type(odd) {
        background-color: #f9f9f9;
    }
    .uiGrid.table td, .uiGrid.table th {
        border-left: none;
    }
    .uiGrid.table thead {
        border: 1px solid #e1e8ee;
    }
    .uiGrid.table {
        border: none;
    }
</style>