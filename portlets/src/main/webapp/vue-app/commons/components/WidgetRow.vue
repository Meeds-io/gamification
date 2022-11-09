<template>
  <v-list class="py-0" dense>
    <div v-if="$slots.title" class="subtitle-2">
      <slot name="title"></slot>
    </div>
    <v-list-item class="px-0">
      <v-list-item-action-text v-if="$slots.icon" class="me-4">
        <slot name="icon"></slot>
      </v-list-item-action-text>
      <v-list-item-content>
        <v-list-item-title class="text-wrap">
          <slot name="content"></slot>
        </v-list-item-title>
      </v-list-item-content>
    </v-list-item>
  </v-list>
</template>