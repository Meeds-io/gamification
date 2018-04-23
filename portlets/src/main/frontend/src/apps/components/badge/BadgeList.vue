<template>
    <b-container fluid>
        <b-row>
        </b-row>

        <b-row>
            <b-col sm="12">
                <table class="table table-hover badge-table">
                    <thead>
                        <tr>
                            <!--
                <th class="badge-image-col"></th>
                -->
                            <th class="badge-title-col">Title</th>
                            <th class="badge-desc-col">Description</th>
                            <th class="badge-nedded-score-col">Needed Score</th>
                            <th class="badge-start-validity-date-col">Start validity date</th>
                            <th class="badge-end-validity-date-col">End validity Date</th>
                            <th class="badge-icon-col">Icon</th>
                            <th class="badge-status-col">Status</th>
                            <th class="badge-last-modified-date-col">Last Modified Date</th>
                            <th class="badge-creation-date-col">Creation Date</th>
                            <th class="badge-last-modified-by-col">Last modified by</th>
                            <th class="badge-created-by-col">Created by</th>                            
                            <th class="badge-action-col">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="badge in badges" track-by="id" v-on:click.prevent="onEdit(badge)">
                            <!--
                <td>
                    <img v-if="badge.imageUrl" v-bind:src="badge.imageUrl" alt="badge image" class="badge-image">
                    <img v-else src="../assets/badge_placeholder.svg" alt="Badge image" class="badge-image">
                </td>
            -->
                            <td class="badge-title-col">{{badge.title}}</td>
                            <td class="badge-desc-col">{{badge.description}}</td>
                            <td class="badge-needed-score-col">{{badge.neededScore}}</td>
                            <td class="badge-start-validity-date-col">{{badge.startValidityDate}}</td>
                            <td class="badge-end-validity-date-col">{{badge.endValidityDate}}</td>
                            <td class="badge-icon-col">{{badge.icon}}</td>
                            <td class="badge-status-col">{{badge.isEnabled}}</td>
                            <td class="badge-last-modified-date-col">{{badge.lastModifiedDate}}</td>
                            <td class="badge-created-date-col">{{badge.createdDate}}</td>
                            <td class="badge-last-modified-date-col">{{badge.lastModifiedBy}}</td>
                            <td class="badge-created-date-col">{{badge.createdBy}}</td>
                            <td>
                                <a href="#" v-on:click.prevent.stop="onRemove(badge.id,badge.title)">remove</a>
                            </td>
                        </tr>
                        <tr v-if="!badges.length">
                            <td colspan="5" class="p-y-3 text-xs-center">
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
    export default {
        props: ['badges'],
        data() {
            return {

            }
        },
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