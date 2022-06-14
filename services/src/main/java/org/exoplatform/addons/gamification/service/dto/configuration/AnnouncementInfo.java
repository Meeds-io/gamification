package org.exoplatform.addons.gamification.service.dto.configuration;

import java.util.Map;


public class AnnouncementInfo implements Cloneable{

    private Announcement announcement ;

    private Map<String, String> templateParams ;

    public AnnouncementInfo(Announcement announcement, Map<String, String> templateParams) {
        this.announcement = announcement;
        this.templateParams = templateParams;
    }

    public AnnouncementInfo() {
    }


    @Override
    public AnnouncementInfo clone() { // NOSONAR
        return new AnnouncementInfo(announcement, templateParams);
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public Map<String, String> getTemplateParams() {
        return templateParams;
    }

    public void setTemplateParams(Map<String, String> templateParams) {
        this.templateParams = templateParams;
    }
}
