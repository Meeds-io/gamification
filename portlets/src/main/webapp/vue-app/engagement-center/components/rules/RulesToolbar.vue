<template>
  <v-toolbar
    id="rulesListToolbar"
    flat>
    <v-toolbar-title v-if="canManageRule">
      <v-icon
        v-if="menuHeaderChanged"
        @click="changeHeaderMenu">
        fas fa-arrow-left
      </v-icon>
      <div v-else class="text-no-wrap mt-sm-3">
        <div
          class="d-inline-block">
          <v-btn
            class="btn btn-primary"
            small
            @click="$root.$emit('rule-form-drawer')">
            <v-icon dark>
              mdi-plus
            </v-icon>
            <span class="ms-2 d-none d-lg-inline subtitle-1">
              {{ $t('programs.details.rule.button.addRule') }}
            </span>
          </v-btn>
        </div>
      </div>
    </v-toolbar-title>
    <v-spacer v-if="!isMobile" />
    <div>
      <v-text-field
        v-if="!isMobile"
        v-model="keyword"
        :placeholder="$t('programs.details.filter.filterRules')"
        prepend-inner-icon="fa-filter"
        class="pa-0 me-3 my-auto"
        clearable />
      <v-text-field
        v-else-if="isMobile && menuHeaderChanged"
        v-model="keyword"
        :placeholder="$t('programs.details.filter.filterRules')"
        prepend-inner-icon="fa-filter"
        class="pa-0 ms-3 my-auto"
        clearable />
    </div>
    <v-spacer v-if="isMobile" />
    <v-scale-transition>
      <select
        v-model="filter"
        class="width-auto my-auto ignore-vuetify-classes d-none d-sm-inline">
        <option
          v-for="ruleFilter in ruleFilters"
          :key="ruleFilter.value"
          :value="ruleFilter.value">
          {{ ruleFilter.text }}
        </option>
      </select>
    </v-scale-transition>
    <div class="d-sm-none">
      <v-icon
        v-if="!menuHeaderChanged"
        @click="changeHeaderMenu">
        fa-filter
      </v-icon>
      <v-icon
        v-else
        @click="openBottomMenuFilter">
        fa-sliders-h
      </v-icon>
    </div>
    <v-bottom-sheet v-model="bottomMenu" class="pa-0">
      <v-sheet class="text-center">
        <v-toolbar
          color="primary"
          dark
          class="border-box-sizing">
          <v-btn text @click="bottomMenu = false">
            {{ $t('programs.details.filter.cancel') }}
          </v-btn>
          <v-spacer />
          <v-toolbar-title>
            <v-icon>fa-filter</v-icon>
            {{ $t('programs.details.filter.cancel') }}
          </v-toolbar-title>
          <v-spacer />
          <v-btn text @click="changeFilterSelection">
            {{ $t('programs.details.filter') }}
          </v-btn>
        </v-toolbar>
        <v-list>
          <v-list-item
            v-for="ruleFilter in ruleFilters"
            :key="ruleFilter"
            @click="filterToChange = ruleFilter.value">
            <v-list-item-title class="align-center d-flex">
              <v-icon v-if="filterToChange === ruleFilter.value">fa-check</v-icon>
              <span v-else class="me-6"></span>
              <v-spacer />
              <div>
                {{ ruleFilter.text }}
              </div>
              <v-spacer />
              <span class="me-6"></span>
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-sheet>
    </v-bottom-sheet>
  </v-toolbar>
</template>

<script>

export default {
  props: {
    canManageRule: {
      type: Boolean,
      default: false,
    },
    keyword: {
      type: String,
      default: null,
    },
    filter: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    filterToChange: null,
    bottomMenu: false,
    menuHeaderChanged: false,
  }),
  computed: {
    ruleFilters() {
      return [{
        text: this.$t('programs.details.filter.all'),
        value: 'ALL',
      },{
        text: this.$t('programs.details.filter.enabled'),
        value: 'ENABLED',
      },{
        text: this.$t('programs.details.filter.disabled'),
        value: 'DISABLED',
      }];
    },
    isMobile() {
      return this.$vuetify.breakpoint.xs;
    }
  },
  watch: {
    keyword() {
      this.$emit('keyword-changed', this.keyword);
    },
    filter() {
      this.$emit('filter-changed', this.filter);
    },
  },
  methods: {
    applyFilter() {
      this.$emit('filter-changed', this.filter);
    },
    openBottomMenuFilter() {
      this.filterToChange = this.filter;
      this.bottomMenu = true;
    },
    changeFilterSelection() {
      this.bottomMenu = false;
      this.filter = this.filterToChange;
      this.applyFilter();
    },
    changeHeaderMenu() {
      this.menuHeaderChanged = !this.menuHeaderChanged;
    }
  }
};
</script>