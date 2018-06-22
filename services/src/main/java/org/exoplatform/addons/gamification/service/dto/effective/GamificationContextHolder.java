package org.exoplatform.addons.gamification.service.dto.effective;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;

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
