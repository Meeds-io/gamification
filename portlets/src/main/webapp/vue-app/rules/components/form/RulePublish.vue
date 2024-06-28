<template>
  <div
    id="engagementCenterRulePublication"
    class="d-flex flex-column">
    <div class="d-flex align-center pt-4">
      <v-card
        :max-width="spaceId && '50%' || 'calc(100% - 50px)'"
        class="flex-grow-1 flex-shrink-0 text-wrap d-flex text-start me-2"
        flat>
        {{ $t('rule.form.publishLabel') }}
        <template v-if="!spaceId">
          {{ $t('rule.form.personalStream') }}
        </template>
      </v-card>
      <div v-if="spaceId" class="flex-grow-1 flex-shrink-1 text-truncate">
        <exo-space-avatar
          :space-id="spaceId"
          :space="space" />
      </div>
      <div class="flex-shrink-0 ms-2">
        <v-switch
          id="engagementCenterRulePublishSwitch"
          v-model="publish"
          class="my-0 ms-0 me-n1" />
      </div>
    </div>
    <div v-if="rule.id && !rule.published" class="text-subtitle">
      {{ $t('rule.form.actionHidden') }}
    </div>
    <rich-editor
      v-if="publish"
      ref="rulePublicationEditor"
      id="engagementCenterRulePublishMessage"
      v-model="message"
      :max-length="MAX_LENGTH"
      :template-params="templateParams"
      :placeholder="$t('rule.form.rulePublicationPlaceholder')"
      :object-id="metadataObjectId"
      :suggester-space-id="spaceId"
      object-type="rule"
      ck-editor-type="activityContent_rule"
      class="flex my-3"
      autofocus
      tag-enabled
      @validity-updated="validMessage = $event"
      @attachments-edited="$emit('attachments-edited')" />
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    program: {
      type: Object,
      default: null,
    },
    templateParams: {
      type: Object,
      default: null,
    },
    metadataObjectId: {
      type: String,
      default: null,
    },
    enabled: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    MAX_LENGTH: 1300,
    publish: false,
    message: null,
    userId: eXo.env.portal.userIdentityId,
    username: eXo.env.portal.userName,
    validMessage: false,
  }),
  computed: {
    space() {
      return this.program?.space;
    },
    spaceId() {
      return this.program?.spaceId || this.program?.space?.id;
    },
  },
  watch: {
    publish() {
      this.$emit('update:publish', this.publish);
    },
    message() {
      this.$emit('update:message', this.message);
    },
    spaceId: {
      immediate: true,
      handler() {
        this.$emit('update:spaceId', this.spaceId);
      },
    },
    validMessage() {
      this.$emit('update:validMessage', this.validMessage);
    },
  },
  created() {
    this.validMessage = true;
    this.message = '';
  },
  mounted() {
    this.publish = this.enabled;
  },
  methods: {
    saveAttachments() {
      this.$refs?.rulePublicationEditor?.saveAttachments();
    },
  },
};
</script>