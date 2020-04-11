import PopularSpaces from './components/PopularSpaces.vue';
import PopularSpacesItem from './components/PopularSpacesItem.vue';

const components = {
  'popular-spaces': PopularSpaces,
  'popular-spaces-item': PopularSpacesItem,
};

for(const key in components) {
  Vue.component(key, components[key]);
}
