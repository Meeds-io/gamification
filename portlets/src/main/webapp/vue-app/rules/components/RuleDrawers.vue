<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
    <engagement-center-rule-detail-drawer
      ref="challengeDetailsDrawer" />
    <engagement-center-rule-achievements-drawer
      ref="achievementsDrawer" />
    <engagement-center-rule-form-drawer
      ref="ruleFormDrawer" />
    <engagement-center-program-owners-drawer
      ref="ownersDetails" />
    <exo-confirm-dialog
      ref="deleteRuleConfirmDialog"
      :title="$t('programs.details.title.confirmDeleteRule')"
      :message="$t('actions.deleteConfirmMessage')"
      :ok-label="$t('programs.details.ok.button')"
      :cancel-label="$t('programs.details.cancel.button')"
      @ok="deleteRule" />
    <engagement-center-rule-extensions />
  </div>
</template>
<script>
export default {
  data: () => ({
    selectedRule: null,
  }),
  created() {
    document.addEventListener('rule-delete-confirm', this.confirmDelete);
  },
  beforeDestroy() {
    document.removeEventListener('rule-delete-confirm', this.confirmDelete);
  },
  methods: {
    deleteRule() {
      this.loading = true;
      this.$ruleService.deleteRule(this.selectedRule.id)
        .then(() => {
          this.$root.$emit('alert-message', this.$t('programs.details.ruleDeleteSuccess'), 'success');
          this.$root.$emit('rule-deleted-event', this.selectedRule);
        })
        .catch(e => {
          let msg = '';
          if (e.message === '401' || e.message === '403') {
            msg = this.$t('actions.deletePermissionDenied');
          } else if (e.message  === '404') {
            msg = this.$t('actions.notFound');
          } else  {
            msg = this.$t('actions.deleteError');
          }
          this.$root.$emit('alert-message', msg, 'error');
        })
        .finally(() => this.loading = false);
    },
    confirmDelete(event) {
      const rule = event?.detail;
      if (rule) {
        this.selectedRule = rule;
        this.$refs.deleteRuleConfirmDialog.open();
      }
    },
  },
};
</script>