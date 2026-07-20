/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.mcp;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.mcp.model.AnnouncementModel;
import io.meeds.gamification.mcp.model.CampaignModel;
import io.meeds.gamification.mcp.model.LeaderboardEntryModel;
import io.meeds.gamification.mcp.model.QuestModel;
import io.meeds.gamification.mcp.model.RealizationModel;
import io.meeds.gamification.mcp.model.ScoreModel;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProfileReputation;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;
import io.meeds.mcp.server.plugin.McpToolPlugin;

/**
 * MCP tools exposing the Meeds Gamification add-on to the AI agent (EVA). The
 * product's UI vocabulary is used: a <b>campaign</b> is a gamification program
 * and a <b>quest</b> is a gamification action/rule inside a campaign. Every
 * method acts as the current user, so program ownership, rewarding-admin rights
 * and rule visibility are enforced by the underlying services.
 */
@Service
@Profile("mcp-server")
public class GamificationMcpTool implements McpToolPlugin {

  private static final int          DEFAULT_LIST_LIMIT = 10;

  private static final int          MAX_LIST_LIMIT     = 50;

  private final ProgramService      programService;

  private final RuleService         ruleService;

  private final RealizationService  realizationService;

  private final AnnouncementService announcementService;

  private final IdentityManager     identityManager;

  public GamificationMcpTool(ProgramService programService,
                             RuleService ruleService,
                             RealizationService realizationService,
                             AnnouncementService announcementService,
                             IdentityManager identityManager) {
    this.programService = programService;
    this.ruleService = ruleService;
    this.realizationService = realizationService;
    this.announcementService = announcementService;
    this.identityManager = identityManager;
  }

  // ---------------------------------------------------------------- READS ----

