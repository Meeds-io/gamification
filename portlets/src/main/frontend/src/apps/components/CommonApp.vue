<template>

 <div>
 <h1> Welcome To eXo a</h1>
 <h2>{{message}}</h2>

 </div>

</template>
<script>

  export default {
    components: {
      
    },
    data() {
        return {
            message: 'hello'
        }
    },
    beforeCreate() {
        console.log('###### Nothing gets called before me!')
        this.message='beforeCreate'
      },
      created() {
          this.message = 'Created.'
          console.log('propertyComputed will update, as this.property is now reactive.')
        },

        mounted() {
            console.log(this.$el.textContent) // I'm text inside the component.
          }


  }
</script>
<style>
</style>