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
  <exo-drawer
    ref="drawer"
    v-model="drawer"
    :right="!$vuetify.rtl"
    :allow-expand="allowExpand"
    class="EngagementCenterProgramColorDrawer"
    go-back-button
    @opened="init"
    @expand-updated="expanded = $event">
    <template #title>
      {{ $t('program.form.programColorDrawerTitle') }}
    </template>
    <template #content>
      <v-row class="ma-0">
        <v-col :cols="expanded && 6 || 12" class="d-flex">
          <v-card
            class="d-flex mx-auto"
            max-width="280"
            flat>
            <engagement-center-rule-card
              v-if="initialized"
              :rule="rule"
              no-validation />
          </v-card>
        </v-col>
        <v-col :cols="expanded && 6 || 12" class="d-flex">
          <div class="mx-auto position-relative" flat>
            <v-color-picker
              v-model="color"
              :swatches="swatches"
              dot-size="25"
              width="340"
              mode="hexa"
              show-swatches />
          </div>
        </v-col>
      </v-row>
    </template>
    <template #footer>
      <div class="d-flex">
        <v-spacer />
        <v-btn
          class="btn mx-1"
          @click="close">
          {{ $t('programs.label.cancel.button') }}
        </v-btn>
        <v-btn
          :loading="loading"
          :disabled="disabledButton"
          class="btn btn-primary"
          @click="apply">
          {{ $t('gamification.apply') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    program: {
      type: Object,
      default: null,
    },
    allowExpand: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    drawer: false,
    expanded: false,
    initialized: false,
    loading: false,
    color: null,
    validColor: false,
    swatches: [
      ['#FF0000', '#319ab3', '#f97575'],
      ['#98cc81', '#4273c8', '#cea6ac'],
      ['#bc99e7', '#9ee4f5', '#774ea9'],
      ['#ffa500', '#bed67e', '#0E100F'],
      ['#ffaacc', '#0000AA', '#000055'],
    ],
  }),
  computed: {
    hexaColor() {
      return this.color?.hexa || this.color;
    },
    programColor() {
      return this.color?.hexa || this.color;
    },
    disabledButton() {
      return !this.validColor || this.hexaColor.toUpperCase() === this.program?.color?.toUpperCase();
    },
    rule() {
      const program = Object.assign({}, this.program);
      program.color = this.hexaColor;
      return {
        title: this.$t('program.form.programColor.RuleExampleTitle'),
        description: this.$t('program.form.programColor.RuleExampleDescription'),
        program,
        enabled: true,
        score: 1600,
      };
    },
  },
  watch: {
    drawer() {
      this.$root.$emit('close-alert-message');
    },
    hexaColor() {
      if (this.drawer) {
        this.$root.$emit('close-alert-message');
        if (this.initialized) {
          this.loading = true;
          this.$programService.checkColorValidity(this.program?.id, this.hexaColor)
            .then(canUse => {
              this.validColor = canUse === 'true';
              if (!this.validColor) {
                this.$root.$emit('alert-message', this.$t('program.form.programColor.programColorAlreadyUsed'), 'warning');
              }
            })
            .finally(() => this.loading = false);
        }
      }
    },
  },
  methods: {
    init() {
      this.initialized = false;
      this.color = this.value;
      this.validColor = false;
      const self = this;
      window.require(['PORTLET/gamification-portlets/EngagementCenterActions'], function() {
        self.$nextTick(() => {
          self.initialized = true;
          if (!self.value) {
            self.color = '#707070';
          }
        });
      });
    },
    open() {
      window.require(['PORTLET/gamification-portlets/EngagementCenterActions']);
      this.$refs.drawer.open();
    },
    close() {
      this.$refs.drawer.close();
    },
    apply() {
      this.$emit('input', this.hexaColor);
      this.close();
    }
  }
};
</script>
