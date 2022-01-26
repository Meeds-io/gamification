import {initExtensions} from './challengesExtensions.js';

//getting language of the PLF
const lang = eXo && eXo.env.portal.language || 'en';

//should expose the locale ressources as REST API
const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Challenges-${lang}.json`;

export function init() {
  exoi18n.loadLanguageAsync(lang, url).then(() => {
    initExtensions();
  });
}