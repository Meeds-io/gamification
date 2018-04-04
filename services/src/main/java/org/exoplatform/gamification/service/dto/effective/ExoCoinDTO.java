package org.exoplatform.gamification.service.dto.effective;

import org.exoplatform.gamification.entities.domain.effective.ExoCoinEntity;
import org.hibernate.validator.constraints.NotBlank;

public class ExoCoinDTO {

    @NotBlank
    protected String title;

    protected String hash;

    protected long count;

    public ExoCoinDTO(ExoCoinEntity exoCoin) {

        this.title = exoCoin.getType();

        this.hash = exoCoin.getHash();

        this.count = exoCoin.getCount();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public String toString() {
        return "BadgeItemDTO{" +
                "title='" + title + '\'' +
                ", hash='" + hash + '\'' +
                ", count='" + count + '\'' +
                "}";
    }
}
