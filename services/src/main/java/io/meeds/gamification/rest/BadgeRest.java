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
package io.meeds.gamification.rest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.service.BadgeService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.service.impl.BadgeRegistryImpl;

@Path("/gamification/badges")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("administrators")
public class BadgeRest implements ResourceContainer {

  private static final Log        LOG                          = ExoLogger.getLogger(BadgeRest.class);

  private static final String     DEFAULT_BADGE_ICON_NAME      = "DEFAULT_BADGE_ICON";

  private static final String     DEFAULT_BADGE_ICON_MIME_TYPE = "image/png";

  private static final String     DEFAULT_BADGE_ICON_NAMESPACE = "gamification";

  private final CacheControl      cacheControl;

  protected final BadgeService    badgeService;

  protected final RuleService     ruleService;

  protected final FileService     fileService;

  protected final UploadService   uploadService;

  protected final IdentityManager identityManager;

  public BadgeRest(BadgeService badgeService,
                   RuleService ruleService,
                   FileService fileService,
                   UploadService uploadService,
                   IdentityManager identityManager) {
    this.cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);

    this.badgeService = badgeService;
    this.ruleService = ruleService;
    this.fileService = fileService;
    this.uploadService = uploadService;
    this.identityManager = identityManager;
  }

  @GET
  @RolesAllowed("users")
  @Path("/all")
  public Response getAllBadges(@Context UriInfo uriInfo) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      try {
        List<BadgeDTO> allBadges = badgeService.getAllBadges();

        return Response.ok(allBadges, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

      } catch (Exception e) {

        LOG.error("Error listing all GamificationInformationsPortlet ", e);

        return Response.serverError()
                       .cacheControl(cacheControl)
                       .entity("Error listing all GamificationInformationsPortlet")
                       .build();
      }

    } else {
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).build();
    }

  }

  @POST
  @RolesAllowed("administrators")
  @Path("/add")
  public Response addBadge(@Context UriInfo uriInfo, BadgeDTO badgeDTO) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      String currentUserName = conversationState.getIdentity().getUserId();

      InputStream inputStream = null;

      /** Upload badge's icon into DB */
      FileItem fileItem = null;

      try {

        // Compute rule's data
        badgeDTO.setId(null);
        badgeDTO.setCreatedBy(currentUserName);
        badgeDTO.setLastModifiedBy(currentUserName);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        badgeDTO.setCreatedDate(formatter.format(new Date()));
        badgeDTO.setLastModifiedDate(formatter.format(new Date()));

        if (badgeDTO.getUploadId() != null) {
          UploadResource uploadResource = uploadService.getUploadResource(badgeDTO.getUploadId());

          if (uploadResource != null) {

            fileItem = new FileItem(null,
                                    badgeDTO.getTitle().toLowerCase(),
                                    uploadResource.getMimeType(),
                                    DEFAULT_BADGE_ICON_NAMESPACE,
                                    (long) uploadResource.getUploadedSize(),
                                    new Date(),
                                    currentUserName,
                                    false,
                                    new FileInputStream(uploadResource.getStoreLocation()));
            fileItem = fileService.writeFile(fileItem);

          } else {
            inputStream = BadgeRegistryImpl.class.getClassLoader().getResourceAsStream("medias/images/default_badge.png");

            fileItem = new FileItem(null,
                                    DEFAULT_BADGE_ICON_NAME,
                                    DEFAULT_BADGE_ICON_MIME_TYPE,
                                    DEFAULT_BADGE_ICON_NAMESPACE,
                                    inputStream.available(),
                                    new Date(),
                                    currentUserName,
                                    false,
                                    inputStream);

            fileItem = fileService.writeFile(fileItem);

          }
          /** END upload */
          badgeDTO.setIconFileId(fileItem.getFileInfo().getId());
        }

        // --- Add badge
        badgeDTO = badgeService.addBadge(badgeDTO);

        return Response.ok().cacheControl(cacheControl).entity(badgeDTO).build();

      } catch (ObjectAlreadyExistsException e) {

        LOG.error("Badge with title {} and program {} already exist", badgeDTO.getTitle(), badgeDTO.getProgram().getTitle(), e);

        return Response.notModified()
                .cacheControl(cacheControl)
                .entity("Badge already exists")
                .build();

      } catch (Exception e) {

        LOG.error("Error adding new badge {} by {} ", badgeDTO.getTitle(), currentUserName, e);

        return Response.serverError().cacheControl(cacheControl).entity("Error adding new badge").build();

      }

    } else {

      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).build();
    }

  }

  @PUT
  @RolesAllowed("administrators")
  @Path("/update")
  public Response updateBadge(@Context UriInfo uriInfo, @Context HttpServletRequest request, BadgeDTO badgeDTO) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      String currentUserName = conversationState.getIdentity().getUserId();
      try {

        if (badgeDTO.getUploadId() != null) {
          /** Upload badge's icon into DB */
          FileItem fileItem = null;
          UploadResource uploadResource = uploadService.getUploadResource(badgeDTO.getUploadId());

          if (uploadResource != null) {

            fileItem = new FileItem(null,
                    badgeDTO.getTitle().toLowerCase(),
                    uploadResource.getMimeType(),
                    DEFAULT_BADGE_ICON_NAMESPACE,
                    (long) uploadResource.getUploadedSize(),
                    new Date(),
                    currentUserName,
                    false,
                    new FileInputStream(uploadResource.getStoreLocation()));
            fileItem = fileService.writeFile(fileItem);
            /** END upload */
            badgeDTO.setIconFileId(fileItem.getFileInfo().getId());

          }
        }
        // Compute rule's data
        badgeDTO.setCreatedBy(currentUserName);
        badgeDTO.setLastModifiedBy(currentUserName);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        badgeDTO.setLastModifiedDate(formatter.format(new Date()));

        // --- Update rule
        badgeDTO = badgeService.updateBadge(badgeDTO);
        return Response.ok().cacheControl(cacheControl).entity(badgeDTO).build();
      }
        catch (ObjectAlreadyExistsException e) {

          LOG.error("Badge with title {} and program {} already exist", badgeDTO.getTitle(), badgeDTO.getProgram().getTitle(), e);

          return Response.notModified()
                  .cacheControl(cacheControl)
                  .entity("Badge already exists")
                  .build();

        }
        catch (Exception e) {

        LOG.error("Error when updating badge {} by {} ", badgeDTO.getTitle(), currentUserName, e);

        return Response.serverError().cacheControl(cacheControl).entity("Error adding new badge").build();
        }

    } else {

      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).build();
    }

  }

  @DELETE
  @RolesAllowed("administrators")
  @Path("/delete/{id}")
  public Response deleteBadge(@Context UriInfo uriInfo, @PathParam("id") Long id) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {
      try {
        // --- Remove the rule
        badgeService.deleteBadge(id);

        return Response.ok("Deleted " + id, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

      } catch (Exception e) {

        LOG.error("Error deleting badge {} by {} ", id, conversationState.getIdentity().getUserId(), e);

        return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
      }

    } else {
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).build();
    }

  }

}
