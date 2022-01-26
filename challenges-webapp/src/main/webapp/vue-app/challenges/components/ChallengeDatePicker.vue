<template>
  <div class="challengeDates">
    <div class="challengePlanDateCalender d-flex align-center">
      <i class="uiIconStartDate uiIconBlue"></i>
      <div v-if="disabledStartDate" class="subtitle-1 mx-4 mt-1">
        {{ getFormatDate(startDate) }}
      </div>
      <div v-else>
        <date-picker
          ref="challengeStartDate"
          v-model="startDate"
          :default-value="false"
          :placeholder="$t('challenges.label.startDate')"
          :max-value="maximumStartDate"
          :min-value="minimumStartDate"
          :disabled="disabledStartDate"
          class="flex-grow-1 my-auto"
          @input="emitStartDate(startDate)" />
      </div>
    </div>
    <div v-if="!startDate" class="mx-3 mt-n2 justify-center">
      <span class="error--text">
        * {{ $t('challenges.label.requiredStartDate') }}
      </span>
    </div>
    <div class="challengeEndDateCalender d-flex align-center mt-1">
      <i class="uiIconEndDate uiIconBlue"></i>
      <div v-if="disabledEndDate" class="subtitle-1 mx-4 mt-1">
        {{ getFormatDate(endDate) }}
      </div>
      <div v-else>
        <date-picker
          ref="challengeEndDate"
          v-model="endDate"
          :default-value="false"
          :placeholder="$t('challenges.label.endDate')"
          :min-value="minimumEndDate"
          :disabled="disabledEndDate"
          class="flex-grow-1 my-auto"
          @input="emitEndDate(endDate)" />
      </div>
    </div>
    <div v-if="!endDate" class="mx-3 mt-n2 justify-center">
      <span class="error--text">
        * {{ $t('challenges.label.requiredEndDate') }}
      </span>
    </div>
  </div>
</template>


<script>

export default {
  name: 'ChallengeDatePicker',
  data () {
    return {
      startDate: null,
      endDate: null,
      disabledStartDate: false,
      disabledEndDate: false,
    };
  },
  computed: {
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
    }

  },
  mounted() {
    $('.challengeDate').off('click').on('click', () => {
      this.$refs.challengeStartDate.menu = false;
      this.$refs.challengeEndDate.menu = false;
    });
  },
  methods: {
    emitStartDate(date) {
      this.$emit('startDateChanged',new Date(date));
    },
    emitEndDate(date) {
      this.$emit('endDateChanged',new Date(date));
    },
    getFormatDate(date) {
      return this.$challengeUtils.getFromDate(date);
    }
  }
};
</script>