  /**
   * Lists the campaigns (gamification programs) visible to the current user,
   * optionally filtered by a search term and/or a space.
   */
  public List<CampaignModel> listCampaigns(String term,
                                           Long spaceId,
                                           Integer limit,
                                           Integer offset) throws IllegalAccessException {
    // Mirror ProgramRest#getPrograms: always build the filter with
    // allSpaces=false (the no-arg ProgramFilter default) so the DAO keeps the
    // "(audienceId IS NULL OR visibility = OPEN)" visibility restriction and
    // the service scopes to the caller's member spaces. Passing allSpaces=true
    // (the previous spaceId==null default) would leak RESTRICTED programs from
    // spaces the caller is not a member of.
    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ALL);
    if (StringUtils.isNotBlank(term)) {
      filter.setProgramTitle(term.trim());
    }
    if (spaceId != null && spaceId > 0) {
      filter.setSpacesIds(List.of(spaceId));
    }
    List<ProgramDTO> programs = programService.getPrograms(filter, getCurrentUserName(), clampOffset(offset), clampLimit(limit));
    List<CampaignModel> result = new ArrayList<>();
    for (ProgramDTO program : programs) {
      result.add(toCampaign(program));
    }
    return result;
  }

  /**
   * Returns a single campaign (gamification program) by its id.
   */
  public CampaignModel getCampaign(Long campaignId) throws IllegalAccessException, ObjectNotFoundException {
    return toCampaign(resolveProgram(campaignId));
  }

  /**
   * Lists the quests (gamification actions/rules) of a campaign, or across all
   * accessible campaigns when no campaign id is given.
   */
  public List<QuestModel> listQuests(Long campaignId,
                                     String type,
                                     String status,
                                     Integer limit,
                                     Integer offset) {
    // Mirror RuleRest#getRules: always build the filter with allSpaces=false
    // (the no-arg RuleFilter default) so the DAO keeps the "(audienceId IS NULL
    // OR visibility = OPEN)" visibility restriction and the service scopes to
    // the caller's member spaces. Passing allSpaces=true (the previous
    // campaignId==null default) would leak RESTRICTED quests from spaces the
    // caller is not a member of.
    RuleFilter filter = new RuleFilter();
    if (campaignId != null && campaignId > 0) {
      filter.setProgramId(campaignId);
    }
    filter.setType(parseFilterType(type));
    filter.setStatus(parseStatusType(status));
    List<RuleDTO> rules = ruleService.getRules(filter, getCurrentUserName(), clampOffset(offset), clampLimit(limit));
    List<QuestModel> result = new ArrayList<>();
    for (RuleDTO rule : rules) {
      result.add(toQuest(rule));
    }
    return result;
  }

  /**
   * Returns a single quest (gamification action/rule) by its id.
   */
  public QuestModel getQuest(Long questId) throws IllegalAccessException, ObjectNotFoundException {
    return toQuest(resolveRule(questId));
  }

  /**
   * Returns the current user's gamification score: the overall total, the
   * overall leaderboard rank and the score per campaign. When a campaign id is
   * given, the rank and score for that campaign are added.
   */
  public ScoreModel getMyGamificationScore(Long campaignId) {
    long identityId = currentUserIdentityId();
    String earnerId = String.valueOf(identityId);
    long total = realizationService.getScoreByIdentityId(earnerId);
    List<ProfileReputation> perProgram = realizationService.getScorePerProgramByIdentityId(earnerId);
    List<ScoreModel.CampaignScore> scores = new ArrayList<>();
    Long campaignScore = null;
    if (perProgram != null) {
      for (ProfileReputation reputation : perProgram) {
        scores.add(new ScoreModel.CampaignScore(reputation.getProgramId(), reputation.getScore()));
        if (campaignId != null && reputation.getProgramId() == campaignId) {
          campaignScore = reputation.getScore();
        }
      }
    }
    Long programId = (campaignId != null && campaignId > 0) ? campaignId : null;
    int rank = realizationService.getLeaderboardRank(identityId, null, null, null, programId);
    return new ScoreModel(identityId, total, rank, programId, campaignScore, scores);
  }

  /**
   * Returns the gamification leaderboard (top earners), optionally scoped to a
   * campaign and/or a space, for a given period.
   */
  public List<LeaderboardEntryModel> getGamificationLeaderboard(Long campaignId,
                                                                Long spaceId,
                                                                String period,
                                                                String identityType,
                                                                Integer limit) throws IllegalAccessException {
    LeaderboardFilter filter = new LeaderboardFilter();
    if (campaignId != null && campaignId > 0) {
      filter.setProgramId(campaignId);
    }
    if (spaceId != null && spaceId > 0) {
      filter.setSpaceId(spaceId);
    }
    if (StringUtils.isNotBlank(period)) {
      try {
        filter.setPeriod(period.trim());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid period '" + period + "'. Allowed values are: WEEK, MONTH, YEAR, ALL.");
      }
    }
    if (StringUtils.isNotBlank(identityType)) {
      filter.setIdentityType(parseIdentityType(identityType));
    }
    filter.setLimit(clampLimit(limit));
    List<StandardLeaderboard> leaderboard = realizationService.getLeaderboard(filter, getCurrentUserName());
    List<LeaderboardEntryModel> result = new ArrayList<>();
    int rank = 1;
    if (leaderboard != null) {
      for (StandardLeaderboard entry : leaderboard) {
        result.add(new LeaderboardEntryModel(rank++, parseLong(entry.getEarnerId()), entry.getReputationScore()));
      }
    }
    return result;
  }

  /**
   * Lists the current user's own realizations (gamification achievements),
   * optionally filtered by date range, campaign and/or status.
   */
  public List<RealizationModel> listMyRealizations(String fromDate,
                                                   String toDate,
                                                   Long campaignId,
                                                   String status,
                                                   Integer limit,
                                                   Integer offset) throws IllegalAccessException {
    RealizationFilter filter = new RealizationFilter();
    filter.setEarnerIds(List.of(String.valueOf(currentUserIdentityId())));
    filter.setEarnerType(IdentityType.USER);
    filter.setFromDate(parseDate(fromDate, "from_date"));
    filter.setToDate(parseDate(toDate, "to_date"));
    if (campaignId != null && campaignId > 0) {
      filter.setProgramIds(List.of(campaignId));
    }
    if (StringUtils.isNotBlank(status)) {
      filter.setStatuses(List.of(parseRealizationStatus(status)));
    }
    List<RealizationDTO> realizations = realizationService.getRealizationsByFilter(filter,
                                                                                   getCurrentUserAclIdentity(),
                                                                                   clampOffset(offset),
                                                                                   clampLimit(limit));
    List<RealizationModel> result = new ArrayList<>();
    for (RealizationDTO realization : realizations) {
      result.add(toRealization(realization));
    }
    return result;
  }

  /**
   * Lists the current user's own quest announcements (challenges they
   * announced), most recent first. Optionally scoped to a single quest. Each
   * entry exposes the announcement id needed by cancel_quest_announcement. This
   * only ever returns the current user's own announcements.
   */
  public List<AnnouncementModel> listMyAnnouncements(Long questId,
                                                     Integer limit,
                                                     Integer offset) throws IllegalAccessException {
    RealizationFilter filter = new RealizationFilter();
    filter.setEarnerIds(List.of(String.valueOf(currentUserIdentityId())));
    filter.setEarnerType(IdentityType.USER);
    if (questId != null && questId > 0) {
      filter.setRuleIds(List.of(questId));
    }
    List<RealizationDTO> realizations = realizationService.getRealizationsByFilter(filter,
                                                                                   getCurrentUserAclIdentity(),
                                                                                   clampOffset(offset),
                                                                                   clampLimit(limit));
    List<AnnouncementModel> result = new ArrayList<>();
    for (RealizationDTO realization : realizations) {
      // Only manual-quest realizations with an author are announcements;
      // automatic point awards have no creator and are excluded.
      if (realization.getCreator() != null) {
        result.add(toAnnouncement(realization));
      }
    }
    return result;
  }

  // --------------------------------------------------------------- WRITES ----

  /**
   * Creates a new campaign (gamification program). Requires rewarding-admin
   * rights, or space-manager rights when a space is targeted.
   */
  public CampaignModel createCampaign(String title,
                                      String description,
                                      Long spaceId,
                                      Long budget,
                                      Boolean open,
                                      String visibility) throws IllegalAccessException, ObjectNotFoundException {
    if (StringUtils.isBlank(title)) {
      throw new IllegalArgumentException("title is required. A campaign must have a title.");
    }
    String currentUser = getCurrentUserName();
    boolean allowed = (spaceId != null && spaceId > 0) ? programService.canAddProgram(currentUser, spaceId)
                                                       : programService.canAddProgram(currentUser);
    if (!allowed) {
      throw new IllegalStateException("You need rewarding-admin rights (or to be a manager of the target space) to create a campaign.");
    }
    boolean isOpen = open == null || open;
    ProgramDTO program = new ProgramDTO();
    program.setTitle(title.trim());
    program.setDescription(StringUtils.trimToNull(description));
    if (spaceId != null && spaceId > 0) {
      program.setSpaceId(spaceId);
    }
    program.setBudget(budget == null ? 0 : budget);
    program.setOpen(isOpen);
    program.setEnabled(true);
    program.setVisibility(parseVisibility(visibility, spaceId));
    program.setOwnerIds(new HashSet<>(Set.of(currentUserIdentityId())));
    try {
      return toCampaign(programService.createProgram(program, getCurrentUserAclIdentity()));
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You need rewarding-admin rights (or to be a manager of the target space) to create a campaign.");
    }
  }

  /**
   * Creates a new quest (gamification action/rule) inside a campaign. Only
   * MANUAL quests (challenges the user announces) are supported; AUTOMATIC
   * (event-triggered) quests are rejected. Requires ownership of the campaign.
   */
  public QuestModel createQuest(Long campaignId,
                                String title,
                                String description,
                                Integer score,
                                String type,
                                String startDate,
                                String endDate,
                                String recurrence) throws IllegalAccessException, ObjectNotFoundException {
    if (campaignId == null || campaignId <= 0) {
      throw new IllegalArgumentException("campaign_id is required. Resolve it with list_campaigns.");
    }
    if (StringUtils.isBlank(title)) {
      throw new IllegalArgumentException("title is required. A quest must have a title.");
    }
    if (score == null || score <= 0) {
      throw new IllegalArgumentException("score is required and must be a positive number of points.");
    }
    EntityType questType = parseEntityType(type);
    if (questType != EntityType.MANUAL) {
      throw new IllegalStateException("AUTOMATIC quests require an event trigger — not yet supported via chat."
          + " Create a MANUAL quest (a challenge the user announces) instead.");
    }
    String currentUser = getCurrentUserName();
    ProgramDTO program = resolveProgram(campaignId);
    if (!canManageCampaign(program, currentUser)) {
      throw new IllegalStateException("You need to be an owner of this campaign to add a quest to it.");
    }
    RuleDTO rule = new RuleDTO();
    rule.setTitle(title.trim());
    rule.setDescription(StringUtils.trimToNull(description));
    rule.setScore(score);
    rule.setProgram(program);
    rule.setType(EntityType.MANUAL);
    rule.setEnabled(true);
    rule.setStartDate(parseRuleDate(startDate, "start_date"));
    rule.setEndDate(parseRuleDate(endDate, "end_date"));
    rule.setRecurrence(parseRecurrence(recurrence));
    try {
      return toQuest(ruleService.createRule(rule, currentUser));
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You need to be an owner of this campaign to add a quest to it.");
    }
  }

  /**
   * Updates an existing quest (gamification action/rule). Only the provided
   * fields are changed; the others keep their current values. Requires
   * ownership of the campaign the quest belongs to.
   */
  public QuestModel updateQuest(Long questId,
                                String title,
                                String description,
                                Integer score,
                                String startDate,
                                String endDate,
                                Boolean enabled,
                                String recurrence) throws IllegalAccessException, ObjectNotFoundException {
    if (questId == null || questId <= 0) {
      throw new IllegalArgumentException("quest_id is required. Resolve it with list_quests.");
    }
    String currentUser = getCurrentUserName();
    RuleDTO rule = resolveRule(questId);
    if (!ruleService.canEditRule(rule, currentUser)) {
      throw new IllegalStateException("You need to be an owner of this quest's campaign to edit it.");
    }
    if (StringUtils.isNotBlank(title)) {
      rule.setTitle(title.trim());
    }
    if (description != null) {
      rule.setDescription(StringUtils.trimToNull(description));
    }
    if (score != null && score > 0) {
      rule.setScore(score);
    }
    if (StringUtils.isNotBlank(startDate)) {
      rule.setStartDate(parseRuleDate(startDate, "start_date"));
    }
    if (StringUtils.isNotBlank(endDate)) {
      rule.setEndDate(parseRuleDate(endDate, "end_date"));
    }
    if (enabled != null) {
      rule.setEnabled(enabled);
    }
    if (StringUtils.isNotBlank(recurrence)) {
      rule.setRecurrence(parseRecurrence(recurrence));
    }
    try {
      return toQuest(ruleService.updateRule(rule, currentUser));
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You need to be an owner of this quest's campaign to edit it.");
    }
  }

  /**
   * Deletes a quest (gamification action/rule). Requires ownership of the
   * campaign the quest belongs to.
   */
  public String deleteQuest(Long questId) throws ObjectNotFoundException {
    if (questId == null || questId <= 0) {
      throw new IllegalArgumentException("quest_id is required. Resolve it with list_quests.");
    }
    try {
      ruleService.deleteRuleById(questId, getCurrentUserName());
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You need to be an owner of this quest's campaign to delete it.");
    } catch (ObjectNotFoundException e) {
      throw new ObjectNotFoundException("No quest found with id " + questId + ". It may have already been deleted.");
    }
    return "Quest " + questId + " has been deleted.";
  }

  /**
   * Announces (as the current user) that they completed a manual quest (a
   * challenge), so it can be reviewed and rewarded.
   */
  public AnnouncementModel announceQuest(Long questId,
                                         String comment) throws IllegalAccessException, ObjectNotFoundException {
    if (questId == null || questId <= 0) {
      throw new IllegalArgumentException("quest_id is required. Resolve it with list_quests.");
    }
    Announcement announcement = new Announcement();
    announcement.setChallengeId(questId);
    announcement.setComment(StringUtils.trimToNull(comment));
    try {
      Announcement created = announcementService.createAnnouncement(announcement, new HashMap<>(), getCurrentUserName());
      return new AnnouncementModel(created.getId(),
                                   created.getChallengeId(),
                                   created.getComment(),
                                   created.getActivityId(),
                                   created.getCreator(),
                                   created.getCreatedDate());
    } catch (IllegalStateException e) {
      throw new IllegalStateException("This quest is not a manual challenge, so it cannot be announced."
          + " Only MANUAL quests can be announced; automatic quests are rewarded by the platform.");
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You cannot announce this quest. It may be disabled, over, or you may not be"
          + " a member of the campaign's space.");
    } catch (ObjectNotFoundException e) {
      throw new ObjectNotFoundException("No quest found with id " + questId + ".");
    }
  }

  /**
   * Cancels (deletes) a quest announcement previously made by the current user.
   */
  public String cancelQuestAnnouncement(Long announcementId) throws ObjectNotFoundException {
    if (announcementId == null || announcementId <= 0) {
      throw new IllegalArgumentException("announcement_id is required and must be a positive number.");
    }
    try {
      announcementService.deleteAnnouncement(announcementId, getCurrentUserName());
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You can only cancel an announcement that you made yourself.");
    } catch (ObjectNotFoundException e) {
      throw new ObjectNotFoundException("No announcement found with id " + announcementId + ". It may have already been cancelled.");
    }
    return "Announcement " + announcementId + " has been cancelled.";
  }

  // -------------------------------------------------------------- HELPERS ----

  private ProgramDTO resolveProgram(Long campaignId) throws IllegalAccessException, ObjectNotFoundException {
    if (campaignId == null || campaignId <= 0) {
      throw new IllegalArgumentException("campaign_id is required. Resolve it with list_campaigns.");
    }
    try {
      return programService.getProgramById(campaignId, getCurrentUserName());
    } catch (ObjectNotFoundException e) {
      throw new ObjectNotFoundException("No campaign found with id " + campaignId + ". Resolve it with list_campaigns.");
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You are not allowed to view this campaign.");
    }
  }

  private RuleDTO resolveRule(Long questId) throws IllegalAccessException, ObjectNotFoundException {
    if (questId == null || questId <= 0) {
      throw new IllegalArgumentException("quest_id is required. Resolve it with list_quests.");
    }
    try {
      return ruleService.findRuleById(questId, getCurrentUserName());
    } catch (ObjectNotFoundException e) {
      throw new ObjectNotFoundException("No quest found with id " + questId + ". Resolve it with list_quests.");
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You are not allowed to view this quest.");
    }
  }

  private boolean canManageCampaign(ProgramDTO program, String username) {
    return programService.isProgramOwner(program.getId(), username) || programService.canEditProgram(program.getId(), username);
  }

  private long currentUserIdentityId() {
    org.exoplatform.social.core.identity.model.Identity identity =
        identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, getCurrentUserName());
    if (identity == null) {
      throw new IllegalStateException("Could not resolve the current user's identity.");
    }
    return Long.parseLong(identity.getId());
  }

  private CampaignModel toCampaign(ProgramDTO program) {
    return new CampaignModel(program.getId(),
                             program.getTitle(),
                             program.getDescription(),
                             program.getSpaceId(),
                             program.getBudget(),
                             program.isOpen(),
                             program.isEnabled(),
                             program.getVisibility() == null ? null : program.getVisibility().name());
  }

  private QuestModel toQuest(RuleDTO rule) {
    ProgramDTO program = rule.getProgram();
    return new QuestModel(rule.getId() == null ? 0 : rule.getId(),
                          rule.getTitle(),
                          rule.getDescription(),
                          rule.getScore(),
                          rule.getType() == null ? null : rule.getType().name(),
                          rule.getStartDate(),
                          rule.getEndDate(),
                          rule.isEnabled(),
                          rule.getRecurrence() == null ? null : rule.getRecurrence().name(),
                          program == null ? 0 : program.getId(),
                          program == null ? null : program.getTitle(),
                          rule.getPrerequisiteRuleIds());
  }

  private RealizationModel toRealization(RealizationDTO realization) {
    return new RealizationModel(realization.getId() == null ? 0 : realization.getId(),
                                realization.getRuleId(),
                                realization.getActionTitle(),
                                realization.getProgram() == null ? 0 : realization.getProgram().getId(),
                                realization.getActionScore(),
                                realization.getStatus(),
                                realization.getCreatedDate());
  }

  private AnnouncementModel toAnnouncement(RealizationDTO realization) {
    return new AnnouncementModel(realization.getId() == null ? 0 : realization.getId(),
                                 realization.getRuleId(),
                                 realization.getComment(),
                                 realization.getActivityId(),
                                 realization.getCreator(),
                                 realization.getCreatedDate());
  }

  private EntityVisibility parseVisibility(String visibility, Long spaceId) {
    if (StringUtils.isBlank(visibility)) {
      return (spaceId != null && spaceId > 0) ? EntityVisibility.RESTRICTED : EntityVisibility.OPEN;
    }
    try {
      return EntityVisibility.valueOf(visibility.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid visibility '" + visibility + "'. Allowed values are: OPEN, RESTRICTED.");
    }
  }

  private EntityType parseEntityType(String type) {
    if (StringUtils.isBlank(type)) {
      return EntityType.MANUAL;
    }
    try {
      return EntityType.valueOf(type.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid quest type '" + type + "'. Allowed values are: MANUAL, AUTOMATIC.");
    }
  }

  private EntityFilterType parseFilterType(String type) {
    if (StringUtils.isBlank(type)) {
      return EntityFilterType.ALL;
    }
    try {
      return EntityFilterType.valueOf(type.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid quest type '" + type + "'. Allowed values are: MANUAL, AUTOMATIC, ALL.");
    }
  }

  private EntityStatusType parseStatusType(String status) {
    if (StringUtils.isBlank(status)) {
      return EntityStatusType.ALL;
    }
    try {
      return EntityStatusType.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid status '" + status + "'. Allowed values are: ENABLED, DISABLED, ALL.");
    }
  }

  private RealizationStatus parseRealizationStatus(String status) {
    try {
      return RealizationStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid status '" + status
          + "'. Allowed values are: ACCEPTED, REJECTED, CANCELED, PENDING.");
    }
  }

  private IdentityType parseIdentityType(String identityType) {
    try {
      return IdentityType.valueOf(identityType.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid identity_type '" + identityType + "'. Allowed values are: USER, SPACE.");
    }
  }

  private RecurrenceType parseRecurrence(String recurrence) {
    if (StringUtils.isBlank(recurrence)) {
      return RecurrenceType.NONE;
    }
    try {
      return RecurrenceType.valueOf(recurrence.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid recurrence '" + recurrence
          + "'. Allowed values are: NONE, ONCE, DAILY, WEEKLY, MONTHLY.");
    }
  }

  /**
   * Parses a yyyy-MM-dd (or full ISO date-time) string into an RFC-3339 date
   * string as stored on rules. Returns null when blank.
   */
  private String parseRuleDate(String date, String field) {
    if (StringUtils.isBlank(date)) {
      return null;
    }
    String trimmed = date.trim();
    if (trimmed.contains("T")) {
      return trimmed;
    }
    validateIsoDate(trimmed, field);
    return trimmed + "T00:00:00";
  }

  private Date parseDate(String date, String field) {
    if (StringUtils.isBlank(date)) {
      return null;
    }
    String trimmed = date.trim();
    String isoDate = trimmed.contains("T") ? trimmed.substring(0, 10) : trimmed;
    validateIsoDate(isoDate, field);
    return Date.from(LocalDate.parse(isoDate).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private void validateIsoDate(String date, String field) {
    try {
      LocalDate.parse(date);
    } catch (Exception e) {
      throw new IllegalArgumentException(field + " must be a date in the yyyy-MM-dd format (e.g. 2026-01-31).");
    }
  }

  private int clampLimit(Integer limit) {
    if (limit == null || limit <= 0) {
      return DEFAULT_LIST_LIMIT;
    }
    return Math.min(limit, MAX_LIST_LIMIT);
  }

  private int clampOffset(Integer offset) {
    return (offset == null || offset < 0) ? 0 : offset;
  }

  private long parseLong(String value) {
    return StringUtils.isBlank(value) ? 0 : Long.parseLong(value.trim());
  }

}
