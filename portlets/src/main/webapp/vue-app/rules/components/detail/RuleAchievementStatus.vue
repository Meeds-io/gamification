<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
  <v-card
    max-width="380"
    class="rule-prerequisites grey"
    flat>
    <div class="d-flex align-center justify-center pt-4 px-2">
      <v-avatar
        size="32"
        class="me-2"
        tile>
        <v-icon
          class="white--text"
          size="20">
          fas fa-hourglass
        </v-icon>
      </v-avatar>
      <div class="white--text text-wrap font-weight-bold">{{ $t('rule.detail.pendingStatus.instructionsOne') }}</div>
    </div>
    <div class="align-center justify-center pb-4 px-2">
      <div class="white--text text-wrap pb-1">{{ $t('rule.detail.pendingStatus.instructionsTwo') }}</div>
      <div class="white--text text-wrap">{{ $t('rule.detail.pendingStatus.instructionsThree') }}</div>
    </div>
  </v-card>
</template>