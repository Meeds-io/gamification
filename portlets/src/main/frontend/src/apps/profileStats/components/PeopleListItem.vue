<template>
  <v-list-item class="py-0 px-2">
    <v-list-item-avatar class="my-1 me-2" size="30">
      <v-img :src="person.avatar" />
    </v-list-item-avatar>

    <v-list-item-content class="py-0">
      <a
        :id="cmpId"
        class="connectionProfileLink"
        :href="person.profileLink"
        rel="nofollow">
        <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="person.fullname" />
      </a>
    </v-list-item-content>
  </v-list-item>
</template>

<script>
export default {
  props: {
    person: {
      type: Object,
      default: () => ({}),
    },
    people: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data() {
    return {
      cmpId: `react${parseInt(Math.random() * 10000).toString()}`,
    };
  },
};
</script>
