<template>
  <tr>
    <td class="wrap align-center px-6">
      <date-format
        :format="dateFormat"
        :value="realization.createdDate" />
    </td>
    <td class="text-truncate align-center">
      {{ earner }}
    </td>
    <td class="align-center actionTitle px-0">
      <a
        :href="realization.url"
        :class="actionLabelClass"
        class="text-color">
        <span class="actionDescription">
          {{ actionLabel }}
        </span> 
      </a>
    </td>
    <td class="text-truncate align-center">
      <v-chip :color="actionTypeColor" :dark="actionTypeDark" :outlined="!actionTypeDark">
        {{ actionTypeLabel }}
      </v-chip>
    </td>
    <td class="text-truncate align-center">
      {{ domainDescription }}
    </td>
    <td class="text-truncate align-center">
      {{ score }}
    </td>
    <td class="text-truncate align-center">
      <v-chip :color="statusColor" dark>
        {{ statusLabel }}
      </v-chip>
    </td>
    <td class="text-truncate actions align-center">
      <v-menu
        v-if="hasActions"
        offset-y
        attach>
        <template #activator="{ on, attrs }">
          <v-btn
            icon
            small
            class="mx-5"
            v-bind="attrs"
            v-on="on">
            <v-icon size="20px">fa-ellipsis-v</v-icon>
          </v-btn>
        </template>
        <v-list flat class="pt-0 pb-0">
          <template>
            <v-list-item @mousedown="$event.preventDefault()" v-if="canEdit">
              <v-list-item-title class="options" @click="editRealization">
                <i class="fas fa-edit px-1"></i>
                <span class="px-1"> {{ $t('realization.label.edit') }} </span>
              </v-list-item-title>
            </v-list-item>
            <v-list-item @mousedown="$event.preventDefault()" v-if="canAccept">
              <v-list-item-title class="options" @click="updateRealizations('ACCEPTED')">
                <i class="fas fa-check px-1"></i>
                <span class="px-1"> {{ $t('realization.label.accept') }}</span>
              </v-list-item-title>
            </v-list-item>
            <v-list-item @mousedown="$event.preventDefault()" v-if="canReject">
              <v-list-item-title class="options" @click="updateRealizations('REJECTED')">
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

<script>

export default {
  props: {
    realization: {
      type: Object,
      default: null,
    },
    dateFormat: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    menu: false,
  }),
  computed: {
    earner() {
      return this.realization.earner || '';
    },
    isManualType() {
      return this.realization.action?.type === 'MANUAL';
    },
    isAutomaticType() {
      return this.realization.action?.type === 'AUTOMATIC';
    },
    actionLabel() {
      if (this.isAutomaticType) {
        const key = `exoplatform.gamification.gamificationinformation.rule.description.${this.realization.actionLabel}`;
        if (this.$te(key)) {
          return this.$t(key);
        }
      }
      return this.realization.actionLabel;
    },
    domainDescription() {
      return this.realization?.domain?.description || '-';
    },
    score() {
      return this.realization?.score || '-';
    },
    status() {
      return this.realization.status;
    },
    statusLabel() {
      switch (this.status) {
      case 'REJECTED': return this.$t('realization.label.rejected');
      case 'ACCEPTED': return this.$t('realization.label.accepted');
      case 'EDITED': return this.$t('realization.label.edited');
      default: return '';
      }
    },
    statusColor() {
      switch (this.status) {
      case 'REJECTED': return 'red';
      case 'ACCEPTED': return 'green';
      case 'EDITED': return 'teal';
      default: return '';
      }
    },
    actionTypeLabel() {
      if (this.isAutomaticType) {
        return this.$t('realization.label.auto');
      } else if (this.isManualType) {
        return this.$t('realization.label.manual');
      }
      return '-';
    },
    actionTypeColor() {
      if (this.isAutomaticType) {
        return 'blue-grey';
      } else if (this.isManualType) {
        return '';
      }
      return '-';
    },
    actionTypeDark() {
      return this.isAutomaticType;
    },
    actionLabelClass() {
      return !this.realization.url && 'defaultCursor' || '';
    },
    canReject() {
      return this.status === 'ACCEPTED' || this.status === 'EDITED';
    },
    canAccept() {
      return this.status === 'REJECTED';
    },
    canEdit() {
      return this.realization.action && this.realization.action.type === 'MANUAL';
    },
    hasActions() {
      return this.canReject || this.canAccept || this.canEdit;
    },
  },
  methods: {
    updateRealizations(status) {
      this.$realizationsServices.updateRealization(this.realization.id, status)
        .then((updatedRealization) => this.$emit('updated', updatedRealization));
    },
    editRealization() {
      this.$root.$emit('realization-open-edit-drawer', this.realization, this.actionLabel);
    },
  }
};
</script>