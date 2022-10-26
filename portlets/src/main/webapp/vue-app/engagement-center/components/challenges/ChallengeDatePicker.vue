<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
contact@meeds.io
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <div class="challengeDates">
    <div class="challengePlanDateCalender d-flex align-center">
      <i class="uiIconStartDate uiIconBlue"></i>
      <div v-if="disabledStartDate" class="subtitle-1 mx-4 mt-1">
        <date-format
          id="engagementCenterChallengeStartDate"
          :value="startDate" />
      </div>
      <div id="engagementCenterChallengeStartDatePicker" v-else>
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
        <date-format
          id="engagementCenterChallengeEndDatePicker"
          :value="endDate" />
      </div>
      <div id="engagementCenterChallengeEndDatePicker" v-else>
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
    emitStartDate(value) {
      if (value) {
        const date = new Date(value);
        this.$emit('startDateChanged', this.$engagementCenterUtils.getIsoDate(date));
      }
    },
    emitEndDate(value) {
      if (value) {
        const date = new Date(value);
        this.$emit('endDateChanged', this.$engagementCenterUtils.getIsoDate(date));
      }
    },
  },
};
</script>
