<template>
  <!-- when using a component in a template Vue.js expects the DOM element to be
    lowercased and dasherized. e.g. RulesList -> rules-list -->

    <manage-rules></manage-rules>
</template>
<script>

    import ManageRules from './rule/ManageRules'
  export default {
    components: {
      ManageRules
    }
  }
</script>