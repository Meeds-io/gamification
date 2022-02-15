<template>
  <v-app
    class="Realizations border-box-sizing">
    <v-toolbar
      color="transparent"
      flat
      class="pa-4 mb-4">
      <div class="border-box-sizing clickable">
        <v-btn class="btn btn-primary" @click="getReport()">
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
      :items-per-page=" realizations && realizations.length"
      :loading="loading"
      hide-default-footer
      class="mx-6 mt-6 realizationsTable">
      <template slot="item" slot-scope="props">
        <tr v-show="realizations">
          <td class="wrap align-center px-6">
            {{ getFromDate(props.item.createdDate) }}
          </td>
          <td class="text-truncate align-center wrap">
            {{ props.item.creator && props.item.creator.remoteId || props.item.earner && props.item.earner.remoteId || '-' }}
          </td>
          <td class="align-center actionTitle px-1">
            <span v-if="props.item.action && props.item.action.type === 'MANUAL'"> {{ props.item.action && props.item.action.event }} </span>
            <span v-if="props.item.action && props.item.action.type === 'AUTOMATIC'">
              {{ props.item.action && props.item.action.title && $t(`exoplatform.gamification.gamificationinformation.rule.title.${props.item.action.event}`) }} </span>
          </td>
          <td class="align-center actionTitle px-0">
            <span v-if="props.item.action && props.item.action.type === 'MANUAL'"> {{ props.item.actionLabel }} </span>
            <span v-if="props.item.action && props.item.action.type === 'AUTOMATIC'">
              {{ props.item.action && props.item.action.title && $t(`exoplatform.gamification.gamificationinformation.rule.description.${props.item.action.event}`) }}             </span>
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
            {{ props.item && props.item.score }}
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
          <td class="text-truncate actions">
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
                    <v-list-item-title class="options">
                      <i class="fas fa-edit pr-1"></i>
                      {{ $t('realisation.label.edit') }}
                    </v-list-item-title>
                  </v-list-item>
                  <v-divider />
                  <v-list-item @mousedown="$event.preventDefault()" v-if="props.item.status === 'REJECTED'">
                    <v-list-item-title class="options" @click="updateRealizations(props.item,'EDITED')">
                      <i class="fas fa-check pr-1"></i>
                      {{ $t('realisation.label.accept') }}
                    </v-list-item-title>
                  </v-list-item>
                  <v-divider />
                  <v-list-item @mousedown="$event.preventDefault()" v-if=" props.item.status === 'NORMAL' || props.item.status === 'EDITED'">
                    <v-list-item-title class="options" @click="updateRealizations(props.item,'REJECTED')">
                      <i class="fas fa-ban pr-1"></i>
                      {{ $t('realisation.label.reject') }}
                    </v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </v-menu>
          </td>
        </tr>
      </template>
    </v-data-table>
    <v-toolbar
      color="transparent"
      flat
      class="pa-2 mb-4">
      <v-btn
        v-if="showLoadMoreButton"
        class="btn"
        :loading="loading"
        :disabled="loading"
        @click="getRealizations(true)"
        block>
        <span class="ms-2 d-none d-lg-inline">
          {{ $t("realisation.label.loadMore") }}
        </span>
      </v-btn>
    </v-toolbar>
  </v-app>
</template>

<script>

export default {
  data: () => ({
    realizations: [],
    selectedRealizations: {},
    realizationsPerPage: 10,
    loading: true,
    loadMore: false,
    limitReached: false,
    showLoadMoreButton: false,
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
          sortable: false,
          value: 'date',
          class: 'actionHeader px-2',
        },
        {
          text: this.$t('realisation.label.creator'),
          align: 'center',
          sortable: false,
          value: 'creator',
          class: 'actionHeader px-1'

        },
        {
          text: this.$t('realisation.label.actionId'),
          align: 'center',
          sortable: false,
          value: 'actionId',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realisation.label.actionLabel'),
          align: 'center',
          sortable: false,
          value: 'actionLabel',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realisation.label.actionType'),
          align: 'center',
          sortable: false,
          value: 'actionType',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realisation.label.program'),
          align: 'center',
          sortable: false,
          value: 'program',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realisation.label.programLabel'),
          align: 'center',
          sortable: false,
          value: 'programLabel',
          class: 'actionHeader px-0'
        },
        {
          text: this.$t('realisation.label.points'),
          align: 'center',
          sortable: false,
          value: 'points',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realisation.label.status'),
          align: 'center',
          sortable: false,
          value: 'status',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realisation.label.grantee'),
          align: 'center',
          sortable: false,
          value: 'grantee',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realisation.label.space'),
          align: 'center',
          sortable: false,
          value: 'space',
          class: 'actionHeader px-1'
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
    getRealizations(loadMore) {
      this.loading = true;
      const offset = loadMore ? this.realizations.length : 0;
      this.$realizationsServices.getAllRealizations(this.fromDate,this.toDate,offset,this.realizationsPerPage).then(realizations => {
        if (realizations.length >= this.realizationsPerPage) {
          this.showLoadMoreButton = true;
        } else {
          this.showLoadMoreButton = false;
        }
        this.realizations = loadMore && this.realizations.concat(realizations) || realizations;

      }).finally(() => {
        this.loading = false;
      });
    },
    getFromDate(date) {
      date = new Date(date);
      const day = String(date.getDate());
      const month = String(date.getMonth());
      const year = String(date.getFullYear());
      const lang = eXo.env.portal.language;
      const time =   date.toLocaleString(lang, { hour: 'numeric', minute: 'numeric', hour12: true }).toLowerCase();
      return `${day}/${month}/${year} ${time} ` ;
    },
    closeActionsMenu() {
      this.selectedRealizations = {};
    },
    updateRealizations(realization,status) {
      this.$realizationsServices.updateStatus(realization.id,status).then(() => {
        this.getRealizations(false);
      } );
    },
    getReport() {
      const offset = this.loadMore ? this.realizations.length : 0;
      return this.$realizationsServices.getReport(this.fromDate, this.toDate,offset, this.realizationsPerPage);
    }
  }
};
</script>
