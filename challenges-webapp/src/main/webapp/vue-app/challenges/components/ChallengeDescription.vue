<template>
  <div
    id="descriptionId"
    class="activityRichEditor">
    <div class="py-1 px-2 subtitle-1">
      {{ $t('challenges.label.describeYourChallenge') }}
    </div>
    <textarea
      id="descriptionContent"
      ref="editor"
      v-model="inputVal"
      cols="30"
      rows="10"
      class="d-none"></textarea>
    <div
      :class="!validLength && 'tooManyChars' || ''"
      class="activityCharsCount">
      {{ charsCount }}{{ maxLength > -1 ? ' / ' + maxLength : '' }}
      <i class="uiIconMessageLength"></i>
    </div>
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
    validLength() {
      return this.charsCount <= this.maxLength;
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
        extraPlugins: 'simpleLink,widget',
        removePlugins: 'suggester,image,maximize,resize',
        toolbarLocation: 'bottom',
        autoGrow_onStartup: true,
        toolbar: [
          ['Bold', 'Italic', 'BulletedList', 'NumberedList', 'Blockquote'],
        ],
        on: {
          change: evt => this.inputVal = evt.editor.getData(),
          destroy: () => this.inputVal = '',
        }
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
