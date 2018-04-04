package org.exoplatform.gamification.entities.domain.effective;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ExoCoinEntity implements Serializable{

    @Column(name = "type")
    protected String type;

    @Column(name = "hash")
    protected String hash;

    @Column(name = "count")
    protected long count;

    public ExoCoinEntity() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
