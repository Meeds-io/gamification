<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
  <div class="d-flex flex-column">
    <div v-if="totalTargetItem > 0" class="mb-1">
      {{ $t('actions.for') }} {{ totalTargetItem }} {{ targetItemLabel }}
    </div>
    <v-chip
      color="#F57C00"
      class="content-box-sizing white--text align-self-start"
      small>
      + {{ rule.score }}
    </v-chip>
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    targetItemLabel: {
      type: String,
      default: null,
    }
  },
  computed: {
    totalTargetItem() {
      return this.rule?.event?.properties?.totalTargetItem || 0;
    }
  }
};
</script>