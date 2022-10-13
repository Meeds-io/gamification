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
  <tr>
    <td>
      <div class="align-start text-truncate">
        <engagement-center-rule-title :rule="rule" />
      </div>
    </td>
    <td>
      <div class="align-center text-capitalize">
        {{ TypeLabel }}
      </div>
    </td>
    <td>
      <div class="align-center">
        {{ rule.score }}
      </div>
    </td>
    <td v-if="canManageRule" class="align-center">
      <v-menu
        v-model="menu"
        :left="!$vuetify.rtl"
        :right="$vuetify.rtl"
        bottom
        offset-y
        attach>
        <template #activator="{ on, attrs }">
          <v-btn
            icon
            small
            class="me-2"
            v-bind="attrs"
            v-on="on"
            @blur="closeMenu">
            <v-icon size="16" class="icon-default-color">fas fa-ellipsis-v</v-icon>
          </v-btn>
        </template>
        <v-list dense class="pa-0">
          <v-list-item
            dense
            @mousedown="$event.preventDefault()"
            @click="editRule">
            <v-icon size="13" class="dark-grey-color">fas fa-edit</v-icon>
            <v-list-item-title class="ps-3 d-flex">{{ $t('programs.details.rule.button.edit') }}</v-list-item-title>
          </v-list-item>
          <v-list-item
            v-if="automaticRule"
            dense
            @mousedown="$event.preventDefault()"
            @click="deleteRule">
            <v-icon size="13" class="dark-grey-color">fas fa-trash-alt</v-icon>
            <v-list-item-title class="ps-3 d-flex">
              {{ $t('programs.details.rule.button.delete') }}
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </td>
  </tr>
</template>

<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    canManageRule: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      menu: false
    };
  },
  computed: {
    automaticRule() {
      return this.rule?.type === 'AUTOMATIC';
    },
    TypeLabel() {
      return this.automaticRule ? this.$t('programs.details.rule.type.automatic') : this.$t('programs.details.rule.type.manuel') || '-';
    },
  },
  methods: {
    closeMenu() {
      this.menu = false;
    },
    editRule() {
      if (this.automaticRule) {
        this.$root.$emit('rule-form-drawer', this.rule);
      } else {
        this.$root.$emit('edit-manuel-rule', this.rule);
      }
    },
    deleteRule() {
      this.$emit('delete-rule', this.rule);
    }
  }

};
</script>