import ActivityAnnouncement from './components/ActivityAnnouncement.vue';

const components = {
  'activity-announcement': ActivityAnnouncement,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
