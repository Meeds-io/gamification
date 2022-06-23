<template>    
  <v-data-table
    :headers="realizationsHeaders"
    :items="realizationList"
    :items-per-page=" realizationList && realizationList.length"
    :loading="loading"
    hide-default-footer
    class="mx-6 mt-6 realizationsTable">
    <template slot="item" slot-scope="props">
      <tr v-show="realizationList">
        <td class="wrap align-center px-6">
          {{ getFromDate(props.item.createdDate) }}
        </td>
        <td class="text-truncate align-center">
          {{ itemEarner(props.item) }}
        </td>
        <td class="align-center actionTitle px-0">
          <a
            :href="props.item.url"
            class="text-color"
            :class="!props.item.url && 'defaultCursor'">
            <span :class="{actionDescription: isManual}">
              {{ actionDescription(props.item) }}
            </span>
          </a>
        </td>
        <td class="text-truncate align-center">
          <span> {{ realizationActionType(props.item) }} </span>
        </td>
        <td class="text-truncate align-center">
          {{ domainDescription(props.item) }}
        </td>
        <td class="text-truncate align-center">
          {{ itemScore(props.item) }}
        </td>
        <td class="text-truncate align-center">
          <span> {{ realizationStatus(props.item) }} </span>
        </td>
        <td class="text-truncate actions align-center">
          <v-menu
            offset-y
            :value="selectedRealizations === props.item"
            attach>
            <template
              #activator="{ on }">
              <v-btn
                icon
                small
                v-on="on"
                class="mx-5"
                @click="selectedRealizations = props.item"
                @blur="closeActionsMenu">
                <v-icon size="20px">fa-ellipsis-v</v-icon>
              </v-btn>
            </template>
            <v-list flat class="pt-0 pb-0">
              <template>
                <v-list-item @mousedown="$event.preventDefault()" v-if="props.item.action && props.item.action.type === 'MANUAL'">
                  <v-list-item-title class="options" @click="editRealizations(props.item)">
                    <i class="fas fa-edit px-1"></i>
                    <span class="px-1"> {{ $t('realization.label.edit') }} </span>
                  </v-list-item-title>
                </v-list-item>
                <v-list-item @mousedown="$event.preventDefault()" v-if="props.item.status === 'REJECTED'">
                  <v-list-item-title class="options" @click="updateRealizations(props.item,'ACCEPTED')">
                    <i class="fas fa-check px-1"></i>
                    <span class="px-1"> {{ $t('realization.label.accept') }}</span>
                  </v-list-item-title>
                </v-list-item>
                <v-list-item @mousedown="$event.preventDefault()" v-if=" props.item.status === 'ACCEPTED' || props.item.status === 'EDITED'">
                  <v-list-item-title class="options" @click="updateRealizations(props.item,'REJECTED')">
                    <i class="fas fa-ban px-1"></i>
                    <span class="px-1"> {{ $t('realization.label.reject') }}</span>
                  </v-list-item-title>
                </v-list-item>
              </template>
            </v-list>
          </v-menu>
        </td>
      </tr>
    </template>
  </v-data-table>
</template>
<script>

export default {
  data(){
    return {
      isManual: false
    };
  },
  computed: {
    itemEarner(item){
      return item.earner || '-';
    },
    itemScore(item){
      return item && item.score;
    },
    domainDescription(item){
      return item.action && item.domain && item.domain.description || '-';
    },
    actionDescription(item){
      if (item.action && item.action.type === 'MANUAL'){
        this.isManual = true;
        return item.actionLabel;
      } else if (item.action && item.action.type === 'AUTOMATIC'){
        return this.getActionLabel(item.actionLabel);
      } return '' ;
    },
    realizationsHeaders() {
      return [
        {
          text: this.$t('realization.label.date'),
          align: 'center',
          sortable: false,
          value: 'date',
          class: 'actionHeader px-2',
        },
        {
          text: this.$t('realization.label.grantee'),
          align: 'center',
          sortable: false,
          value: 'grantee',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.actionLabel'),
          align: 'center',
          sortable: false,
          value: 'actionLabel',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.actionType'),
          align: 'center',
          sortable: false,
          value: 'actionType',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.programLabel'),
          align: 'center',
          sortable: false,
          value: 'programLabel',
          class: 'actionHeader px-0'
        },
        {
          text: this.$t('realization.label.points'),
          align: 'center',
          sortable: false,
          value: 'points',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.status'),
          align: 'center',
          sortable: false,
          value: 'status',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.actions'),
          align: 'center',
          sortable: false,
          value: '',
          class: 'actionHeader px-2'
        },
      ];
    },
    realizationList(){
      return this.realizations;
    },
    realizationStatus(item){
      switch (item.status){
      case 'REJECTED' : return '$t(\'realization.label.rejected\')';
      case 'EDITED' : return '$t(\'realization.label.edited\')';
      case 'ACCEPTED' : return '$t(\'realization.label.accepted\')';
      default : return '';
      }
    },
    realizationActionType(item) {
      if (item.action && item.action.type === 'AUTOMATIC'){
        return '$t(\'realization.label.auto\')';
      } else if (item.action && item.action.type === 'MANUAL'){
        return '$t(\'realization.label.manual\')';
      } return '';
    }
  },
  methods: {
    getFromDate(date) {
      date = new Date(date);
      const day = String(date.getDate());
      const month = String(date.getMonth()+1);
      const year = String(date.getFullYear());
      const lang = eXo.env.portal.language;
      const time =   date.toLocaleString(lang, { hour: 'numeric', minute: 'numeric', hour12: true,  timeZone: 'UTC'}).toLowerCase();
      return `${day}/${month}/${year} ${time} ` ;
    },
    getActionLabel(actionLabel){
      if (!this.$t(`exoplatform.gamification.gamificationinformation.rule.description.${actionLabel}`).includes('exoplatform.gamification.gamificationinformation.rule.description')){
        return this.$t(`exoplatform.gamification.gamificationinformation.rule.description.${actionLabel}`) ;
      } else {
        return actionLabel;
      }
    },
    editRealizations(realization){
      this.$refs.editRealizationDrawer.points = realization.score;
      this.$refs.editRealizationDrawer.actionLabel = this.getActionLabel(realization.actionLabel);
      this.$refs.editRealizationDrawer.program = realization.domain;
      this.$refs.editRealizationDrawer.realizationId = realization.id;
      this.$nextTick().then(() => this.$refs.editRealizationDrawer.open());
    },
    updateRealizations(realization,status) {
      this.$realizationsServices.updateRealization(realization.id,status).then((updatedRealization) => {
        this.realizationUpdated(updatedRealization);
      });
    },
    realizationUpdated(updatedRealization){
      const index = this.realizations && this.realizations.findIndex((realization) => { return  realization.id === updatedRealization.id;});
      this.realizations[index] = updatedRealization;
      this.$set(this.realizations,index,updatedRealization);
    }
  }
};
</script>
