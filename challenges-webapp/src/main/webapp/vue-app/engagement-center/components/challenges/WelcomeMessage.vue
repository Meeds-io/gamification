<template>
  <div class="welcomeMessage">
    <img
      src="/challenges/images/Challenge_app_backg_image.png"
      :alt="$t('challenges.welcomeMessageImgAlt')"
      class="mx-4"
      loading="lazy">
    <div class="mx-4 my-6">
      <p class="title"> {{ $t('challenges.welcomeMessage') }} </p>
      <p v-if="canAddChallenge"> {{ $t('challenges.welcomeMessageForManager') }} </p>
      <p v-else> {{ $t('challenges.welcomeMessageForUser') }} </p>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    canAddChallenge: {
      type: Boolean,
      default: false,
    },
  },
};
</script>
