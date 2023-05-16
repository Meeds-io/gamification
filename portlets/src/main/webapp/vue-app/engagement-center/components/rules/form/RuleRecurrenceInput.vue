<template>
  <div>
    <v-card-text class="d-flex flex-grow-1 text-left text-subtitle-1 px-0 py-2">
      {{ $t('gamification.achievementCanBeSubmitted') }}
    </v-card-text>

    <v-btn-toggle
      v-model="period"
      class="flex-grow-1 d-flex flex-wrap justify-space-between"
      mandatory
      outlined
      dense
      group>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.onceTooltip')"
        value="ONCE"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.once') }}
      </engagement-center-button-group-item>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.dailyTooltip')"
        value="DAILY"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.daily') }}
      </engagement-center-button-group-item>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.weeklyTooltip')"
        value="WEEKLY"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.weekly') }}
      </engagement-center-button-group-item>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.monthlyTooltip')"
        value="MONTHLY"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.monthly') }}
      </engagement-center-button-group-item>
    </v-btn-toggle>
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    period: null,
  }),
  created() {
    this.period = this.value;
  },
  watch: {
    period() {
      this.$emit('input', this.period);
    },
    value() {
      if (this.period !== this.value) {
        this.period = this.value;
      }
    },
  }
};
</script>