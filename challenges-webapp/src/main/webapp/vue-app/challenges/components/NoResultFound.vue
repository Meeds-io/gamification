<template>
  <v-flex class="searchNoResults justify-center d-flex border-box-sizing">
    <div class="d-flex flex-column">
      <img
        src="/challenges/images/challenge_not_found_filter_image.png"
        :alt="$t('challenges.search.noResults')"
        width="230px"
        class="mx-auto mb-2 searchNoResultsImg"
        loading="lazy">
      <div class="d-flex flex-column ma-auto text-center text-sub-title">
        <span class="title">{{ $t('challenges.search.noResults') }}</span>
        <span class="text-sub-title subtitle-1">{{ $t('challenges.search.noResultsMessage') }}</span>
      </div>
    </div>
  </v-flex>
</template>