<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2023 Meeds Association
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
  <div>
    <div class="d-flex">
      <v-tooltip bottom>
        <template #activator="{ on }">
          <div 
            v-on="on" 
            class="d-flex my-auto clickable" 
            @click="$emit('back-to-list')">
            <v-icon class="px-3" size="16">
              fas fa-arrow-left
            </v-icon>
            <div class="text-subtitle-1 font-weight-bold dark-grey-color"> {{ $t('programs.details.label.BackToList') }} </div>
          </div>
        </template>
        <span>{{ $t('programs.details.label.BackToList') }}</span>
      </v-tooltip>
    </div>
    <div class="pt-12 d-flex align-center justify-center">
      <v-icon 
        class="px-8 primary--text"
        size="40">
        fas fa-award
      </v-icon>
      <v-icon 
        class="px-4 px-sm-12 secondary--text"
        size="40">
        fas fa-stream
      </v-icon>
      <v-icon 
        class="px-8 primary--text"
        size="40">
        fas fa-trophy
      </v-icon>
    </div>
    <div class="d-flex flex-column justify-center align-center py-12">
      <span class="text-header-title font-weight-regular">
        {{ $t('programs.details.programDeleted') }}
      </span>
      <v-btn
        class="btn btn-primary mt-8"
        :href="programListLink">
        <span class="mx-2 text-capitalize-first-letter subtitle-1">
          {{ $t('programs.details.programDeleted.explore') }}
        </span>
      </v-btn>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      programListLink: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`
    };
  },
};
</script>
