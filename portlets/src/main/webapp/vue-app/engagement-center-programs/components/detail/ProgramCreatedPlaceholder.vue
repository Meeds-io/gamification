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
  <div class="d-flex flex-column align-center justify-center">
    <span class="subtitle-1 pt-14">
      {{ $t('programs.details.label.newProgramIntroduction') }}
    </span>
    <span class="py-10">
      <v-btn
        color="primary"
        depressed
        large
        @click="$emit('add')">
        <v-icon class="font-weight-bold" dark>
          mdi-plus
        </v-icon>
        <span class="ms-2 d-none d-lg-inline title font-weight-bold">
          {{ $t('programs.details.rule.button.addIncentive') }}
        </span>
      </v-btn>
    </span>
    <div class="pb-12 subtitle-1 text-wrap">
      <div class="d-flex align-center">
        <v-card
          class="d-flex justify-center me-3"
          min-width="50"
          flat>
          <v-icon color="primary" size="30">fa-trophy</v-icon>
        </v-card>
        {{ $t('programs.details.label.manualActionIntroduction') }}
      </div>
      <div class="d-flex align-center pt-4">
        <v-card
          class="d-flex justify-center me-3"
          min-width="50"
          flat>
          <v-icon color="primary" size="30">fa-award</v-icon>
        </v-card>
        {{ $t('programs.details.label.automaticActionIntroduction') }}
      </div>
    </div>
  </div>
</template>