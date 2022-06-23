extensionRegistry.registerComponent('RealizationsHeaderLeft', 'realizations-header-left', {
  id: 'export-xslx',
  vueComponent: Vue.options.components['realizations-export-xslx'],
  rank: 20,
});

extensionRegistry.registerComponent('RealizationsHeaderRighterLeft', 'realizations-header-righter-left', {
  id: 'filter-input',
  vueComponent: Vue.options.components['realizations-filter'],
  rank: 20,
});

extensionRegistry.registerComponent('RealizationsBody', 'realizations-body', {
  id: 'realizations-table',
  vueComponent: Vue.options.components['realizations-table'],
  rank: 50,
});

extensionRegistry.registerComponent('RealizationLoadMore', 'realizations-load-more', {
  id: 'realizations-load-more',
  vueComponent: Vue.options.components['realizations-table-load-more'],
  rank: 60,
});



