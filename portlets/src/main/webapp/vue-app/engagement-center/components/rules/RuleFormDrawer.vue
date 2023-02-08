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
  <exo-drawer
    ref="ruleFormDrawer"
    right
    v-model="drawer"
    body-classes="hide-scroll decrease-z-index-more">
    <template slot="title">
      {{ drawerTitle }}
    </template>
    <template slot="content">
      <v-form
        ref="RuleForm"
        class="form-horizontal pt-0 pb-4"
        flat
        @submit="updateRule">
        <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 pb-2">
          {{ $t('rule.form.label.rules') }}
        </v-card-text>
        <v-card-text class="d-flex py-0">
          <input
            id="ruleTitle"
            ref="ruleTitle"
            v-model="rule.title"
            :placeholder="$t('rule.form.label.title.placeholder')"
            type="text"
            class="ignore-vuetify-classes flex-grow-1"
            required>
        </v-card-text>
        <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 pb-2">
          {{ $t('rule.form.label.event') }}
        </v-card-text>
        <v-card-text class="py-0">
          <v-autocomplete
            id="EventSelectAutoComplete"
            ref="EventSelectAutoComplete"
            v-model="value"
            :items="eventNames"
            item-text="label"
            class="pa-0"
            filled
            persistent-hint
            dense>
            <template #selection="data">
              <v-chip
                v-bind="data.attrs"
                :input-value="data.selected"
                :title="data.item && data.item.label || data.item"
                @click="data.select">
                {{ data.item && data.item.label || data.item }}
              </v-chip>
            </template>
            <template #item="data">
              <v-list-item-content v-text="data.item.label" />
            </template>
          </v-autocomplete>
        </v-card-text>
        <v-card-text v-if="eventExist" class="error--text py-0">
          {{ $t('rule.form.error.sameEventExist') }}
        </v-card-text>
        <v-card-text>
          <div class="py-2">
            <engagement-center-description-editor
              id="ruleDescription"
              ref="ruleDescription"
              v-model="rule.description"
              :label="$t('rule.form.label.description')"
              :placeholder="$t('rule.form.label.description.placeholder')"
              @addDescription="addDescription($event)"
              @validity-updated=" validDescription = $event" />
          </div>
        </v-card-text>
        <div class="d-flex flex-row pa-4">
          <label class="text-subtitle-1 mt-1 pe-3">{{ $t('rule.form.label.program') }}</label>
          <v-chip color="primary">
            <v-avatar left>
              <v-img :src="programCover" />
            </v-avatar>
            <span class="text-truncate">
              {{ programTitle }}
            </span>
          </v-chip>
        </div>
        <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 pb-2">
          {{ $t('rule.form.label.rewards') }}
        </v-card-text>
        <div class="d-flex flex-row px-4">
          <v-text-field
            v-model="rule.score"
            class="mt-0 pt-0 me-2"
            hide-details
            outlined
            dense
            style="max-width: 185px" />
          <label class="my-auto">{{ $t('rule.form.label.points') }}</label>
        </div>
        <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 pb-2">
          {{ $t('rule.form.label.status') }}
        </v-card-text>
        <div class="d-flex flex-row px-4">
          <label class="subtitle-1 text-light-color mt-1 pe-3">{{ $t('rule.form.label.enabled') }}</label>
          <v-switch
            id="allowAttendeeToUpdateRef"
            ref="allowAttendeeToUpdateRef"
            v-model="rule.enabled"
            class="mt-0 ms-4" />
        </div>
      </v-form>
    </template>
    <template slot="footer">
      <div class="d-flex">
        <v-spacer />
        <v-btn
          class="btn me-2"
          @click="close">
          {{ $t('rule.form.label.button.cancel') }}
        </v-btn>
        <v-btn
          :disabled="disableSaveButton"
          class="btn btn-primary"
          @click="updateRule">
          {{ saveButtonLabel }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  props: {
    program: {
      type: Object,
      default: () => null,
    },
    events: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data: () => ({
    rule: {},
    ruleToUpdate: {},
    saving: false,
    eventMapping: [],
    drawer: false,
    value: '',
    eventExist: false,
    validDescription: false,
    validEvent: false,
  }),
  computed: {
    eventNames() {
      this.events.filter(event => event != null).forEach(event => {
        const eventObject = {};
        eventObject.name = event;
        let fieldLabelI18NKey = `exoplatform.gamification.gamificationinformation.rule.title.${event}`;
        let fieldLabelI18NValue = this.$t(fieldLabelI18NKey);
        if (fieldLabelI18NValue === fieldLabelI18NKey) {
          fieldLabelI18NKey = `exoplatform.gamification.gamificationinformation.rule.title.def_${event}`;
          fieldLabelI18NValue = this.$t(fieldLabelI18NKey);
        }
        eventObject.label = fieldLabelI18NValue === fieldLabelI18NKey ? event : fieldLabelI18NValue;
        this.eventMapping.push(eventObject);
      });
      return this.eventMapping;
    },
    programCover() {
      return this.program?.coverUrl || '';
    },
    programTitle() {
      return this.program?.title;
    },
    ruleTitle() {
      return this.rule?.title;
    },
    ruleId() {
      return this.rule?.id;
    },
    ruleTitleValid() {
      return this.ruleTitle?.length > 0;
    },
    disableSaveButton() {
      return this.saving || !this.ruleTitleValid || !this.validDescription || !this.validEvent ;
    },
    drawerTitle() {
      return this.ruleId ? this.$t('rule.form.label.edit') : this.$t('rule.form.label.add');
    },
    saveButtonLabel() {
      return this.ruleId ? this.$t('rule.form.label.button.update') : this.$t('rule.form.label.button.add');
    }
  },
  watch: {
    value: {
      immediate: true,
      handler() {
        this.emitSelectedValue(this.value);
        this.validEvent = this.value && this.value !== '';
      }
    },
  },
  created() {
    this.$root.$on('rule-form-drawer', (rule) => {
      this.rule = rule && JSON.parse(JSON.stringify(rule)) || {
        score: 20,
        enabled: true,
        area: this.programTitle
      };
      this.eventExist = false;
      if (this.$refs.ruleFormDrawer) {
        this.$refs.ruleFormDrawer.open();
      }
      window.setTimeout(() => {
        if (this.$refs.ruleTitle) {
          this.$refs.ruleTitle.focus();
        }
      }, 200);
      this.$nextTick().then(() => {
        this.$refs.ruleDescription.initCKEditor();
        this.$root.$emit('rule-form-drawer-opened', this.rule);
        this.value = this.eventMapping.find(event => event.name === rule?.event) || '';
      });
    });
  },
  methods: {
    close() {
      this.$refs.ruleDescription.destroyCKEditor();
      this.$refs.ruleFormDrawer.close();
      this.rule.enabled = true;
      this.rule.event = null;
      this.$set(this.rule,'title','');
      this.rule.description = '';
      this.value = {};
    },
    emitSelectedValue(value) {
      const eventObject = this.eventMapping.find(event => event.label === value);
      if (eventObject) {
        this.$set(this.rule,'event', eventObject.name);
        if (this.eventExist) {
          this.eventExist = false;
        }
      }
    },
    addDescription(value) {
      if (value) {
        this.$set(this.rule,'description', value);
      }
    },
    updateRule() {
      this.saving = true;
      this.$refs.ruleFormDrawer.startLoading();
      if (this.rule.id) {
        this.$ruleServices.updateRule(this.rule)
          .then(rule => {
            this.displayAlert(this.$t('programs.details.ruleUpdateSuccess'));
            this.$root.$emit('program-rules-refresh', rule);
            this.close();
          }).catch(e => {
            if (e.message === '409') {
              this.eventExist = true;
            }}).finally(() => {
            this.saving = false;
            this.$refs.ruleFormDrawer.endLoading();
          });
      } else {
        this.$ruleServices.createRule(this.rule, this.program)
          .then(rule => {
            this.displayAlert(this.$t('programs.details.ruleCreationSuccess'));
            this.$root.$emit('program-rules-refresh', rule);
            this.close();
          }).catch(e => {
            if (e.message === '409') {
              this.eventExist = true;
            }}).finally(() => {
            this.saving = false;
            this.$refs.ruleFormDrawer.endLoading();
          });
      }
    },
    displayAlert(message, type) {
      document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
        message,
        type: type || 'success',
      }}));
    },
  }
};
</script>
