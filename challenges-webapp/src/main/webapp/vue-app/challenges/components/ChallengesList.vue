<template>
  <v-app>
    <v-container>
      <v-row no-gutters>
        <v-col
          v-for="challenge in challenges"
          :key="challenge.id"
          class="mb-4"
          cols="12"
          md="6"
          lg="4"
          xl="3">
          <challenge-card :challenge="challenge" @edit="$emit('edit-challenge',challenge)" />
        </v-col>
      </v-row>
    </v-container>
  </v-app>
</template>

<script>

export default {
  props: {
    challenges: {
      type: Array,
      default: function() {
        return [];
      },
    }
  },
};
</script>
