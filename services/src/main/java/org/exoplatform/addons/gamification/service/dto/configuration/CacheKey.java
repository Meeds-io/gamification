package org.exoplatform.addons.gamification.service.dto.configuration;

import java.io.Serializable;
import java.util.List;

public class CacheKey implements Serializable {

    private List<Long> ids ;
    private int  offset ;
    private int limit ;
    private Long id;
    private String title ;
    private Integer context ;

    public CacheKey(Integer context,List<Long> ids, int offset, int limit) {
        this.ids = ids;
        this.offset = offset;
        this.limit = limit;
        this.context = context;
    }

    public CacheKey(Integer context,Long id) {
        this.id = id;
        this.context = context;
    }

    public CacheKey(Integer context,String title) {
        this.title = title;
        this.context = context;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }
}
