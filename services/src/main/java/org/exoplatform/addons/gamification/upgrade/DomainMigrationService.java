/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.upgrade;


import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.exoplatform.commons.api.settings.SettingService;


import java.util.List;
import java.util.stream.Collectors;

public class DomainMigrationService implements Startable {

    private static final Log LOG = ExoLogger.getLogger(DomainMigrationService.class);
    public final static String GAMIFICATION_DATA_VERSION = "gamifictaionDataVertion";
    public final static int DOMAIN_MIGRATION_DATA_VERSION = 1;
    public final static int RULES_EVENT_MIGRATION_DATA_VERSION = 2;



    protected PortalContainer portalContainer;
    protected RuleService ruleService;
    protected DomainService domainService;
    protected BadgeService badgeService;
    protected GamificationHistoryDAO gamificationHistoryDAO;
    protected DomainMapper domainMapper;
    protected SettingService settingService;


    public DomainMigrationService(PortalContainer portalContainer, RuleService ruleService, DomainService domainService, BadgeService badgeService, GamificationHistoryDAO gamificationHistoryDAO, DomainMapper domainMapper, SettingService settingService) {
        this.ruleService = ruleService;
        this.domainService = domainService;
        this.badgeService = badgeService;
        this.gamificationHistoryDAO = gamificationHistoryDAO;
        this.domainMapper = domainMapper;
        this.settingService = settingService;
        this.portalContainer = portalContainer;
    }

