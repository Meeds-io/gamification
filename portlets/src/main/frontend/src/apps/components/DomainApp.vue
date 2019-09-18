<template>
    <manage-domains></manage-domains>
</template>

<script>


    import ManageDomains from './domain/ManageDomains'

    export default {
        components: {
            ManageDomains
        }
    }
</script>

<style scoped>
    .UIToolbarContainer .uiDropdownWithIcon > a {
        color: #4d5466 !important;
    }

    a, a:hover, a:focus {
        color: inherit;
        text-decoration: none;
    }

    a {
        color: none;
        text-decoration: none;
        background-color: transparent;
        -webkit-text-decoration-skip: objects;
    }

    .alert {
        max-width: 98%;
        margin: 0 auto;
        top: 7px;
    }

    .alert-dismissible .close {
        padding: 0.5rem 1.25rem !important;
    }

    .alert .close {
        line-height: 16px;
    }

    .btn-primary:hover {
        background-color: #476a9c;
    }

    .dropdown-toggle:after {
        display: none;
    }

    .dropdown-menu {
        font-size: inherit;
    }

</style>