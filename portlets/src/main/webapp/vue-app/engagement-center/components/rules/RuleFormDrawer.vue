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
            dense
            @change="selectEvent">
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

        <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 pb-2">
          {{ $t('rule.form.label.description') }}
        </v-card-text>
        <v-card-text class="py-0">
          <extended-textarea
            id="ruleDescription"
            ref="ruleDescription"
            v-model="rule.description"
            :placeholder="$t('rule.form.label.description.placeholder')"
            :max-length="ruleDescriptionTextLength"
            class="pt-0" />
        </v-card-text>
        <div class="d-flex flex-row pa-4">
          <label class="text-subtitle-1 mt-1 pe-3">{{ $t('rule.form.label.program') }}</label>
          <v-chip>
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
        <div class="d-flex flex-row pa-4">
          <label class="text-subtitle-1 mt-1 pe-3">{{ $t('rule.form.label.enabled') }}</label>
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
          class="btn me-2">
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
    value: null,
    ruleDescriptionTextLength: 1300,
    eventExist: false
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
    ruleDescription() {
      return this.rule?.description;
    },
    ruleEvent() {
      return this.rule?.event || this.value;
    },
    ruleTitleValid() {
      return this.ruleTitle?.length > 0;
    },
    ruleDescriptionValid() {
      return this.ruleDescription?.length > 0 && this.ruleDescription?.length < 1301;
    },
    ruleEventValid() {
      return this.ruleEvent !== null;
    },
    disableSaveButton() {
      return this.saving || !this.ruleTitleValid || !this.ruleDescriptionValid || !this.ruleEventValid ;
    },
    drawerTitle() {
      return this.ruleId ? this.$t('rule.form.label.edit') : this.$t('rule.form.label.add');
    },
    saveButtonLabel() {
      return this.ruleId ? this.$t('rule.form.label.button.update') : this.$t('rule.form.label.button.add');
    }
  },
  created() {
    this.$root.$on('rule-form-drawer', (rule) => {
      this.$nextTick().then(() => {
        if (rule === null) {
          this.resetFrom();
        } else {
          this.updateForm(rule);
        }
        this.open();
        this.$nextTick().then(() => this.$root.$emit('rule-form-drawer-opened', this.rule));
      });
    });
  },
  methods: {
    close() {
      this.$refs.ruleFormDrawer.close();
    },
    open() {
      this.$refs.ruleFormDrawer.open();
      window.setTimeout(() => {
        if (this.$refs.ruleTitle) {
          this.$refs.ruleTitle.focus();
        }
      }, 200);
    },
    selectEvent(value) {
      this.$set(this.rule,'event', this.eventMapping.find(event => event.label === value).name);
      if (this.eventExist) {
        this.eventExist = false;
      }
    },
    updateRule() {
      this.saving = true;
      this.$refs.ruleFormDrawer.startLoading();
      if (this.rule.id) {
        this.ruleToUpdate.title = this.rule.title;
        this.ruleToUpdate.description = this.rule.description;
        this.ruleToUpdate.event = this.rule.event;
        this.ruleToUpdate.score = this.rule.score;
        this.ruleToUpdate.enabled = this.rule.enabled;
        this.$ruleServices.updateRule(this.ruleToUpdate)
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
        this.$ruleServices.createRule(this.rule)
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
    resetFrom() {
      this.rule.enabled = true;
      this.rule.event = null;
      this.$set(this.rule,'title','');
      this.$set(this.rule,'description','');
      this.value = {};
      this.rule.score = 20;
      this.rule.area = this.programTitle;
      this.$forceUpdate();
      this.eventExist = false;
    },
    updateForm(rule) {
      this.$set(this.rule,'id',rule.id);
      this.$set(this.rule,'enabled',rule.enabled);
      this.$set(this.rule,'score',rule.score);
      this.$set(this.rule,'event',rule.event);
      this.rule.area = this.programTitle;
      this.value = this.eventMapping.find(event => event.name === rule.event);
      this.ruleToUpdate = rule;
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
