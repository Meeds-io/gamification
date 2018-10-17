package org.exoplatform.addons.gamification.migration.datamodel;

import org.exoplatform.addons.gamification.GamificationUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.addons.gamification.storage.dao.GamificationDAO;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.storage.dao.GamificationItemDAO;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;

import javax.servlet.ServletContext;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class DataModelMigration {

    private static final Log LOG = ExoLogger.getLogger(DataModelMigration.class);

    private static String MIGRATION_PROCESS_SOCIAL_DOMAIN = "Social";
    private static String MIGRATION_PROCESS_KNOWLEDGE_DOMAIN = "Knowledge";
    private static String MIGRATION_PROCESS_MIGRATION_SOCIAL_ENTRIES = "migrateSocialEntries";
    private static String MIGRATION_PROCESS_MIGRATION_KNOWLEDGE_ENTRIES = "migrateKnowledgeEntries";
    private static String MIGRATION_PROCESS_LABEL = "Gamification Migration Process";

    private GamificationDAO gamificationDAO;
    private GamificationHistoryDAO gamificationHistoryDAO;
    private SettingService settingService;

    public DataModelMigration(GamificationDAO gamificationDAO,
                              SettingService settingService,
                              GamificationHistoryDAO gamificationHistoryDAO) {
        this.gamificationDAO = gamificationDAO;
        this.settingService = settingService;
        this.gamificationHistoryDAO = gamificationHistoryDAO;
    }

    public void migrate() {
        PortalContainer.addInitTask(PortalContainer.getInstance().getPortalContext(), new RootContainer.PortalContainerPostInitTask() {
            @Override
            public void execute(ServletContext context, PortalContainer portalContainer) {
                GamificationUtils.getExecutorService().submit(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        RequestLifeCycle.begin(PortalContainer.getInstance());
                        if (!GamificationUtils.isGamificationDatamodelMigrated()) {
                            try {
                                ExoContainerContext.setCurrentContainer(PortalContainer.getInstance());
                                LOG.info("=== Start migration of Gamification data model");
                                long startTime = System.currentTimeMillis();
                                migrateOldDatamodelentries();
                                long endTime = System.currentTimeMillis();
                                LOG.info("=== Migration of Gamification data model is done in " + (endTime - startTime) + " ms");
                            } catch (Exception e) {
                                LOG.error("Error while migrating Gamification data model - Cause : " + e.getMessage(), e);
                            }
                        } else {
                            LOG.info("No Gamification entry data to migrate");
                        }
                        return null;
                    }
                });
            }
        });
    }

    private void migrateOldDatamodelentries() throws Exception {

        int current = 0;

        try {
            List<GamificationContextEntity> gamification = gamificationDAO.findAll();
            int totalEntries = 0;
            if (gamification != null) totalEntries = gamification.size();
            LOG.info("    Number of gamification entries to process = " + totalEntries);
            double userPercent = 0;
            for (GamificationContextEntity entry : gamification) {
                if (!GamificationUtils.isGamificationDatamodelMigrated(entry.getUsername())) {
                    migrateEntry(entry);
                    current++;
                    userPercent = ((double) current / totalEntries) * 100;
                }
                LOG.info("Migrate gamification entry service=gamification operation=migration parameters=\"user_social_id:{}, global_score:{}, progress:{}%\"", entry.getUsername(), entry.getScore(), (int)userPercent);

            }

            // Make migration done only when all records are OK
            GamificationUtils.setGamificationDatamodelMigrationDone();

        } catch (Exception e) {
            LOG.error("Error while migrating Gamification DataModel - Cause : " + e.getMessage(), e);
        }
    }

    private void migrateEntry(GamificationContextEntity entry) throws Exception {

        if (GamificationUtils.isBlackListed(CommonsUtils.getService(IdentityManager.class).getIdentity(entry.getUsername(), false).getRemoteId())) {
            return;
        }
        long socialScore;
        // Migrate only unblacked list users

        // Gamification App first rolling out version
        LocalDate gamificationRollOutDate = LocalDate.of(2018, Month.AUGUST, 24);

        // Build new social gamification action history
        GamificationActionsHistory actionsHistory = new GamificationActionsHistory();
        actionsHistory.setUserSocialId(entry.getUsername());
        socialScore = calculateSocialScore(entry.getScore());
        actionsHistory.setGlobalScore(socialScore);
        actionsHistory.setActionScore(socialScore);
        actionsHistory.setDomain(MIGRATION_PROCESS_SOCIAL_DOMAIN);
        actionsHistory.setActionTitle(MIGRATION_PROCESS_MIGRATION_SOCIAL_ENTRIES);
        actionsHistory.setDate(Date.from(gamificationRollOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        // Complete with metadata
        actionsHistory.setCreatedBy(MIGRATION_PROCESS_LABEL);
        actionsHistory.setLastModifiedBy(MIGRATION_PROCESS_LABEL);
        actionsHistory.setCreatedDate(new Date());
        actionsHistory.setLastModifiedDate(new Date());
        // Save new social entry
        gamificationHistoryDAO.create(actionsHistory);

        // Build new social gamification action history
        actionsHistory = new GamificationActionsHistory();
        actionsHistory.setUserSocialId(entry.getUsername());
        actionsHistory.setGlobalScore(entry.getScore());
        actionsHistory.setActionScore((entry.getScore()) - socialScore);
        actionsHistory.setDomain(MIGRATION_PROCESS_KNOWLEDGE_DOMAIN);
        actionsHistory.setActionTitle(MIGRATION_PROCESS_MIGRATION_KNOWLEDGE_ENTRIES);
        actionsHistory.setDate(Date.from(gamificationRollOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        // Complete with metadata
        actionsHistory.setCreatedBy(MIGRATION_PROCESS_LABEL);
        actionsHistory.setLastModifiedBy(MIGRATION_PROCESS_LABEL);
        actionsHistory.setCreatedDate(new Date());
        actionsHistory.setLastModifiedDate(new Date());
        // Save new social entry
        gamificationHistoryDAO.create(actionsHistory);
        settingService.set(Context.USER.id(entry.getUsername()), Scope.APPLICATION.id(GamificationUtils.GAMIFICATION_DATAMODEL_MIGRATION_USER_KEY), GamificationUtils.GAMIFICATION_DATAMODEL_MIGRATION_DONE, SettingValue.create("true"));
    }

    private long calculateSocialScore(long total) {
        long socialScore = 0;
        socialScore = total * 2 / 3;
        return socialScore;
    }
}
