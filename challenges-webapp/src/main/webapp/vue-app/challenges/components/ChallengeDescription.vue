<template>
  <div
    id="descriptionId"
    class="challengeDescription">
    <div class="py-1 px-2 subtitle-1">
      {{ title }}
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
      default: ''
    },
    challenge: {
      type: Object,
      default: function () {
        return {};
      }
    },
    isChallenge: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      inputVal: '',
      charsCount: 0,
      editorReady: false,
      maxLength: 1300,
      disabled: false,
    };
  },
  computed: {
    title() {
      return this.isChallenge ? this.$t('challenges.label.describeYourChallenge') : this.$t('challenges.label.describeYourAchievement');
    },
    challengeDescription () {
      return this.isChallenge && this.challenge && this.challenge.id && this.challenge.description && this.challenge.description.replace( /(<a([^>]+)>)/ig, '').replace( /(<\/a>)/ig, '') || '';
    }
  },
  watch: {
    inputVal(val) {
      return this.emitValue(val);
    },
    value() {
      this.inputVal = this.challengeDescription;
    },
    reset() {
      CKEDITOR.instances['descriptionContent'].destroy(true);
      this.editorReady = false;
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
    this.initCKEditor();
  },
  methods: {
    initCKEditor: function () {
      CKEDITOR.plugins.addExternal('embedsemantic', '/commons-extension/eXoPlugins/embedsemantic/', 'plugin.js');
      let extraPlugins = 'simpleLink,widget,embedsemantic';
      const windowWidth = $(window).width();
      const windowHeight = $(window).height();
      if (windowWidth > windowHeight && windowWidth < 768) {
        extraPlugins = 'simpleLink,selectImage,embedsemantic';
      }
      CKEDITOR.basePath = '/commons-extension/ckeditor/';
      const self = this;
      $(this.$refs.editor).ckeditor({
        customConfig: '/commons-extension/ckeditorCustom/config.js',
        //removePlugins: 'suggester,simpleLink,confighelper',
        extraPlugins: extraPlugins,
        removePlugins: 'confirmBeforeReload,maximize,resize',
        toolbarLocation: 'bottom',
        autoGrow_onStartup: true,
        toolbar: [
          ['Bold', 'Italic', 'BulletedList', 'NumberedList', 'Blockquote'],
        ],
        on: {
          blur: function () {
            $(document.body).trigger('click');
          },
          change: function(evt) {
            const newData = evt.editor.getData();
            self.inputVal = newData;
            let pureText = newData ? newData.replace(/<[^>]*>/g, '').replace(/&nbsp;/g, '').trim() : '';
            const div = document.createElement('div');
            div.innerHTML = pureText;
            pureText = div.textContent || div.innerText || '';
            self.charsCount = pureText.length;
          },
          destroy: function () {
            self.inputVal = '';
            self.charsCount = 0;
          }
        },
      });
    },

    emitValue(text) {
      this.$emit('addDescription',text);
      return text;
    },

    deleteDescription: function() {
      if (  CKEDITOR.instances['descriptionContent']) {
        CKEDITOR.instances['descriptionContent'].destroy();
      }
    },
  }
};
</script>
