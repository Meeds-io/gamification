<template>
  <div>
    <v-card-text class="d-flex flex-grow-1 text-left px-0 pt-4 pb-2">
      {{ $t('rule.form.label.duration.title') }}
    </v-card-text>

    <v-card-text class="pa-0 flex d-flex ruleDates">
      <select
        v-model="durationFilter"
        class="d-flex flex-grow-0 flex-shrink-0 width-auto ignore-vuetify-classes my-0"
        @change="resetDates">
        <option value="BEFORE">
          {{ $t('rule.form.label.before') }}
        </option>
        <option value="AFTER">
          {{ $t('rule.form.label.after') }}
        </option>
        <option value="BETWEEN">
          {{ $t('rule.form.label.between') }}
        </option>
      </select>
      <div class="flex-grow-0 flex-shrink-0">
        <v-icon color="primary" class="mt-2 px-4">fas fa-calendar-check</v-icon>
      </div>
      <div class="flex-grow-1 flex-shrink-1 overflow-hidden">
        <date-picker
          v-if="displayStartDate"
          v-model="startDateValue"
          :default-value="false"
          :placeholder="$t('challenges.label.startDate')"
          :min-value="minimumStartDate"
          :max-value="maximumStartDate"
          :attach="false"
          :allow-overflow="false"
          class="flex-grow-1 mb-n2"
          required />
        <template v-if="displayEndDate">
          <v-spacer v-if="displayStartDate" class="my-2" />
          <date-picker
            v-model="endDateValue"
            :default-value="false"
            :placeholder="$t('challenges.label.endDate')"
            :min-value="minimumEndDate"
            :attach="false"
            :allow-overflow="false"
            class="flex-grow-1 mb-n2"
            required />
        </template>
      </div>
    </v-card-text>
  </div>
</template>
<script>
export default {
  props: {
    startDate: {
      type: String,
      default: null,
    },
    endDate: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    durationFilter: 'BEFORE',
    startDateValue: null,
    endDateValue: null,
  }),
  computed: {
    validDatesInput() {
      if (this.displayStartDate && this.displayEndDate) {
        return this.startDate && this.endDate;
      } else if (this.displayStartDate) {
        return this.startDate;
      } else if (this.displayEndDate) {
        return this.endDate;
      }
      return true;
    },
    displayStartDate() {
      return this.durationFilter === 'AFTER' || this.durationFilter === 'BETWEEN';
    },
    displayEndDate() {
      return this.durationFilter === 'BEFORE' || this.durationFilter === 'BETWEEN';
    },
    minimumStartDate() {
      return new Date();
    },
    maximumStartDate() {
      if (this.endDate){
        const date = new Date(this.endDate);
        date.setDate(date.getDate()- 1) ;
        return date;
      } else {
        return null;
      }
    },
    minimumEndDate() {
      let date = new Date();
      if (this.startDate){
        date = new Date(this.startDate);
      }
      date.setDate(date.getDate() + 1) ;
      return date;
    },
    ruleStartDate() {
      return this.rule?.startDate;
    },
    ruleEndDate() {
      return this.rule?.endDate;
    },
  },
  watch: {
    startDateValue() {
      this.$emit('update:start-date', this.startDateValue && this.$engagementCenterUtils.getIsoDate(this.startDateValue) || null);
    },
    endDateValue() {
      this.$emit('update:end-date', this.endDateValue && this.$engagementCenterUtils.getIsoDate(this.endDateValue) || null);
    },
    validDatesInput() {
      this.$emit('input', !!this.validDatesInput);
    },
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      if (this.startDate && this.endDate) {
        this.durationFilter = 'BETWEEN';
      } else if (this.endDate) {
        this.durationFilter = 'BEFORE';
      } else {
        this.durationFilter = 'AFTER';
      }
      this.startDateValue = this.startDate ? new Date(this.startDate).getTime() : null;
      this.endDateValue = this.endDate ? new Date(this.endDate).getTime() : null;
      this.$emit('input', !!this.validDatesInput);
    },
    resetDates() {
      this.startDateValue = null;
      this.endDateValue = null;
    },
  }
};
</script>
