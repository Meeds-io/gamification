<template>
  <v-app
    class="Realizations border-box-sizing">
    <v-toolbar
      color="transparent"
      flat
      class="pa-4 mb-4">
      <div class="border-box-sizing clickable">
        <v-btn class="btn btn-primary ">
          <span class="ms-2 d-none d-lg-inline">
            {{ $t("realisation.label.export") }}
          </span>
        </v-btn>
      </div>
      <div class="selected-period-menu mt-6 mx-3">
        <select-period v-model="selectedPeriod" class="mx-2" />
      </div>
    </v-toolbar>
    <v-data-table
      :headers="realizationsHeaders"
      :items="realizations"
      :loading="loadingRealizations"
      class="mx-6 mt-6 realizationsTable">
      <template slot="item" slot-scope="props">
        <tr v-show="realizations">
          <td class="wrap align-center px-6">
            {{ getFromDate(props.item.createdDate) }}
          </td>
          <td class="text-truncate align-center wrap">
            {{ props.item.creator && props.item.creator.remoteId || props.item.receiver && props.item.receiver.remoteId || '-' }}
          </td>
          <td class="align-center actionTitle">
            {{ props.item.action && props.item.action.event || props.item.action.title }}
          </td>
          <td class="align-center actionTitle">
            <span v-if="props.item.action && props.item.action.type === 'AUTOMATIC'"> {{ $t('realisation.label.auto') }} </span>

            {{ props.item.actionLabel }}
          </td>
          <td class="text-truncate align-center">
            <span v-if="props.item.action && props.item.action.type === 'AUTOMATIC'"> {{ $t('realisation.label.auto') }} </span>
            <span v-if="props.item.action && props.item.action.type === 'MANUAL'"> {{ $t('realisation.label.manual') }} </span>
          </td>
          <td class="text-truncate align-center">
            {{ props.item.action && props.item.action.domainDTO && props.item.action.domainDTO.title }}
          </td>
          <td class="text-truncate align-center">
            {{ props.item.action && props.item.action.domainDTO && props.item.action.domainDTO.description }}
          </td>
          <td class="text-truncate align-center">
            {{ props.item.action && props.item.action.score }}
          </td>
          <td class="text-truncate align-center">
            <span v-if="props.item.status === 'REJECTED'"> {{ $t('realisation.label.rejected') }} </span>
            <span v-else-if="props.item.status === 'EDITED'"> {{ $t('realisation.label.edited') }} </span>
            <span v-else-if="props.item.status === 'NORMAL'"> {{ $t('realisation.label.normal') }} </span>
          </td>
          <td class="text-truncate align-center">
            {{ props.item.earner && props.item.earner.remoteId || '-' }}
          </td>
          <td class="text-truncate align-center">
            {{ props.item.space || '-' }}
          </td>
          <td class="text-truncate">
            <v-menu
              offset-y
              :value="selectedRealizations === props.item"
              attach>
              <template
                v-slot:activator="{ on }">
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
                  <v-list-item @mousedown="$event.preventDefault()">
                    <v-list-item-title class="options">{{ $t('realisation.label.edit') }}</v-list-item-title>
                  </v-list-item>
                  <v-divider />
                  <v-list-item @mousedown="$event.preventDefault()" v-if="props.item.status === 'REJECTED'">
                    <v-list-item-title class="options" @click="acceptRealizations(props.item)">{{ $t('realisation.label.accept') }}</v-list-item-title>
                  </v-list-item>
                  <v-divider />
                  <v-list-item @mousedown="$event.preventDefault()" v-if=" props.item.status === 'NORMAL' || props.item.status === 'EDITED'">
                    <v-list-item-title class="options" @click="rejectRealizations(props.item)">{{ $t('realisation.label.reject') }}</v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </v-menu>
          </td>
        </tr>
      </template>
    </v-data-table>
  </v-app>
</template>

<script>

export default {
  data: () => ({
    realizations: [],
    selectedRealizations: {},
    loadingRealizations: false,
    showMenu: false,
    dateMenu: false,
    toDate: new Date().toISOString() ,
    fromDate: null ,
    alert: false,
    type: '',
    message: '',
    selectedPeriod: null,
  }),
  computed: {
    realizationsHeaders() {
      return [
        {
          text: this.$t('realisation.label.date'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.creator'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'

        },
        {
          text: this.$t('realisation.label.actionId'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.actionLabel'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.actionType'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.program'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.programLabel'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-0'
        },
        {
          text: this.$t('realisation.label.points'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.status'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.grantee'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.space'),
          align: 'center',
          sortable: true,
          value: '',
          class: 'actionHeader px-2'
        },
        {
          text: this.$t('realisation.label.actions'),
          align: 'center',
          sortable: false,
          value: '',
          class: 'actionHeader px-2'
        },
      ];
    }
  },
  watch: {
    selectedPeriod(newValue) {
      if (newValue) {
        this.fromDate = new Date(newValue.min).toISOString() ;
        this.toDate = new Date(newValue.max).toISOString();
        this.getRealizations();
      }
    },
  },
  methods: {
    getRealizations() {
      this.$realizationsServices.getAllRealizations(this.fromDate,this.toDate).then(realizations => {
        this.realizations = realizations;
      });
    },
    getFromDate(date) {
      date = new Date(date);
      const day = String(date.getDate());
      const month = String(date.getMonth());
      const year = String(date.getFullYear());
      const lang = eXo.env.portal.language;
      const time =   date.toLocaleString(lang, { hour: 'numeric', minute: 'numeric', hour12: true });
      return `${day}/${month}/${year} ${time} ` ;
    },
    closeActionsMenu() {
      this.selectedRealizations = {};
    },
    acceptRealizations(realization) {
      this.$realizationsServices.updateStatus(realization.id,'EDITED').then(() => {
        this.getRealizations(false);
      } );
    },
    rejectRealizations(realization) {
      this.$realizationsServices.updateStatus(realization.id,'REJECTED').then(() => {
        this.getRealizations(false);
      } );
    },
  }
};
</script>
