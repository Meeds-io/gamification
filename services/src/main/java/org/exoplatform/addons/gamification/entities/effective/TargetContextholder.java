package org.exoplatform.addons.gamification.entities.effective;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TargetContextholder implements Serializable {

    protected List<String> usernames;

    protected Date lastModifiedDate;

    protected Date createdDate;

    protected Long score;

    public TargetContextholder() {
        this.lastModifiedDate = new Date();
        this.createdDate = new Date();
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
