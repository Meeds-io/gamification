<!-- src/components/ProductList.vue -->
<template>
    <b-container fluid>
        <b-row>
        </b-row>

        <b-row>
            <b-col sm="12">
                <table class="table table-hover rule-table">
                    <thead>
                        <tr>
                            <!--
                <th class="rule-image-col"></th>
                -->
                            <th class="rule-name-col">Title</th>
                            <th class="rule-desc-col">Description</th>
                            <th class="rule-price-col">score</th>
                            <th class="rule-delete-col">Creation Date</th>
                            <th class="rule-delete-col">Last Modified Date</th>
                            <th class="rule-delete-col">Created by</th>
                            <th class="rule-delete-col">Last modified by</th>
                            <th class="rule-delete-col">Enabled</th>
                            <th class="rule-delete-col">Action</th>
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
                            <td>{{ moment(rule.createdDate).format('YYYY-MM-DD') }}</td>
                            <td>{{ moment(rule.lastModifiedDate).format('YYYY-MM-DD') }}</td>
                            <td>{{rule.createdBy}}</td>
                            <td>{{rule.lastModifiedBy}}</td>
                            <td>{{rule.enabled}}</td>
                            <td>
                                <a href="#" v-on:click.prevent.stop="onRemove(rule.id,rule.title)">remove</a>
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
    import moment from 'moment'
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