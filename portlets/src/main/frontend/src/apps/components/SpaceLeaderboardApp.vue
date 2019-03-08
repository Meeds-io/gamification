<template>
    <spaceLeaderboard></spaceLeaderboard>
</template>
<script>

    import SpaceLeaderboard from './space/SpaceLeaderboard'
    export default {
        components: {
            SpaceLeaderboard
        }
    }
</script>

<style>
    .fade {
        opacity: 1 !important;
    }

    .fade:not(.show) {
        opacity: 1 !important;
    }

    .popover .arrow {
        width: auto !important;
        height: auto !important;
        margin: 0 !important;
    }

    .bs-popover-auto[x-placement^=top] .arrow,
    .bs-popover-top .arrow {
        bottom: calc((.5rem + 1px) * -2.1) !important;
    }

    .bs-popover-auto[x-placement^=left] .arrow.container-fluid .bs-popover-left .arrow {
        right: calc((.5rem + 1px) * -2.2) !important;
    }

    .bs-popover-auto[x-placement^=bottom] .arrow,
    .bs-popover-bottom .arrow {
        top: calc((.5rem + 1px) * -2.2) !important;
    }

    .bs-popover-auto[x-placement^=left] .arrow,
    .bs-popover-right .arrow {

        right: calc((.5rem + 1px) * -2.2) !important;
    }

    .d2b-tooltip {
        z-index: 99999999555555 !important;
    }

    .d2b-chart {
        width: 219px !important;
        height: 200px !important;
    }

    .d2b-chart-frame {
        width: 239px;
        height: 226px !important;
    }

    .d2b-legend-frame {
        bottom: 2px !important;
        left: 11px !important;
        width: 226px !important;
        min-height: 50px !important;
        text-align: left !important;
    }

    .d2b-legend-svg-icon {
        width: 12px !important;
        height: 12px !important;
    }

    .popover {
        font-family: Helvetica, arial, sans-serif !important;
    }

    .bs-popover-auto[x-placement^=left],
    .bs-popover-left {
        left: 0px !important;
    }

    .vue-avatar--wrapper {
        margin: 3px 6px;
        width: 34px!important;
        zoom: 100%;

    }

    .d2b-legend:not(.d2b-vertical) .d2b-legend-icon {
        margin-top: -0.5px !important;
    }

    .d2b-legend:not(.d2b-vertical) .d2b-legend-item {
        display: block !important;
        width: 50% !important;
        float: left !important;
    }

    .bs-popover-auto[x-placement^=left] .arrow,
    .bs-popover-left .arrow {
        right: calc((.5rem + 1px) * -2.1) !important;
        width: .5rem;
        height: 1rem;
        margin: .3rem 0;
    }

    .d2b-legend:not(.d2b-vertical) .d2b-legend-item:not(:last-child) {
        margin-right: 0 !important;
    }
    .space-leaderboard-portlet {
        padding: 10px 10px 10px 10px;
        width: 360px;
    }
    .d2b-chart-frame .d2b-breadcrumbs-frame:not(.d2b-vertical).d2b-legend-frame, .d2b-chart-frame .d2b-legend-frame:not(.d2b-vertical).d2b-legend-frame {
        height: auto !important;
    }
</style>