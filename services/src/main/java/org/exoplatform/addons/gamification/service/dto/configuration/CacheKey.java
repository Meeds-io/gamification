package org.exoplatform.addons.gamification.service.dto.configuration;

import java.io.Serializable;
import java.util.Objects;

public class CacheKey implements Serializable {

  private static final long serialVersionUID = 8953827201312560226L;

  private RuleFilter        filter;

  private int               offset;

  private int               limit;

  private Long              id;

  private String            title;

  private long              domainId;

  private Integer           context;

  public CacheKey(Integer context, int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
    this.context = context;
  }

  public CacheKey(Integer context, String title, int offset, int limit) {
    this.title = title;
    this.offset = offset;
    this.limit = limit;
    this.context = context;
  }

  public CacheKey(Integer context, long domainId, int offset, int limit) {
    this.domainId = domainId;
    this.offset = offset;
    this.limit = limit;
    this.context = context;
  }

  public CacheKey(Integer context, Long id) {
    this.id = id;
    this.context = context;
  }

  public CacheKey(Integer context, String title) {
    this.title = title;
    this.context = context;
  }

  public CacheKey(Integer context, RuleFilter filter, int offset, int limit) {
    this.context = context;
    this.filter = filter;
    this.offset = offset;
    this.limit = limit;
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

  public RuleFilter getFilter() {
    return filter;
  }

  public void setFilter(RuleFilter filter) {
    this.filter = filter;
  }

  public Integer getContext() {
    return context;
  }

  public void setContext(Integer context) {
    this.context = context;
  }

  public long getDomainId() {
    return domainId;
  }

  public void setDomainId(long domainId) {
    this.domainId = domainId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(context, domainId, id, filter, limit, offset, title);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CacheKey other = (CacheKey) obj;
    return Objects.equals(context, other.context) && domainId == other.domainId && Objects.equals(id, other.id)
        && Objects.equals(filter, other.filter) && limit == other.limit && offset == other.offset
        && Objects.equals(title, other.title);
  }

}
