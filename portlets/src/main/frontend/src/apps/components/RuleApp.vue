<template>


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

<style>
   .UIToolbarContainer .uiDropdownWithIcon>a{
         color: #4d5466 !important;
     }
      a, a:hover, a:focus {
           color: inherit;
           text-decoration: none;
       }
       a{
           color: none;
           text-decoration: none;
           background-color: transparent;
           -webkit-text-decoration-skip: objects;
       }
    .alert{
      max-width: 98%;
      margin: 0 auto;
      top: 7px;
    }
    .alert-dismissible .close{
      padding:0.5rem 1.25rem !important;
    }
    .alert .close{
      line-height: 16px;   
    }
    .btn-primary:hover {
      background-color: #476a9c;
    }
    .dropdown-toggle:after{
        display:none;
     }
     .dropdown-menu{
        font-size: inherit;
     }

   .alert-success {
        color: #315b73;
        background-color: #d4edda;
        border-color: #c3e6cb;
        margin: 0 auto;
        top: 10px;
    }
   form {
       margin-bottom: 24px;
   }

   h5.mt-0 {
       color: #4d5466;
       font-family: Helvetica, arial, sans-serif;
       line-height: 20px;
       font-size: 1.5em;
       text-transform: uppercase;
       font-weight: bold;
       text-align: center;
       padding: 20px 0px;
   }

   label {
       display: inline-block;
       max-width: 100%;
       margin-bottom: 5px;
       font-weight: 700;
       color: #333;
   }

   input[type="number"] {
       font-size: 15px;
       height: 40px;
       padding: 0 10px;
       border: 2px solid #e1e8ee;
       border-radius: 5px;
       box-shadow: none;
       max-height: 40px;
       text-overflow: ellipsis;
   }

   input[type="number"]:focus:invalid:focus,
   input[type="date"]:focus:invalid:focus {
       border-color: #e9322d;
       -webkit-box-shadow: 0 0 6px #f8b9b7;
       -moz-box-shadow: 0 0 6px #f8b9b7;
       box-shadow: 0 0 6px #f8b9b7;
   }

   .card {
       position: relative;
       border-radius: 3px;
       background: #ffffff;
       border-top: 3px solid #d2d6de;
       margin-bottom: 20px;
       width: 100%;
       box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
       border-top-color: #3c8dbc;
       margin: 0px 11px;
       padding: 15px;

   }
   .require-msg{
       max-width: 100% !important;
       font-size: 14px;
       padding: 10px;
   }
</style>