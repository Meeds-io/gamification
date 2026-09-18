/**
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
package io.meeds.gamification.service;

import static org.junit.Assert.assertThrows;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.mock.MockUploadService;
import org.exoplatform.upload.UploadService;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.mock.SpaceServiceMock;
import io.meeds.gamification.model.ProgramColorAlreadyExists;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.test.AbstractServiceTest;

@SuppressWarnings("deprecation")
public class ProgramServiceTest extends AbstractServiceTest {

  private static final String INTERNAL_USER     = "internalUser";

  private static final String SPACE_MEMBER_USER = "root10";

  private static final String SPACE_MANGER_USER = "root5";

  private static final String ADMIN_USER        = "root1";

  private Identity            adminAclIdentity;

  private Identity            spaceMemberAclIdentity;

  private Identity            spaceManagerAclIdentity;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adminAclIdentity = registerAdministratorUser(ADMIN_USER);
    spaceMemberAclIdentity = registerInternalUser(SPACE_MEMBER_USER);
    spaceManagerAclIdentity = registerInternalUser(SPACE_MANGER_USER);
    registerInternalUser(INTERNAL_USER);
  }

  @Test
  public void testGetProgramsAsAdmin() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
  }

  @Test
  public void testGetProgramsAsInternalUser() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
  }

  @Test
  public void testGetOpenProgramsAsInternalUser() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, null, null);
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>(), null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>(), null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>(), null);
    assertEquals(4, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>(), null);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>(), null);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.getPrograms(filter, INTERNAL_USER, OFFSET, 10).size());
  }

  @Test
  public void testGetProgramsAsSpaceMemberUser() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
  }

  @Test
  public void testGetProgramsByFilter() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", true, new HashSet<>());
    filter.setOwnerId(1);
    assertEquals(1, programService.getPrograms(filter, ADMIN_USER, OFFSET, 10).size());
  }

  @Test
  public void testGetProgramsByOwner() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    assertEquals(0, programService.countPrograms(filter, SPACE_MEMBER_USER));

    ProgramEntity programEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    filter.setOwnerId(10);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    assertEquals(0, programService.countPrograms(filter, SPACE_MEMBER_USER));

    programEntity.setOwners(Collections.singleton(10l));
    programDAO.update(programEntity);

    assertEquals(1, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    assertEquals(1, programService.countPrograms(filter, SPACE_MEMBER_USER));
  }

  @Test
  public void testGetProgramsByAnonym() throws IllegalAccessException, ObjectNotFoundException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, null, OFFSET, 10).size());
    assertEquals(0, programService.countPrograms(filter, null));

    ProgramEntity programEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    assertEquals(0, programService.getPrograms(filter, null, OFFSET, 10).size());
    assertEquals(0, programService.countPrograms(filter, null));

    ProgramDTO program = programService.getProgramById(programEntity.getId());
    program.setSpaceId(Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    programService.updateProgram(program);

    assertEquals(1, programService.getPrograms(filter, null, OFFSET, 10).size());
    assertEquals(1, programService.countPrograms(filter, null));
  }

  @Test
  public void testGetProgramsBySpaceExcludeOpen() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, programService.getProgramIds(filter, 0, 10).size());
    ProgramEntity program1 = newDomain(EntityType.MANUAL, "program1", true, new HashSet<>());
    program1.setAudienceId(null);
    programDAO.update(program1);

    ProgramEntity program2 = newDomain(EntityType.MANUAL, "program2", true, new HashSet<>());
    program2.setAudienceId(2l);
    programDAO.update(program2);

    ProgramEntity program3 = newDomain(EntityType.AUTOMATIC, "program3", true, new HashSet<>());
    program3.setAudienceId(3l);
    programDAO.update(program3);

    ProgramEntity program4 = newDomain(EntityType.AUTOMATIC, "program4", true, new HashSet<>());
    assertEquals(1l, program4.getAudienceId().longValue());

    assertEquals(4, programService.getProgramIds(filter, 0, 10).size());

    filter.setSpacesIds(Collections.singletonList(1l));
    assertEquals(Arrays.asList(program4.getId(), program1.getId()), programService.getProgramIds(filter, 0, 10));

    filter.setExcludeOpen(true);
    assertEquals(Arrays.asList(program4.getId()), programService.getProgramIds(filter, 0, 10));
  }

  /**
   * The filter shape ProgramServiceImpl#computeUserSpaces produces for a caller
   * who is not a member of the requested space — no space left to filter on,
   * excludeOpen set — must still be a runnable query: ProgramDAO emitted its
   * "(audienceId IS NULL OR visibility = :openVisibility)" predicate without
   * binding the parameter, and the query died in the engine (EXO-90210).
   */
  @Test
  public void testGetProgramsExcludeOpenWithNoSpaceLeftToFilterOn() throws IllegalAccessException {
    ProgramEntity openProgram = newDomain(EntityType.MANUAL, "open-program", true, new HashSet<>());
    openProgram.setAudienceId(null);
    programDAO.update(openProgram);
    programStorage.clearCache();
    restartTransaction();

    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ALL);
    filter.setExcludeOpen(true);

    assertEquals(Collections.singletonList(openProgram.getId()), programService.getProgramIds(filter, 0, 10));

  }

  /**
   * "The programs of space X" asked by someone who shares none of the requested
   * spaces is answered with what that space shows to everyone — its OPEN
   * programs — and with nothing else: not the space's RESTRICTED programs, and
   * not every platform-wide program, which is what the audience-free predicate
   * used to answer (EXO-90210).
   */
  @Test
  public void testGetProgramsOfASpaceTheUserSharesNone() throws IllegalAccessException {
    long otherSpaceId = Long.parseLong(SpaceServiceMock.SPACE_ID_2);
    ProgramEntity spaceOpenProgram = newDomain(EntityType.MANUAL, "other-space-open", true, new HashSet<>(), otherSpaceId);
    spaceOpenProgram.setVisibility(EntityVisibility.OPEN);
    programDAO.update(spaceOpenProgram);
    ProgramEntity spaceRestrictedProgram = newDomain(EntityType.MANUAL,
                                                     "other-space-restricted",
                                                     true,
                                                     new HashSet<>(),
                                                     otherSpaceId);
    spaceRestrictedProgram.setVisibility(EntityVisibility.RESTRICTED);
    programDAO.update(spaceRestrictedProgram);
    ProgramEntity platformWide = newDomain(EntityType.MANUAL, "platform-wide-program", true, new HashSet<>());
    platformWide.setAudienceId(null);
    platformWide.setVisibility(EntityVisibility.OPEN);
    programDAO.update(platformWide);
    programStorage.clearCache();
    restartTransaction();

    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ALL);
    filter.setSpacesIds(Collections.singletonList(otherSpaceId));
    filter.setExcludeOpen(true);

    // SPACE_MEMBER_USER is a member of space 1 only, so it shares none of the
    // requested spaces: it sees that space's open program, not its restricted
    // one, and not the platform-wide one.
    List<Long> asNonMember = programService.getProgramIds(filter, SPACE_MEMBER_USER, 0, 10);
    assertEquals(Collections.singletonList(spaceOpenProgram.getId()), asNonMember);
    assertEquals(1, programService.countPrograms(filter, SPACE_MEMBER_USER));

    // A rewarding manager is not narrowed to their own spaces, so the same
    // question keeps its full answer for them — both of that space's programs,
    // and still not the platform-wide one.
    List<Long> asAdmin = programService.getProgramIds(filter, ADMIN_USER, 0, 10);
    assertEquals(2, asAdmin.size());
    assertTrue(asAdmin.containsAll(List.of(spaceOpenProgram.getId(), spaceRestrictedProgram.getId())));
    assertFalse("a platform-wide program is not a program of that space", asAdmin.contains(platformWide.getId()));
  }

  @Test
  public void testCountProgramsByOwner() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, OFFSET, 10).size());
    ProgramEntity domainEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    filter.setOwnerId(10);
    assertEquals(0, programService.countPrograms(filter, SPACE_MEMBER_USER));

    domainEntity.setOwners(Collections.singleton(10l));
    programDAO.update(domainEntity);

    assertEquals(1, programService.countPrograms(filter, SPACE_MEMBER_USER));
  }

  @Test
  public void testCountPrograms() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.countPrograms(filter, ADMIN_USER));
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.countPrograms(filter, ADMIN_USER));

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.countPrograms(filter, ADMIN_USER));
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.countPrograms(filter, ADMIN_USER));
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.countPrograms(filter, ADMIN_USER));
  }

  @Test
  public void testCreateProgram() throws IllegalAccessException { // NOSONAR
    assertEquals(0, programDAO.count().longValue());
    ProgramDTO program = new ProgramDTO();
    program.setTitle(GAMIFICATION_DOMAIN);
    program.setDescription("Description");
    program.setDeleted(false);
    program.setEnabled(true);
    program.setBudget(20L);
    program.setColor("#FFEECCDD");
    assertThrows(IllegalArgumentException.class, () -> programService.createProgram(null, adminAclIdentity));
    ProgramDTO domainWithId = new ProgramDTO();
    domainWithId.setId(150L);
    assertThrows(IllegalArgumentException.class, () -> programService.createProgram(domainWithId, adminAclIdentity));
    program = programService.createProgram(program, adminAclIdentity);
    assertNotNull(program);
    assertEquals(EntityType.MANUAL.name(), program.getType());
    assertNotNull(programDAO.find(program.getId()));

    ProgramDTO programToSave = new ProgramDTO();
    programToSave.setTitle(GAMIFICATION_DOMAIN);
    programToSave.setDescription("Description");
    programToSave.setDeleted(false);
    programToSave.setEnabled(true);
    programToSave.setBudget(20L);
    programToSave.setColor("#FFEECCdd");
    programToSave.setOwnerIds(Collections.emptySet());
    programToSave.setCoverFileId(1L);
    programToSave.setAvatarFileId(2L);
    programToSave.setType(EntityType.MANUAL.name());
    assertThrows(IllegalAccessException.class, () -> programService.createProgram(programToSave, spaceMemberAclIdentity));
    assertThrows(ProgramColorAlreadyExists.class, () -> programService.createProgram(programToSave, adminAclIdentity));

    programToSave.setColor("#FFEECCEE");
    ProgramDTO savedDomain = programService.createProgram(programToSave, adminAclIdentity);
    assertNotNull(savedDomain);
    savedDomain = programService.getProgramById(savedDomain.getId());
    assertEquals(GAMIFICATION_DOMAIN, programToSave.getTitle());
    assertEquals("Description", programToSave.getDescription());
    assertEquals(adminAclIdentity.getUserId(), programToSave.getCreatedBy());
    assertEquals(adminAclIdentity.getUserId(), programToSave.getLastModifiedBy());
    assertFalse(programToSave.isDeleted());
    assertTrue(programToSave.isEnabled());
    assertEquals(20L, programToSave.getBudget());
    assertEquals(1L, programToSave.getCoverFileId());
    assertEquals(2L, programToSave.getAvatarFileId());
    assertEquals("#FFEECCEE", programToSave.getColor());
    assertEquals(EntityType.MANUAL.name(), savedDomain.getType());
    assertNotNull(programToSave.getCreatedDate());
    assertNotNull(programToSave.getLastModifiedDate());

    assertThrows(ProgramColorAlreadyExists.class, () -> programService.createProgram(programToSave, adminAclIdentity));
    programToSave.setColor("#FFEECCBB");

    program = programService.createProgram(programToSave);
    assertNotNull(program);
    assertEquals(EntityType.AUTOMATIC.name(), program.getType());
    assertEquals("#FFEECCBB", program.getColor());
  }

  /**
   * The service, not its callers, decides a program's audience and visibility:
   * an "open" program has its audience reset to 0 and is visible to everyone,
   * so a program meant for a space must never be created open — the
   * create_campaign MCP tool did exactly that and produced platform-wide,
   * everyone-visible campaigns.
   */
  @Test
  public void testCreateProgramAudienceAndVisibilityAreComputed() {
    ProgramDTO spaceScoped = new ProgramDTO();
    spaceScoped.setTitle("space-scoped-program");
    spaceScoped.setDescription("Description");
    spaceScoped.setEnabled(true);
    spaceScoped.setSpaceId(Long.parseLong(SpaceServiceMock.SPACE_ID_1));
    spaceScoped.setOpen(false);
    spaceScoped = programService.createProgram(spaceScoped);
    assertEquals(Long.parseLong(SpaceServiceMock.SPACE_ID_1), spaceScoped.getSpaceId());
    assertFalse(spaceScoped.isOpen());
    // Space 1's registration is VALIDATION, hence a restricted audience.
    assertEquals(EntityVisibility.RESTRICTED, spaceScoped.getVisibility());

    ProgramDTO openWithSpace = new ProgramDTO();
    openWithSpace.setTitle("open-program-with-a-space");
    openWithSpace.setDescription("Description");
    openWithSpace.setEnabled(true);
    openWithSpace.setSpaceId(Long.parseLong(SpaceServiceMock.SPACE_ID_1));
    openWithSpace.setOpen(true);
    openWithSpace = programService.createProgram(openWithSpace);
    assertEquals("an open program loses its space audience", 0, openWithSpace.getSpaceId());
    assertEquals("and is therefore visible to everyone", EntityVisibility.OPEN, openWithSpace.getVisibility());
  }

  /**
   * An <b>anonymous</b> caller asking for a space's programs must not be given
   * them: ProgramRest#getPrograms admits an anonymous caller and passes its
   * blank current user into getPrograms/countPrograms, and a blank username used
   * to skip the space narrowing entirely, carrying the requested audience into
   * the DAO — which answered with that space's programs, RESTRICTED ones
   * included, to a visitor (EXO-90210).
   */
  @Test
  public void testGetProgramsOfASpaceIsNotGivenToAnonymous() throws IllegalAccessException {
    long spaceId = Long.parseLong(SpaceServiceMock.SPACE_ID_1);
    ProgramEntity restricted = newDomain(EntityType.MANUAL, "restricted-program", true, new HashSet<>(), spaceId);
    restricted.setVisibility(EntityVisibility.RESTRICTED);
    programDAO.update(restricted);
    ProgramEntity openProgram = newDomain(EntityType.MANUAL, "platform-wide-program", true, new HashSet<>());
    openProgram.setAudienceId(null);
    programDAO.update(openProgram);
    programStorage.clearCache();
    restartTransaction();

    ProgramFilter spaceFilter = new ProgramFilter();
    spaceFilter.setStatus(EntityStatusType.ALL);
    spaceFilter.setSpacesIds(Collections.singletonList(spaceId));
    spaceFilter.setExcludeOpen(true);

    // Both shapes the REST layer can hand over for a visitor: the space's
    // RESTRICTED program is never among them.
    assertEquals(Collections.emptyList(), programService.getProgramIds(spaceFilter, null, 0, 10));
    assertEquals(Collections.emptyList(), programService.getProgramIds(spaceFilter, "", 0, 10));
    assertEquals(0, programService.countPrograms(spaceFilter, null));

    // An OPEN program of that same space IS what the space shows to everyone,
    // so a visitor asking about the space does get it — and still not the
    // platform-wide one, which is not an answer to "the programs of space 1".
    ProgramEntity spaceOpenProgram = newDomain(EntityType.MANUAL, "space-open-program", true, new HashSet<>(), spaceId);
    spaceOpenProgram.setVisibility(EntityVisibility.OPEN);
    programDAO.update(spaceOpenProgram);
    programStorage.clearCache();
    restartTransaction();

    assertEquals(Collections.singletonList(spaceOpenProgram.getId()),
                 programService.getProgramIds(spaceFilter, null, 0, 10));
    assertEquals(1, programService.countPrograms(spaceFilter, null));
  }

  /**
   * The budget-ordered listing must answer the same question as the predicate
   * one. It goes through named queries (`Rule.getHighestBudget…`) instead of
   * ProgramDAO's predicates, so the space narrowing has to be expressed there
   * too: without it, `sortByBudget=true` returned a space's RESTRICTED programs
   * — and every platform-wide one — to a caller who shares none of its spaces,
   * while the count for the same filter answered otherwise (EXO-90210).
   */
  @Test
  public void testGetProgramsSortedByBudgetObeysTheSpaceNarrowing() throws IllegalAccessException {
    long spaceId = Long.parseLong(SpaceServiceMock.SPACE_ID_2);
    ProgramEntity spaceOpen = newDomain(EntityType.MANUAL, "budget-space-open", true, new HashSet<>(), spaceId);
    spaceOpen.setVisibility(EntityVisibility.OPEN);
    programDAO.update(spaceOpen);
    newRule("budget-space-open-rule", spaceOpen.getId());
    ProgramEntity spaceRestricted = newDomain(EntityType.MANUAL, "budget-space-restricted", true, new HashSet<>(), spaceId);
    spaceRestricted.setVisibility(EntityVisibility.RESTRICTED);
    programDAO.update(spaceRestricted);
    newRule("budget-space-restricted-rule", spaceRestricted.getId());
    ProgramEntity platformWide = newDomain(EntityType.MANUAL, "budget-platform-wide", true, new HashSet<>());
    platformWide.setAudienceId(null);
    platformWide.setVisibility(EntityVisibility.OPEN);
    programDAO.update(platformWide);
    newRule("budget-platform-wide-rule", platformWide.getId());
    programStorage.clearCache();
    ruleStorage.clearCache();
    restartTransaction();

    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ALL);
    filter.setSpacesIds(Collections.singletonList(spaceId));
    filter.setExcludeOpen(true);
    filter.setSortByBudget(true);

    // A visitor and a logged-in non-member: what the space shows to everyone,
    // and the count agrees with the list.
    assertEquals(Collections.singletonList(spaceOpen.getId()), programService.getProgramIds(filter, null, 0, 10));
    assertEquals(Collections.singletonList(spaceOpen.getId()),
                 programService.getProgramIds(filter, SPACE_MEMBER_USER, 0, 10));
    assertEquals(1, programService.countPrograms(filter, SPACE_MEMBER_USER));

    // A rewarding manager keeps the whole space — and neither caller class is
    // answered with the platform-wide program, which is not a program of that
    // space.
    List<Long> asAdmin = programService.getProgramIds(filter, ADMIN_USER, 0, 10);
    assertEquals(2, asAdmin.size());
    assertTrue(asAdmin.containsAll(List.of(spaceOpen.getId(), spaceRestricted.getId())));
    assertFalse("a platform-wide program is not a program of that space", asAdmin.contains(platformWide.getId()));
  }

  /**
   * The budget-ordered listing answers the same question as the predicate one on
   * every audience shape, not only the space-scoped ones: an <b>unscoped</b>
   * listing includes the OPEN programs that have a space audience — what every
   * public space shows to everyone — and not the RESTRICTED ones. Its queries
   * used to match on {@code audienceId IS NULL} alone, so a public space's
   * programs never appeared in a "top by budget" listing (EXO-90210).
   */
  @Test
  public void testGetProgramsSortedByBudgetUnscopedIncludesOpenSpacePrograms() throws IllegalAccessException {
    long spaceId = Long.parseLong(SpaceServiceMock.SPACE_ID_2);
    ProgramEntity spaceOpen = newDomain(EntityType.MANUAL, "unscoped-space-open", true, new HashSet<>(), spaceId);
    spaceOpen.setVisibility(EntityVisibility.OPEN);
    programDAO.update(spaceOpen);
    newRule("unscoped-space-open-rule", spaceOpen.getId());
    ProgramEntity spaceRestricted = newDomain(EntityType.MANUAL,
                                              "unscoped-space-restricted",
                                              true,
                                              new HashSet<>(),
                                              spaceId);
    spaceRestricted.setVisibility(EntityVisibility.RESTRICTED);
    programDAO.update(spaceRestricted);
    newRule("unscoped-space-restricted-rule", spaceRestricted.getId());
    ProgramEntity platformWide = newDomain(EntityType.MANUAL, "unscoped-platform-wide", true, new HashSet<>());
    platformWide.setAudienceId(null);
    platformWide.setVisibility(EntityVisibility.OPEN);
    programDAO.update(platformWide);
    newRule("unscoped-platform-wide-rule", platformWide.getId());
    programStorage.clearCache();
    ruleStorage.clearCache();
    restartTransaction();

    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ALL);
    filter.setSortByBudget(true);

    // A visitor and a logged-in member of another space: the platform-wide
    // program and the public space's open one, never the restricted one.
    for (String caller : new String[] { null, SPACE_MEMBER_USER }) {
      List<Long> ids = programService.getProgramIds(filter, caller, 0, 10);
      assertTrue("an OPEN program of a space belongs in an unscoped listing", ids.contains(spaceOpen.getId()));
      assertTrue(ids.contains(platformWide.getId()));
      assertFalse("a RESTRICTED program does not", ids.contains(spaceRestricted.getId()));
    }
  }

  /**
   * The unscoped listing — no space asked for — is untouched for a visitor: the
   * platform-wide programs, never a RESTRICTED one. Its own test so that a
   * regression on this path fails on its own name.
   */
  @Test
  public void testUnscopedListingIsUnchangedForAnonymous() throws IllegalAccessException {
    ProgramEntity restricted = newDomain(EntityType.MANUAL,
                                         "restricted-program",
                                         true,
                                         new HashSet<>(),
                                         Long.parseLong(SpaceServiceMock.SPACE_ID_1));
    restricted.setVisibility(EntityVisibility.RESTRICTED);
    programDAO.update(restricted);
    ProgramEntity platformWide = newDomain(EntityType.MANUAL, "platform-wide-program", true, new HashSet<>());
    platformWide.setAudienceId(null);
    platformWide.setVisibility(EntityVisibility.OPEN);
    programDAO.update(platformWide);
    programStorage.clearCache();
    restartTransaction();

    ProgramFilter anyFilter = new ProgramFilter();
    anyFilter.setStatus(EntityStatusType.ALL);

    List<Long> anonymous = programService.getProgramIds(anyFilter, null, 0, 10);
    assertEquals(Collections.singletonList(platformWide.getId()), anonymous);
    assertFalse("a RESTRICTED program is not listed to a visitor", anonymous.contains(restricted.getId()));
  }

  /**
   * get_my_campaigns' union: member ∪ owned, deduplicated, soft-deleted
   * programs excluded and the pagination applied ONCE over the union.
   */
  @Test
  public void testGetMyProgramIds() {
    long userIdentityId = Long.parseLong(identityManager.getOrCreateUserIdentity(SPACE_MEMBER_USER).getId());
    assertTrue(programService.getMyProgramIds(SPACE_MEMBER_USER, 0, 10).isEmpty());

    // Member (audience = the space the user belongs to) AND owned: it must
    // appear exactly once in the union.
    ProgramEntity memberAndOwned = newDomain(EntityType.MANUAL,
                                             "member-and-owned",
                                             true,
                                             Collections.singleton(userIdentityId),
                                             Long.parseLong(SpaceServiceMock.SPACE_ID_1));
    // Owned only, in a space the user is not a member of.
    ProgramEntity ownedOnly = newDomain(EntityType.MANUAL,
                                        "owned-only",
                                        true,
                                        Collections.singleton(userIdentityId),
                                        Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    // Owned but soft-deleted: both underlying id queries include deleted
    // programs, and resolving one by id broadcasts a program-deleted event on a
    // read path and then fails, which used to shorten the page.
    ProgramEntity deleted = newDomain(EntityType.MANUAL,
                                      "owned-but-deleted",
                                      true,
                                      Collections.singleton(userIdentityId),
                                      Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    deleted.setDeleted(true);
    programDAO.update(deleted);
    programStorage.clearCache();
    restartTransaction();

    List<Long> ids = programService.getMyProgramIds(SPACE_MEMBER_USER, 0, 10);
    assertEquals(Arrays.asList(memberAndOwned.getId(), ownedOnly.getId()), ids);

    // Pagination applies once over the union, not once per source list.
    assertEquals(Collections.singletonList(memberAndOwned.getId()), programService.getMyProgramIds(SPACE_MEMBER_USER, 0, 1));
    assertEquals(Collections.singletonList(ownedOnly.getId()), programService.getMyProgramIds(SPACE_MEMBER_USER, 1, 1));
    assertEquals(2, programService.getMyProgramIds(SPACE_MEMBER_USER, 0, -1).size());
    assertTrue(programService.getMyProgramIds(null, 0, 10).isEmpty());
  }

  @Test
  public void testUpdateProgram() throws Exception {
    ProgramDTO programWithColor = new ProgramDTO();
    programWithColor.setTitle(GAMIFICATION_DOMAIN);
    programWithColor.setDescription("Description");
    programWithColor.setDeleted(false);
    programWithColor.setEnabled(true);
    programWithColor.setBudget(20L);
    programWithColor.setColor("#FFEECCDD");
    programWithColor = programService.createProgram(programWithColor, adminAclIdentity);

    String newTitle = "title_2";
    String newDescription = "desc_2";
    long newBudget = 30;

    ProgramDTO notExistDomain = new ProgramDTO();
    notExistDomain.setId(150L);
    assertThrows(ObjectNotFoundException.class, () -> programService.updateProgram(notExistDomain, adminAclIdentity));
    ProgramDTO program = newProgram(EntityType.MANUAL, "domain1", true, Collections.singleton(1L));
    program.setDescription(newDescription);
    program.setTitle(newTitle);
    program.setBudget(newBudget);
    program.setEnabled(true);
    assertThrows(IllegalAccessException.class, () -> programService.updateProgram(program, spaceMemberAclIdentity));

    OrganizationService organizationService = ExoContainerContext.getService(OrganizationService.class);
    User user = organizationService.getUserHandler().createUserInstance(spaceMemberAclIdentity.getUserId());
    user.setFirstName("Regular");
    user.setLastName("User");
    user.setEmail("regularuser@localhost.com");
    organizationService.getUserHandler().createUser(user, true);

    String ownerId = identityManager.getOrCreateUserIdentity(user.getUserName()).getId();
    Set<Long> newOwners = Collections.singleton(Long.parseLong(ownerId));
    program.setOwnerIds(newOwners);
    program.setEnabled(false);
    ProgramDTO updatedDomain = programService.updateProgram(program, adminAclIdentity);
    assertFalse(updatedDomain.isEnabled());

    updatedDomain.setEnabled(true);
    updatedDomain.setColor("#FFEECCEE");
    programService.updateProgram(updatedDomain, adminAclIdentity);

    ProgramDTO storedDomain = programService.getProgramById(updatedDomain.getId());
    assertNotNull(storedDomain);
    assertEquals(newDescription, storedDomain.getDescription());
    assertEquals(newTitle, storedDomain.getTitle());
    assertEquals(newBudget, storedDomain.getBudget());
    assertEquals(newOwners, storedDomain.getOwnerIds());
    assertEquals("#FFEECCEE", storedDomain.getColor());
    assertTrue(storedDomain.isEnabled());

    updatedDomain = programService.updateProgram(updatedDomain, adminAclIdentity);
    assertEquals("#FFEECCEE", storedDomain.getColor());

    updatedDomain.setColor(programWithColor.getColor());
    ProgramDTO programToUpdate = updatedDomain.clone();
    assertThrows(ProgramColorAlreadyExists.class, () -> programService.updateProgram(programToUpdate, adminAclIdentity));

    programService.deleteProgramById(programWithColor.getId(), adminAclIdentity);
    updatedDomain = programService.updateProgram(programToUpdate, adminAclIdentity);
    assertEquals(programWithColor.getColor(), updatedDomain.getColor());

    programService.deleteProgramById(storedDomain.getId(), adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.updateProgram(programToUpdate, spaceMemberAclIdentity));
  }

  @Test
  public void testCanUseProgramColor() throws Exception {
    ProgramDTO programWithColor = new ProgramDTO();
    programWithColor.setTitle(GAMIFICATION_DOMAIN);
    programWithColor.setDescription("Description");
    programWithColor.setDeleted(false);
    programWithColor.setEnabled(true);
    programWithColor.setBudget(20L);
    programWithColor.setColor("#FFEECCDD");
    programWithColor = programService.createProgram(programWithColor, adminAclIdentity);

    assertTrue(programService.canUseProgramColor(programWithColor.getId(), programWithColor.getColor()));
    assertFalse(programService.canUseProgramColor(0, programWithColor.getColor()));
    assertTrue(programService.canUseProgramColor(0, "#FFEECCCC"));
    assertTrue(programService.canUseProgramColor(0, null));
  }

  @Test
  public void testDeleteProgram() throws Exception {
    ProgramDTO domain = newProgram(EntityType.MANUAL, "domain1", true, Collections.singleton(1L));
    assertFalse(domain.isDeleted());
    domain.setDeleted(true);

    assertThrows(ObjectNotFoundException.class, () -> programService.deleteProgramById(20000L, adminAclIdentity));
    assertThrows(IllegalAccessException.class, () -> programService.deleteProgramById(domain.getId(), spaceMemberAclIdentity));

    programService.deleteProgramById(domain.getId(), adminAclIdentity);
    ProgramEntity domainEntity = programDAO.find(domain.getId());
    assertTrue(domainEntity.isDeleted());
  }

  @Test
  public void testDisableProgramReturnsNoPoints() throws Exception {
    RuleDTO rule = newRuleDTO();
    ProgramDTO program = rule.getProgram();

    assertEquals(rule.getScore(), program.getRulesTotalScore());

    program.setEnabled(false);
    program = programService.updateProgram(program, adminAclIdentity);
    assertEquals(0, program.getRulesTotalScore());

    program.setEnabled(true);
    program = programService.updateProgram(program, adminAclIdentity);
    assertEquals(rule.getScore(), program.getRulesTotalScore());
    program = programService.getProgramById(program.getId(), adminAclIdentity.getUserId());
    assertEquals(rule.getScore(), program.getRulesTotalScore());
  }

  @Test
  public void testGetProgramById() {
    assertEquals(programDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> programService.getProgramById(-1L));
    ProgramDTO program = newProgram();
    assertNotNull(program);
    ProgramDTO domain = programService.getProgramById(program.getId());
    assertNotNull(domain);
    assertEquals(program.getId(), domain.getId());
  }

  @Test
  public void testGetProgramByIdAndUser() throws IllegalAccessException, ObjectNotFoundException {
    assertEquals(programDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> programService.getProgramById(-1L, ADMIN_USER));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(5000L, ADMIN_USER));

    ProgramDTO program = newProgram();
    assertNotNull(program);

    long programId = program.getId();

    ProgramDTO foundProgram = programService.getProgramById(programId, ADMIN_USER);
    assertNotNull(foundProgram);
    assertEquals(programId, foundProgram.getId());
    assertTrue(program.isEnabled());
    assertNotNull(programService.getProgramById(programId, SPACE_MEMBER_USER));
    assertThrows(IllegalAccessException.class, () -> programService.getProgramById(programId, "demo"));

    program.setEnabled(false);
    programService.updateProgram(program, adminAclIdentity);

    assertNotNull(programService.getProgramById(programId, ADMIN_USER));
    assertNotNull(programService.getProgramById(programId, SPACE_MEMBER_USER));
    assertThrows(IllegalAccessException.class, () -> programService.getProgramById(programId, "demo"));

    programService.deleteProgramById(programId, adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(programId, ADMIN_USER));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(programId, SPACE_MEMBER_USER));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(programId, "demo"));
  }

  @Test
  public void testGetProgramCoverAsStream() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "cover.png", "image/png");
    domain.setCoverUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramCoverStream(domain.getId()));
  }

  @Test
  public void testGetProgramAvatarAsStream() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "avatar.png", "image/png");
    domain.setAvatarUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramAvatarStream(domain.getId()));
  }

  @Test
  public void testDeleteProgramCover() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "cover.png", "image/png");
    domain.setCoverUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramCoverStream(domain.getId()));

    programService.deleteProgramCoverById(domainId, adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(domainId));
  }

  @Test
  public void testDeleteProgramAvatar() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "avatar.png", "image/png");
    domain.setAvatarUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramAvatarStream(domain.getId()));

    programService.deleteProgramAvatarById(domainId, adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(domainId));
  }

  @Test
  public void testCanAddProgram() {
    assertFalse(programService.canAddProgram(null, 0));
    assertFalse(programService.canAddProgram(spaceMemberAclIdentity.getUserId(), 0));
    assertTrue(programService.canAddProgram(adminAclIdentity.getUserId(), 0));
    assertFalse(programService.canAddProgram(spaceMemberAclIdentity.getUserId(), 1));
    assertTrue(programService.canAddProgram(spaceManagerAclIdentity.getUserId(), 1));
  }

  @Test
  public void testCanUpdateProgram() throws IllegalAccessException, ObjectNotFoundException {
    ProgramDTO domain = newProgram();
    assertFalse(programService.isProgramOwner(domain.getId(), spaceMemberAclIdentity.getUserId()));
    assertTrue(programService.isProgramOwner(domain.getId(), adminAclIdentity.getUserId()));
    assertFalse(programService.isProgramOwner(0, spaceMemberAclIdentity.getUserId()));
    String identityId = identityManager.getOrCreateUserIdentity(spaceMemberAclIdentity.getUserId()).getId();
    domain.setOwnerIds(Collections.singleton(Long.parseLong(identityId)));
    programService.updateProgram(domain, adminAclIdentity);
    assertTrue(programService.isProgramOwner(domain.getId(), spaceMemberAclIdentity.getUserId()));
  }
}
