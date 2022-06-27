<template>
  <div
    id="descriptionId"
    class="challengeDescription">
    <div class="py-1 px-2 subtitle-1">
      {{ $t('challenges.label.describeYourChallenge') }}
    </div>
    <textarea
      id="descriptionContent"
      ref="editor"
      v-model="inputVal"
      cols="30"
      rows="10"
      :disabled="disabled"></textarea>
    <span class="tooManyChars">
      {{ charsCount }}{{ maxLength > -1 ? ' / ' + maxLength : '' }}
      <i class="uiIconMessageLength"></i>
    </span>
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      inputVal: '',
      maxLength: 1300,
      disabled: false,
    };
  },
  computed: {
    charsCount() {
      return this.inputVal && this.$utils.htmlToText(this.inputVal).length || 0;
    },
  },
  watch: {
    inputVal() {
      this.$emit('input', this.inputVal);
    },
    charsCount() {
      if (this.charsCount > this.maxLength) {
        this.$emit('invalidDescription');
      } else {
        this.$emit('validDescription');
      }
    },
  },
  created() {
    CKEDITOR.basePath = '/commons-extension/ckeditor/';
    this.initCKEditor();
  },
  beforeDestroy() {
    this.destroyCKEditor();
  },
  methods: {
    initCKEditor() {
      this.inputVal = this.value || '';
      $(this.$refs.editor).ckeditor({
        customConfig: '/commons-extension/ckeditorCustom/config.js',
        removePlugins: 'suggester,maximize,resize',
        toolbarLocation: 'bottom',
        autoGrow_onStartup: true,
        toolbar: [
          ['Bold', 'Italic', 'BulletedList', 'NumberedList', 'Blockquote'],
        ],
      });
    },
    destroyCKEditor() {
      if (CKEDITOR.instances['descriptionContent']) {
        CKEDITOR.instances['descriptionContent'].destroy();
      }
    },
  }
};
</script>
