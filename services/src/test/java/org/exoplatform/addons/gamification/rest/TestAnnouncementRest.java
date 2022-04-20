package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.*;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.json.JSONWriter;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TestAnnouncementRest extends AbstractServiceTest {

    protected Class<?> getComponentClass() {
        return AnnouncementRest.class;
    }

    private ChallengeService challengeService;

    private AnnouncementService announcementService;

    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                           // NOSONAR

    private static final String startDate = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));

    private static final String endDate = Utils.toRFC3339Date(new Date(System.currentTimeMillis() + MILLIS_IN_A_DAY));

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registry(getComponentClass());
        challengeService = CommonsUtils.getService(ChallengeService.class);
        announcementService = CommonsUtils.getService(AnnouncementService.class);
        ConversationState.setCurrent(null);
    }

    @Test
    public void testCreatAnnouncement() {

        try {
            startSessionAs("root1");
            DomainDTO domain = newDomainDTO();
            Challenge challenge = new Challenge(0,
                    "update challenge",
                    "challenge description",
                    1l,
                    startDate,
                    endDate,
                    Collections.emptyList(),
                    10L,
                    domain.getTitle());
            challenge = challengeService.createChallenge(challenge);
            String restPath = "/gamification/announcement/api/addAnnouncement";
            EnvironmentContext envctx = new EnvironmentContext();
            HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));
            StringWriter writer = new StringWriter();
            JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter.object()
                    .key("id")
                    .value("0")
                    .key("challengeId")
                    .value(challenge.getId())
                    .key("assignee")
                    .value("1")
                    .key("comment")
                    .value("announcement comment")
                    .key("creator")
                    .value("root1")
                    .key("createdDate")
                    .value(startDate)
                    .endObject();
            byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
            MultivaluedMap<String, String> h = new MultivaluedMapImpl();
            h.putSingle("content-type", "application/json");
            h.putSingle("content-length", "" + data.length);
            ContainerResponse response = launcher.service("POST", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(400, response.getStatus());
            ConversationState.setCurrent(null);
            response = launcher.service("POST", restPath, "", h, data, envctx);
            assertNotNull(response);
            assertEquals(401, response.getStatus());


            startSessionAs("root2");
            response = launcher.service("POST", restPath, "", h, data, envctx);
            assertNotNull(response);
            assertEquals(403, response.getStatus());

            startSessionAs("root1");
            response = launcher.service("POST", restPath, "", h, data, envctx);
            assertNotNull(response);
            assertEquals(200, response.getStatus());

            writer = new StringWriter();
            jsonWriter = new JSONWriter(writer);
            jsonWriter.object()
                    .key("id")
                    .value("100")
                    .key("challengeId")
                    .value(1000)
                    .key("assignee")
                    .value("1")
                    .key("comment")
                    .value("announcement comment")
                    .key("creator")
                    .value("root1")
                    .key("createdDate")
                    .value(startDate)
                    .endObject();
            data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
            h = new MultivaluedMapImpl();
            h.putSingle("content-type", "application/json");
            h.putSingle("content-length", "" + data.length);
            //test id != 0
            response = launcher.service("POST", restPath, "", h, data, envctx);
            assertNotNull(response);
            assertEquals(400, response.getStatus());

            //challenge do not exist
            writer = new StringWriter();
            jsonWriter = new JSONWriter(writer);
            jsonWriter.object()
                    .key("id")
                    .value("0")
                    .key("challengeId")
                    .value(1000)
                    .key("assignee")
                    .value("1")
                    .key("comment")
                    .value("announcement comment")
                    .key("creator")
                    .value("root1")
                    .key("createdDate")
                    .value(startDate)
                    .endObject();
            data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
            h = new MultivaluedMapImpl();
            h.putSingle("content-type", "application/json");
            h.putSingle("content-length", "" + data.length);
            response = launcher.service("POST", restPath, "", h, data, envctx);
            assertNotNull(response);
            assertEquals(404, response.getStatus());
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetAllAnnouncementByChallenge() {

        try {
            startSessionAs("root1");
            DomainDTO domain = newDomainDTO();
            Challenge challenge = new Challenge(0,
                    "update challenge",
                    "challenge description",
                    1l,
                    startDate,
                    endDate,
                    Collections.emptyList(),
                    10L,
                    domain.getTitle());
            challenge = challengeService.createChallenge(challenge, "root1");
            Announcement announcement = new Announcement(0,
                    challenge.getId(),
                    1L,
                    "announcement comment",
                    1L,
                    startDate,
                    null);
            announcementService.createAnnouncement(announcement, "root1", false);
            String restPath = "/gamification/announcement/api/ByChallengeId/1?offset=1&limit=-10";
            EnvironmentContext envctx = new EnvironmentContext();
            HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));

            MultivaluedMap<String, String> h = new MultivaluedMapImpl();
            ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(400, response.getStatus());

            restPath = "/gamification/announcement/api/ByChallengeId/1?offset=-1&limit=10";
            envctx = new EnvironmentContext();
            httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));
            response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(400, response.getStatus());

            restPath = "/gamification/announcement/api/ByChallengeId/-1?offset=1&limit=10";
            envctx = new EnvironmentContext();
            httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));
            response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(500, response.getStatus());

            restPath = "/gamification/announcement/api/ByChallengeId/" + challenge.getId() + "?offset=0&limit=10";
            envctx = new EnvironmentContext();
            httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
            envctx.put(HttpServletRequest.class, httpRequest);
            envctx.put(SecurityContext.class, new MockSecurityContext("root"));
            response = launcher.service("GET", restPath, "", h, null, envctx);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
            List<AnnouncementRestEntity> announcementRestEntityList = (List<AnnouncementRestEntity>) response.getEntity();
            assertEquals(1, announcementRestEntityList.size());

        } catch (Exception e) {
        }
    }


}
