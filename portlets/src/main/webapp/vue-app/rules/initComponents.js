import RuleFormDrawer from './components/RuleFormDrawer.vue';
import RuleRecurrenceInput from './components/form/RuleRecurrenceInput.vue';
import RuleLockInput from './components/form/RuleLockInput.vue';
import ButtonGroupItem from './components/form/ButtonGroupItem.vue';
import RuleSuggester from './components/form/RuleSuggester.vue';
import DescriptionEditor from './components/form/DescriptionEditor.vue';

import RuleDetailDrawer from './components/RuleDetailDrawer.vue';
import RuleAnnouncements from './components/detail/RuleAnnouncements.vue';
import RuleProgram from './components/detail/RuleProgram.vue';
import RuleDates from './components/detail/RuleDates.vue';
import RuleHeader from './components/detail/RuleHeader.vue';
import RuleDescription from './components/detail/RuleDescription.vue';
import RuleRecurrence from './components/detail/RuleRecurrence.vue';
import RulePrerequisites from './components/detail/RulePrerequisites.vue';
import RulePrerequisiteItem from './components/detail/RulePrerequisiteItem.vue';
import RuleAnnouncementForm from './components/detail/RuleAnnouncementForm.vue';

import RuleParticipantsDrawer from './components/RuleParticipantsDrawer.vue';

const components = {
  'engagement-center-rule-form-drawer': RuleFormDrawer,
  'engagement-center-button-group-item': ButtonGroupItem,
  'engagement-center-rule-recurrence-input': RuleRecurrenceInput,
  'engagement-center-rule-lock-input': RuleLockInput,
  'engagement-center-description-editor': DescriptionEditor,
  'rule-suggester': RuleSuggester,

  'engagement-center-rule-detail-drawer': RuleDetailDrawer,
  'engagement-center-rule-header': RuleHeader,
  'engagement-center-rule-description': RuleDescription,
  'engagement-center-rule-program': RuleProgram,
  'engagement-center-rule-announcements': RuleAnnouncements,
  'engagement-center-rule-recurrence': RuleRecurrence,
  'engagement-center-rule-dates': RuleDates,
  'engagement-center-rule-announcement-form': RuleAnnouncementForm,
  'engagement-center-rule-prerequisites': RulePrerequisites,
  'engagement-center-rule-prerequisite-item': RulePrerequisiteItem,
  'engagement-center-rule-participants-drawer': RuleParticipantsDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}