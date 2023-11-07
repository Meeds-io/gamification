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
  <users-leaderboard-profile-chart
    :identity-id="user.identityId"
    :score="user.score"
    :programs="programs"
    :period="period"
    :program-id="programId"
    :central-points="centralPoints"
    @select="$emit('select', $event)"
    @open="$emit('open')" />
</template>

<script>
export default {
  props: {
    user: {
      type: Object,
      default: null,
    },
    programs: {
      type: Array,
      default: null,
    },
    programId: {
      type: String,
      default: null,
    },
    period: {
      type: String,
      default: () => 'WEEK',
    },
    centralPoints: {
      type: Boolean,
      default: false,
    },
  },
};
</script>