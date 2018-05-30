package org.exoplatform.gamification.service.dto.effective;

import org.exoplatform.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextItemEntity;

import java.io.Serializable;

public class GamificationContextHolder implements Serializable {

   GamificationContextEntity gamificationContextEntity;

   boolean isNew = false;

   public GamificationContextHolder() {
   }

   public GamificationContextEntity getGamificationContextEntity() {
      return gamificationContextEntity;
   }

   public void setGamificationContextEntity(GamificationContextEntity gamificationContextEntity) {
      this.gamificationContextEntity = gamificationContextEntity;
   }

   public boolean isNew() {
      return isNew;
   }

   public void setNew(boolean aNew) {
      isNew = aNew;
   }
}
