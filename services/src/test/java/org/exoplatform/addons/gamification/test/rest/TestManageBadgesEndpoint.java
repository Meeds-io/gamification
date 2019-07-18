package org.exoplatform.addons.gamification.test.rest;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONWriter;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.rest.ManageBadgesEndpoint;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.wadl.research.HTTPMethods;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

public class TestManageBadgesEndpoint extends AbstractServiceTest {

  private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

  protected Class<?> getComponentClass() {
    return ManageBadgesEndpoint.class;
  }

  private void populateData() throws Exception {
    // make testdata
    for (int i = 0; i < 10; i++) {
      badgeDTO = new BadgeDTO();
      badgeService = new BadgeService();
      badgeDTO.setIconFileId(0);
      badgeDTO.setNeededScore(50);
      badgeDTO.setCreatedBy("root");
      badgeDTO.setDescription("description");
      badgeDTO.setIcon(null);
      badgeDTO.setCreatedDate("2019-05-22");
      badgeDTO.setLastModifiedBy("root");
      badgeDTO.setIcon(null);
      badgeDTO.setDomain("Knowledge");
      badgeDTO.setTitle("Knowledgeable" + i);
      badgeStorage.create(badgeMapper.badgeDTOToBadge(badgeDTO));
    }
  }

  /**
   * Testing the Status Code
   **/
  @Test
  public void testgetAllBadges() {

    try {
      startSessionAs("root");

      populateData();
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageBadgesEndpoint", ManageBadgesEndpoint.class);
      String restPath = "/gamification/badges/all";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));

      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);

      assertEquals(200, response.getStatus());

      LOG.info("List of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {
      LOG.error("Cannot get list of badges", e);
    }

  }

  // Then

  /**
   * Testing the add of a new badge with the Media Type
   **/
  @Test
  public void testAddBadge() {

    try {
      startSessionAs("root");
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageBadgesEndpoint", ManageBadgesEndpoint.class);
      String restPath = "/gamification/badges/add";
      EnvironmentContext envctx = new EnvironmentContext();
      registry(getComponentClass());

      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));

      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
                .key("title")
                .value("foo")
                .key("description")
                .value("description")
                .key("domain")
                .value("social")
                .endObject();

      byte[] data = writer.getBuffer().toString().getBytes("UTF-8");

      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
      assertNotNull(response);

      assertEquals(200, response.getStatus());

      BadgeDTO entity = (BadgeDTO) response.getEntity();
      assertEquals("foo", entity.getTitle());
      assertEquals("description", entity.getDescription());
      assertEquals("social", entity.getDomain());

      LOG.info("List of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {

      LOG.error("Cannot get list of badges", e);
    }

  }

  /**
   * Testing the add of delete of badge with the Media Type
   **/
  @Test
  public void testDeleteBadge() {
    try {
      startSessionAs("root");
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageBadgesEndpoint", ManageBadgesEndpoint.class);
      String restPath = "/gamification/badges/delete";
      EnvironmentContext envctx = new EnvironmentContext();
      registry(getComponentClass());

      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "DELETE", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));

      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
              .key("title")
              .value("foo")
              .key("description")
              .value("description")
              .key("domain")
              .value("social")
              .endObject();

      byte[] data = writer.getBuffer().toString().getBytes("UTF-8");

      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service("DELETE", restPath, "", h, data, envctx);
      assertNotNull(response);

      assertEquals(200, response.getStatus());

      BadgeDTO entity = (BadgeDTO) response.getEntity();
      assertEquals("foo", entity.getTitle());
      assertEquals("description", entity.getDescription());
      assertEquals("social", entity.getDomain());

      LOG.info("Delete of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {

      LOG.error("Cannot delete the list of badges", e);
    }

  }

  /**
   * Testing the add of delete of badge with the Media Type
   **/
  @Test
  public void testUpdateBadge() {

    try {
      startSessionAs("root");
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageBadgesEndpoint", ManageBadgesEndpoint.class);
      String restPath = "/gamification/badges/update";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, HTTPMethods.PUT.toString(), null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));

      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
                .key("title")
                .value("foo")
                .key("description")
                .value("description")
                .key("domain")
                .value("social")
                .endObject();

      byte[] data = writer.getBuffer().toString().getBytes("UTF-8");

      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, data, envctx);
      assertNotNull(response);

      assertEquals(200, response.getStatus());

      BadgeDTO entity = (BadgeDTO) response.getEntity();
      assertEquals("foo", entity.getTitle());
      assertEquals("description", entity.getDescription());
      assertEquals("social", entity.getDomain());

      LOG.info("List of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {

      LOG.error("Cannot get list of badges", e);
    }

  }

}
