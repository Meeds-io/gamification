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
<template class="VuetifyApp">
  <div class="v-application v-application--is-ltr">
    <div class="v-application--wrap">
      <v-card
        height="280"
        width="370">
        <div class="d-flex flex-row">
          <h4 class="hidden-space align-self-start ms-4"> {{ $t('overview.myContributions.title') }} </h4>
          <v-spacer />
          <a :href="achievementsURL"> <h5 class="text-font-size align-self-end me-7 primary--text"> {{ $t('overview.myContributions.seeAll') }} </h5> </a>
        </div>
        <div class="mt-n2">
          <user-points-widget :overview-display="true" />
        </div>
      </v-card>
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    achievementsURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/achievements`;
    }
  },
};
</script>