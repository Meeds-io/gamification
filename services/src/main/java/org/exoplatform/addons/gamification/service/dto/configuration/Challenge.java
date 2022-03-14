package org.exoplatform.addons.gamification.service.dto.configuration;

import java.util.List;

public class Challenge implements Cloneable {
  private long       id;

  private String     title;

  private String     description;

  private long       audience;

  private String     startDate;

  private String     endDate;

  private List<Long> managers;

  private Long       points;

  private String     program;

  public Challenge(long id,
                   String title,
                   String description,
                   long audience,
                   String startDate,
                   String endDate,
                   List<Long> managers,
                   Long points,
                   String program) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.audience = audience;
    this.startDate = startDate;
    this.endDate = endDate;
    this.managers = managers;
    this.points = points;
    this.program = program;
  }

  @Override
  public Challenge clone() { // NOSONAR
    return new Challenge(id, title, description, audience, startDate, endDate, managers, points, program);
  }

  public Challenge() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public long getAudience() {
    return audience;
  }

  public void setAudience(long audience) {
    this.audience = audience;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public List<Long> getManagers() {
    return managers;
  }

  public void setManagers(List<Long> managers) {
    this.managers = managers;
  }

  public Long getPoints() {
    return points;
  }

  public void setPoints(Long points) {
    this.points = points;
  }

  public String getProgram() {
    return program;
  }

  public void setProgram(String program) {
    this.program = program;
  }
}
