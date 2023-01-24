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
  <div
    id="descriptionId"
    class="activityRichEditor newEditorToolbar">
    <div v-if="label" class="py-1 subtitle-1">
      {{ label }}
    </div>
    <div
      v-if="displayPlaceholder"
      @click="hidePlaceholder()"
      class="caption text-sub-title position-absolute pa-5 ma-1px full-width">
      {{ placeholder }}
    </div>
    <textarea
      id="descriptionContent"
      ref="editor"
      v-model="inputVal"
      cols="30"
      rows="10"
      class="d-none"></textarea>
    <div
      :class="tooManyChars && 'tooManyChars' || ''"
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
    },
    label: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: ''
    },
    maxLength: {
      type: Number,
      default: () => 1300,
    },
    autofocus: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      inputVal: '',
      minLength: 1,
      disabled: false,
      displayPlaceholder: true,
      editor: null,
    };
  },
  computed: {
    charsCount() {
      return this.inputVal && this.$utils.htmlToText(this.inputVal).length || 0;
    },
    validLength() {
      return this.minLength <= this.charsCount && this.charsCount <= this.maxLength;
    },
    editorReady() {
      return this.editor && this.editor.status === 'ready';
    },
    isMobile() {
      return this.$vuetify.breakpoint.name === 'sm' || this.$vuetify.breakpoint.name === 'xs';
    },
    tooManyChars() {
      return this.charsCount > this.maxLength;
    }
  },
  watch: {
    inputVal: {
      immediate: true,
      handler(val) {
        if (val!== '') {
          if (this.displayPlaceholder) {
            this.displayPlaceholder = false;
          }
        } else {
          this.displayPlaceholder = true;
        }
        this.$emit('input', val);
      }
    },
    validLength() {
      this.$emit('validity-updated', this.validLength);
    },
    editorReady() {
      if (this.editorReady) {
        this.$emit('ready');
      } else {
        this.$emit('unloaded');
      }
    },
    value(val) {
      if (!this.editor) {
        this.initCKEditor();
      }
      let editorData = null;
      try {
        editorData = this.editor.getData();
      } catch (e) {
        // When CKEditor not initialized yet
      }
      if (val !== editorData) {
        this.initCKEditorData(val || '');
        this.setEditorReady();
      }
    }
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
      const removePlugins = 'image,maximize,resize';
      let extraPlugins = 'simpleLink,widget';
      const toolbar = [
        ['Bold', 'Italic', 'BulletedList', 'NumberedList', 'Blockquote'],
      ];
      extraPlugins = `${extraPlugins},emoji,formatOption`;
      if (!this.isMobile) {
        toolbar[0].push('emoji');
      }
      toolbar[0].unshift('formatOption');

      this.inputVal = this.value || '';
      this.editor = CKEDITOR.instances['descriptionContent'];
      const self = this;
      $(this.$refs.editor).ckeditor({
        customConfig: '/commons-extension/ckeditorCustom/config.js',
        extraPlugins,
        removePlugins,
        toolbar,
        toolbarLocation: 'bottom',
        autoGrow_onStartup: true,
        pasteFilter: 'p; a[!href]; strong; i', 
        on: {
          instanceReady: function () {
            self.editor = CKEDITOR.instances['descriptionContent'];
            self.setEditorReady();
            if (self.autofocus) {
              setTimeout( function() {
                self.editor.getSelection().scrollIntoView();
              }, 0 );
              window.setTimeout(() => self.setFocus(), 50);
            }
          },
          change: evt => this.inputVal = evt.editor?.getData() || '',
          destroy: () => this.inputVal = '',
        }
      });
    },
    destroyCKEditor: function () {
      if (this.editor) {
        this.editor.destroy(true);
      }
    },
    hidePlaceholder() {
      this.displayPlaceholder = false;
      this.setFocus();
    },
    initCKEditorData: function (message) {
      this.inputVal = message || '';
      if (this.editor) {
        this.editor.setData(this.inputVal);
      }
    },
    setEditorReady: function() {
      window.setTimeout(() => {
        this.$set(this.editor, 'status', 'ready');
      }, 200);
    },
    setFocus: function() {
      if (this.editorReady) {
        window.setTimeout(() => {
          this.$nextTick().then(() => this.editor.focus());
        }, 200);
      }
    },
  }
};
</script>
