<template>
  <v-list-item class="py-0 px-2">
    <v-list-item-avatar class="my-1 mr-2" size="30">
      <v-img :src="person.avatar" />
    </v-list-item-avatar>

    <v-list-item-content class="py-0">
      <a :id="cmpId" class="connectionProfileLink" :href="person.profileLink" rel="nofollow">
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
      labels: {
        type: Object,
        default: null,
      },
    },
    data() {
      return {
        cmpId: `react${parseInt(Math.random() * 10000).toString()}`,
      };
    },
    created() {
      if (!this.labels) {
        this.labels = {
          CancelRequest: this.$t('profile.label.CancelRequest'),
          Confirm: this.$t('profile.label.Confirm'),
          Connect: this.$t('profile.label.Connect'),
          Ignore: this.$t('.profile.Ignore'),
          RemoveConnection: this.$t('profile.label.RemoveConnection'),
          StatusTitle: `${this.$t('profile.label.StatusTitle')}...`,
        };
      }
    },
    mounted() {
      this.initTiptip();
    },
    methods: {
    initTiptip() {
      this.$nextTick(() => {
        $(`#${this.cmpId}`).userPopup({
          restURL: '/portal/rest/social/people/getPeopleInfo/{0}.json',
          userId: this.person.username,
          labels: this.labels,
          content: false,
          keepAlive: true,
          defaultPosition: 'top_left',
          maxWidth: '240px',
        });
      });
    },      
  },
  }
</script>