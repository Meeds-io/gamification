package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.service.dto.configuration.ChallengeRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TestChallengeRest extends AbstractServiceTest {
  protected Class<?> getComponentClass() {
    return ChallengeRest.class;
  }

  private static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                                                            // NOSONAR

  private static final String startDate       = Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 3 * MILLIS_IN_A_DAY));

  private static final String endDate         = Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 2 * MILLIS_IN_A_DAY));

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    registry(getComponentClass());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testCreateChallenge() throws Exception {
    DomainDTO domain = newDomainDTO();
    String restPath = "/gamification/challenges/";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("0")
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());
    startSessionAs("root1");
    response = launcher.service("POST", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testUpdateChallenge() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    String restPath = "/gamification/challenges";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");
    restPath = "/gamification/challenges";
    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("0")
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    // null data
    response = launcher.service("PUT", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    // challenge id 0
    response = launcher.service("PUT", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(challengeRestEntity.getId())
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    // update with root 2
    response = launcher.service("PUT", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(challengeRestEntity.getId())
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    // update with root 2
    response = launcher.service("PUT", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");

    response = launcher.service("PUT", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("10")
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    // challenge not exists
    response = launcher.service("PUT", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void testGetChallengeById() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    String restPath = "/gamification/challenges";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");
    restPath = "/gamification/challenges/0";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    // challenge id 0
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/challenges/555";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    // challenge id 5 not exist
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    startSessionAs("root1");

    restPath = "/gamification/challenges/" + challengeRestEntity.getId();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity savedChallenge = (ChallengeRestEntity) response.getEntity();
    assertNotNull(savedChallenge);
    assertEquals(challengeRestEntity.getId(), savedChallenge.getId());
  }

  @Test
  public void testGetChallengesByUser() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    String restPath = "/gamification/challenges/";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");
    restPath = "/gamification/challenges?offset=-1&limit=10";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/challenges?offset=1&limit=-10";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/challenges?offset=0&limit=10";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<ChallengeRestEntity> savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(0, savedChallenges.size());

    startSessionAs("root1");

    restPath = "/gamification/challenges?offset=0&limit=10&domainId=" + domain.getId() + "&announcements=4";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(2, savedChallenges.size());

    restPath = "/gamification/challenges?offset=0&limit=10&domainId=0&announcements=4&groupByDomain=true";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(1, savedChallenges.size());

    restPath = "/gamification/challenges?offset=0&limit=10";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(2, savedChallenges.size());
  }

  @Test
  public void testDeleteChallenge() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    String restPath = "/gamification/challenges/";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");

    // challenge id 0
    restPath = "/gamification/challenges/0";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    response = launcher.service("DELETE", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    // challenge id not exist
    restPath = "/gamification/challenges/1100";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    response = launcher.service("DELETE", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    // unauthorized user
    restPath = "/gamification/challenges/" + challengeRestEntity.getId();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("DELETE", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");

    response = launcher.service("DELETE", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testCanAddChallenge() throws Exception {
    startSessionAs("root1");
    String restPath = "/gamification/challenges/canAddChallenge";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }
}
