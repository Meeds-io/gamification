package org.exoplatform.addons.gamification.entities.effective;

import java.io.Serializable;
import java.util.Date;

public class SourceContext implements Serializable {

    protected String username;

    protected Date lastModifiedDate;

    protected Date createdDate;

    protected Long score;

    public SourceContext() {
        this.lastModifiedDate = new Date();
        this.createdDate = new Date();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
