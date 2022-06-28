package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.After;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.List;

public class TestRealizationsRest extends AbstractServiceTest {

    protected Class<?> getComponentClass() {
        return RealizationsRest.class;
    }

    protected static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                           // NOSONAR

    protected static final String fromDate = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));

    protected static final String toDate = Utils.toRFC3339Date(new Date(System.currentTimeMillis() + +MILLIS_IN_A_DAY));

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registry(getComponentClass());
        startSessionAs("root");
    }
    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testGetAllRealizations() {

        try {
            String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate + "&offset=-1&limit=10";
            EnvironmentContext envctx = new EnvironmentContext();
            HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));
            MultivaluedMap<String, String> h = new MultivaluedMapImpl();
            ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(400, response.getStatus());

            restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate + "&offset=0&limit=-10";
            httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(400, response.getStatus());

            restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate + "&offset=0&limit=10";
            httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
            List<GamificationActionsHistoryRestEntity> realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
            assertEquals(0, realizations.size());
            //add new realization
            newGamificationActionsHistory();
            response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
            realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
            assertEquals(1, realizations.size());
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetReport() {

        try {
            newGamificationActionsHistory();
            newGamificationActionsHistory();
            String restPath = "/gamification/realizations/api/getExport?fromDate=" + fromDate + "&toDate=" + toDate;
            EnvironmentContext envctx = new EnvironmentContext();
            HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));
            MultivaluedMap<String, String> h = new MultivaluedMapImpl();
            ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(200, response.getStatus());

        } catch (Exception e) {
        }
    }

    @Test
    public void testUpdateRealizations() {

        try {
            GamificationActionsHistoryDTO gHistory = newGamificationActionsHistoryDTO();
            String restPath = "/gamification/realizations/api/updateRealizations?realizationId=" + gHistory.getId() + "&status=" + HistoryStatus.EDITED + "&actionLabel=newLabel&points=100&domain=" + gHistory.getDomain();
            EnvironmentContext envctx = new EnvironmentContext();
            HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));
            MultivaluedMap<String, String> h = new MultivaluedMapImpl();
            ContainerResponse response = launcher.service("PUT", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
            GamificationActionsHistoryRestEntity realizations = (GamificationActionsHistoryRestEntity) response.getEntity();
            assertEquals(100, (long) realizations.getScore());
            assertEquals(HistoryStatus.EDITED.name(), realizations.getStatus());

            restPath = "/gamification/realizations/api/updateRealizations?realizationId=" + gHistory.getId() + "&status=" + HistoryStatus.REJECTED + "&actionLabel=&points=0&domain=";
            httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            response = launcher.service("PUT", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
            realizations = (GamificationActionsHistoryRestEntity) response.getEntity();
            assertEquals(HistoryStatus.REJECTED.name(), realizations.getStatus());
        } catch (Exception e) {
        }
    }
}
