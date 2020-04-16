<template>
  <option
    :class="skeleton && 'skeleton-background skeleton-text skeleton-text-width'"
    :value="domain.title">
    {{ skeleton && '&nbsp;&nbsp;&nbsp;&nbsp;' || domain.label }}
  </option>
</template>
<script>
export default {
  props: {
    domain: {
      type: Object,
      default: () => null,
    },
    skeleton: {
      type: Boolean,
      default: () => false,
    },
  },
}
</script>