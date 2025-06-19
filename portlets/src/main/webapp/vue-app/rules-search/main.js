import './initComponents.js';

export function fetchSearchResult(uri, options) {
  return fetch(`${uri}&lang=${eXo.env.portal.language}`, options);
}

export function formatSearchResult(result, term) {
  if (result?.rules?.length) {
    return result.rules.map(rule => {
      rule.titleExcerpt = !term && rule.title || rule.title.replace(new RegExp(`(${term})`, 'ig'), '<span class="searchMatchExcerpt">$1</span>');
      rule.descriptionExcerpt = !term && rule.description || rule.description.replace(new RegExp(`(${term})`, 'ig'), '<span class="searchMatchExcerpt">$1</span>');
      return rule;
    });
  } else {
    return [];
  }
}

Vue.prototype.$utils?.includeExtensions?.('engagementCenterActions');