    @Override
    public void start() {
        if(getDataversion() < DOMAIN_MIGRATION_DATA_VERSION) {
            long startTime = System.currentTimeMillis();
            long glStartTime = System.currentTimeMillis();
            boolean entitiesToMigrate = false;
            boolean rulesmigrationDone=false;
            boolean badgesMigrationDone=false;
            boolean pointsMigrationDone=false;
            RequestLifeCycle.begin(this.portalContainer);
            try {
                LOG.info("=== Start migration of Rules");
                List<String> domains = ruleService.getDomainListFromRules();
                for (String title : domains) {
                    DomainDTO domainDTO = domainService.findDomainByTitle(title);
                    if (domainDTO != null) {
                        List<RuleDTO> rules = ruleService.getAllRulesByDomain(title);
                        if (rules != null && rules.size() > 0) {
                            for (RuleDTO rule : rules) {
                                if (rule.getDomainDTO() == null) {
                                    entitiesToMigrate = true;
                                    rule.setDomainDTO(domainDTO);
                                    ruleService.updateRule(rule);
                                }
                            }
                        }
                    }
                }
                rulesmigrationDone=true;
                List<RuleDTO> rules = ruleService.getAllRulesWithNullDomain();
                if (rules != null && rules.size() > 0) {
                    entitiesToMigrate = true;
                    for (RuleDTO rule : rules) {
                        DomainDTO domainDTO = domainService.findDomainByTitle(rule.getArea());
                        if (domainDTO != null) {
                            entitiesToMigrate = true;
                            rule.setDomainDTO(domainDTO);
                            ruleService.updateRule(rule);
                        }else{
                            rulesmigrationDone=false;
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                if (entitiesToMigrate) {
                    LOG.info("=== Migration of Rules is done in " + (endTime - startTime) + " ms");
                } else {
                    LOG.info("=== No rules to migrate");
                }

            } catch (Exception e) {
                LOG.error("Error when migration Rules ", e);
            } finally {
              RequestLifeCycle.end();
            }

            RequestLifeCycle.begin(this.portalContainer);
            try {
                LOG.info("=== Start migration of Badges");
                startTime = System.currentTimeMillis();
                entitiesToMigrate = false;
                List<String> domains = badgeService.getDomainListFromBadges();
                for (String title : domains) {
                    DomainDTO domainDTO = domainService.findDomainByTitle(title);
                    if (domainDTO != null) {
                        List<BadgeDTO> badges = badgeService.findBadgesByDomain(title);
                        if (badges != null && badges.size() > 0) {
                            for (BadgeDTO badge : badges) {
                                if (badge.getDomainDTO() == null) {
                                    entitiesToMigrate = true;
                                    badge.setDomainDTO(domainDTO);
                                    badgeService.updateBadge(badge);
                                }
                            }
                        }
                    }
                }
                badgesMigrationDone=true;
                List<BadgeDTO> badges = badgeService.getAllBadgesWithNullDomain();
                if (badges != null && badges.size() > 0) {
                    entitiesToMigrate = true;
                    for (BadgeDTO badge : badges) {
                        DomainDTO domainDTO = domainService.findDomainByTitle(badge.getDomain());
                        if (domainDTO != null) {
                            entitiesToMigrate = true;
                            badge.setDomainDTO(domainDTO);
                            badgeService.updateBadge(badge);
                        }else{
                            badgesMigrationDone=false;
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                if (entitiesToMigrate) {
                    LOG.info("=== Migration of Badges is done in " + (endTime - startTime) + " ms");
                } else {
                    LOG.info("=== No badges to migrate");
                }

            } catch (Exception e) {
                LOG.error("Error when migration badges ", e);
            } finally {
              RequestLifeCycle.end();
            }

            RequestLifeCycle.begin(this.portalContainer);
            try {
                LOG.info("=== Start migration of History");
                startTime = System.currentTimeMillis();
                entitiesToMigrate = false;
                List<String> domains = gamificationHistoryDAO.getDomainList();
                for (String title : domains) {
                    DomainDTO domainDTO = domainService.findDomainByTitle(title);
                    if (domainDTO != null) {
                        List<GamificationActionsHistory> points = gamificationHistoryDAO.getAllPointsByDomain(title);
                        if (points != null && points.size() > 0) {
                            for (GamificationActionsHistory point : points) {
                                if (point.getDomainEntity() == null) {
                                    entitiesToMigrate = true;
                                    point.setDomainEntity(domainMapper.domainDTOToDomain(domainDTO));
                                    gamificationHistoryDAO.update(point);
                                }
                            }
                        }
                    }
                }
                pointsMigrationDone=true;
                List<GamificationActionsHistory> points = gamificationHistoryDAO.getAllPointsWithNullDomain();
                if (points != null && points.size() > 0) {
                    for (GamificationActionsHistory point : points) {
                        DomainDTO domainDTO = domainService.findDomainByTitle(point.getDomain());
                        if (domainDTO != null) {
                            entitiesToMigrate = true;
                            point.setDomainEntity(domainMapper.domainDTOToDomain(domainDTO));
                            gamificationHistoryDAO.update(point);
                        }else{
                            pointsMigrationDone=false;
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                if (entitiesToMigrate) {
                    LOG.info("=== Migration of Points is done in " + (endTime - startTime) + " ms");
                } else {
                    LOG.info("=== No Points to migrate");
                }

            } catch (Exception e) {
                LOG.error("Error when migration Points ", e);
            } finally {
              RequestLifeCycle.end();
            }
            if(badgesMigrationDone && rulesmigrationDone && pointsMigrationDone){
                setDataversion(DOMAIN_MIGRATION_DATA_VERSION);
                long endTime = System.currentTimeMillis();
                LOG.info("=== Domains Migration done in " + (endTime - glStartTime) + " ms");
            }
        }

        if(getDataversion() < RULES_EVENT_MIGRATION_DATA_VERSION) {
            long startTime = System.currentTimeMillis();

            try {
                LOG.info("=== Start migration of Rules");
                boolean rulesmigrationDone=false;
                List<RuleDTO> rules = ruleService.getAllRules();
                if (rules != null && rules.size() > 0) {
                    List<RuleDTO> noEvents = rules.stream().filter(rule -> rule.getEvent()==null).collect(Collectors.toList());
                    if(noEvents.size() == 0) {
                        rulesmigrationDone = true;
                    }else{
                        for(RuleDTO rule: noEvents){
                            rule.setEvent(rule.getTitle());
                            ruleService.updateRule(rule);
                        }
                        rules = ruleService.getAllRules();
                        noEvents = rules.stream().filter(rule -> rule.getEvent()==null).collect(Collectors.toList());
                        if(noEvents.size() == 0) {
                            rulesmigrationDone = true;
                        }
                    }

                }
                long endTime = System.currentTimeMillis();
                if (rulesmigrationDone) {
                    setDataversion(RULES_EVENT_MIGRATION_DATA_VERSION);
                    LOG.info("=== Migration of Rules is done in " + (endTime - startTime) + " ms");
                } else {
                    LOG.info("=== No rules to migrate");
                }

            } catch (Exception e) {
                LOG.error("Error when migration Rules ", e);
            }
        }
    }

    @Override
    public void stop() {

    }

    private int getDataversion(){
        SettingValue settingValue = settingService.get(Context.GLOBAL, Scope.GLOBAL, GAMIFICATION_DATA_VERSION);
        if (settingValue != null) {
            return Integer.parseInt(settingValue.getValue().toString());
        } else return 0;
    }

    private void setDataversion(int version){
        settingService.set(Context.GLOBAL, Scope.GLOBAL, GAMIFICATION_DATA_VERSION,
                new SettingValue<Object>(String.valueOf(version)));
    }

    }