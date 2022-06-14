<template>
  <v-alert
    v-model="alert"
    :type="type"
    class="walletAlert"
    dismissible>
    <span v-sanitized-html="message" class="mt-8"> </span>
  </v-alert>
</template>
<script>
export default {
  data: () => ({
    alert: false,
    type: '',
    message: '',
  }),
  created() {
    this.$root.$on('show-alert', message => {
      this.displayMessage(message);
    });
  },
  methods: {
    displayMessage(message) {
      this.message = message.message;
      this.type = message.type;
      this.alert = true;
      window.setTimeout(() => this.alert = false, 5000);
    },
  },
};
</script>