import RuleDatesInput from './components/form/RuleDatesInput.vue';
import RuleRecurrenceInput from './components/form/RuleRecurrenceInput.vue';
import RuleLockInput from './components/form/RuleLockInput.vue';
import ButtonGroupItem from './components/form/ButtonGroupItem.vue';
import RuleSuggester from './components/form/RuleSuggester.vue';
import RulePublish from './components/form/RulePublish.vue';

import RuleAchievements from './components/detail/RuleAchievements.vue';
import RuleAchievementItem from './components/detail/RuleAchievementItem.vue';
import RuleProgram from './components/detail/RuleProgram.vue';
import RuleHeader from './components/detail/RuleHeader.vue';
import RuleMenu from './components/detail/RuleMenu.vue';
import RuleDescription from './components/detail/RuleDescription.vue';
import RuleRecurrence from './components/detail/RuleRecurrence.vue';
import RuleRecurrenceValidity from './components/detail/RuleRecurrenceValidity.vue';
import RulePrerequisites from './components/detail/RulePrerequisites.vue';
import RulePrerequisiteItem from './components/detail/RulePrerequisiteItem.vue';
import RuleConnectorPrerequisiteItem from './components/detail/RuleConnectorPrerequisiteItem.vue';
import RuleAnnouncementForm from './components/detail/RuleAnnouncementForm.vue';
import RulePoints from './components/detail/RulePoints.vue';
import RuleDateStart from './components/detail/RuleDateStart.vue';
import RuleDateOver from './components/detail/RuleDateOver.vue';
import RuleInvalidAudience from './components/detail/RuleInvalidAudience.vue';
import RuleInvalidWhitelist from './components/detail/RuleInvalidWhitelist.vue';
import RuleDisabled from './components/detail/RuleDisabled.vue';
import RuleDateEnd from './components/detail/RuleDateEnd.vue';
import RuleDateInfoChip from './components/detail/RuleDateInfoChip.vue';
import RuleFavoriteAction from './components/detail/RuleFavoriteAction.vue';

import RuleFormDrawer from './components/drawers/RuleFormDrawer.vue';
import RuleFormAutomaticFlow from './components/drawers/RuleFormAutomaticFlow.vue';
import RuleDetailDrawer from './components/drawers/RuleDetailDrawer.vue';
import RuleAchievementsDrawer from './components/drawers/RuleAchievementsDrawer.vue';
import ProgramOwnersDrawer from './components/drawers/ProgramOwnersDrawer.vue';
import RuleDrawers from './components/RuleDrawers.vue';
import RuleExtensions from './components/RuleExtensions.vue';

import AvatarsList from './components/detail/AvatarsList.vue';

const components = {
  'engagement-center-button-group-item': ButtonGroupItem,
  'engagement-center-avatars-list': AvatarsList,
  'engagement-center-rule-dates-input': RuleDatesInput,
  'engagement-center-rule-recurrence-input': RuleRecurrenceInput,
  'engagement-center-rule-lock-input': RuleLockInput,
  'engagement-center-rule-publish-editor': RulePublish,
  'rule-suggester': RuleSuggester,

  'engagement-center-rule-header': RuleHeader,
  'engagement-center-rule-menu': RuleMenu,
  'engagement-center-rule-description': RuleDescription,
  'engagement-center-rule-program': RuleProgram,
  'engagement-center-rule-achievements': RuleAchievements,
  'engagement-center-rule-achievement-item': RuleAchievementItem,
  'engagement-center-rule-recurrence': RuleRecurrence,
  'engagement-center-rule-recurrence-validity': RuleRecurrenceValidity,
  'engagement-center-rule-date-info-chip': RuleDateInfoChip,
  'engagement-center-rule-date-start': RuleDateStart,
  'engagement-center-rule-date-end': RuleDateEnd,
  'engagement-center-rule-date-over': RuleDateOver,
  'engagement-center-rule-invalid-whitelist': RuleInvalidWhitelist,
  'engagement-center-rule-invalid-audience': RuleInvalidAudience,
  'engagement-center-rule-disabled': RuleDisabled,
  'engagement-center-rule-points': RulePoints,
  'engagement-center-rule-announcement-form': RuleAnnouncementForm,
  'engagement-center-rule-prerequisites': RulePrerequisites,
  'engagement-center-rule-prerequisite-item': RulePrerequisiteItem,
  'engagement-center-rule-connector-prerequisite-item': RuleConnectorPrerequisiteItem,

  'engagement-center-rule-extensions': RuleExtensions,

  'engagement-center-rule-form-drawer': RuleFormDrawer,
  'engagement-center-rule-form-automatic-flow': RuleFormAutomaticFlow,
  'engagement-center-rule-detail-drawer': RuleDetailDrawer,
  'engagement-center-rule-achievements-drawer': RuleAchievementsDrawer,
  'engagement-center-program-owners-drawer': ProgramOwnersDrawer,
  'engagement-center-rule-drawers': RuleDrawers,

  'rule-favorite-button': RuleFavoriteAction,
};

for (const key in components) {
  Vue.component(key, components[key]);
}