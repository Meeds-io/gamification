<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

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
  <v-app class="application-body">
    <div class="d-flex flex-column align-center justify-center pa-5">
      <div class="title font-weight-bold text-color">
        {{ $t('program.createProgramsTitle') }}
      </div>
      <v-btn
        :href="createProgramLink"
        class="primary mt-5"
        outlined>
        <span class="text-none">{{ $t('program.createProgram') }}</span>
      </v-btn>
    </div>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    createProgramLink: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs#create`,
  }),
};
</script>