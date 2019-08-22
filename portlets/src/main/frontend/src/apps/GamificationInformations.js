import Vue from 'vue'
import GamificationInformationsApp from './components/GamificationInformationsApp.vue'

$(document).ready(() => {
    const lang = eXo && eXo.env && eXo.env.portal && eXo.env.portal.language;
    const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.addon.Gamification-${lang}.json`;

    exoi18n.loadLanguageAsync(lang, url).then(i18n => {
        new Vue({
            render: (h) => h(GamificationInformationsApp),
            i18n
        }).$mount('#app')
    });

});