<template>
  <v-overlay
    :value="true"
    opacity="0.8"
    class="position-absolute full-width full-height"
    absolute>
    <slot></slot>
  </v-overlay>
</template>
