package org.exoplatform.addons.gamification.activity.processor;

import static org.exoplatform.addons.gamification.activity.processor.ChallengeAnnouncementActivityProcessor.ACTIVITY_TYPE;
import static org.exoplatform.addons.gamification.activity.processor.ChallengeAnnouncementActivityProcessor.ANNOUNCEMENT_COMMENT_PARAM;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

public class ChallengeAnnouncementActivityPreProcessor extends BaseActivityProcessorPlugin {

  public ChallengeAnnouncementActivityPreProcessor(InitParams params) {
    super(params);
  }

  @Override
  public boolean isPreActivityProcessor() {
    return true;
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (!ACTIVITY_TYPE.equals(activity.getType())) {
      return;
    }
    if (activity.isComment() || activity.getType() == null) {
      return;
    }
    if (activity.getTemplateParams().containsKey(ANNOUNCEMENT_COMMENT_PARAM)) {
      activity.getTemplateParams().put(ANNOUNCEMENT_COMMENT_PARAM, null);
    }
  }

}
