package org.exoplatform.addons.gamification.rest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.setting.badge.impl.BadgeRegistryImpl;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

@Path("/gamification/badges")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("administrators")
public class ManageBadgesEndpoint implements ResourceContainer {

  private static final Log        LOG                          = ExoLogger.getLogger(ManageBadgesEndpoint.class);

  private static SimpleDateFormat formatter                    = new SimpleDateFormat("yyyy-MM-dd");

  private static final String     DEFAULT_BADGE_ICON_NAME      = "DEFAULT_BADGE_ICON";

  private static final String     DEFAULT_BADGE_ICON_MIME_TYPE = "image/png";

  private static final String     DEFAULT_BADGE_ICON_NAMESPACE = "gamification";

  private final CacheControl      cacheControl;

  protected BadgeService          badgeService                 = null;

  protected RuleService           ruleService                  = null;

  protected FileService           fileService                  = null;

  protected UploadService         uploadService                = null;

  protected IdentityManager       identityManager                = null;

  public ManageBadgesEndpoint() {

    this.cacheControl = new CacheControl();

    cacheControl.setNoCache(true);

    cacheControl.setNoStore(true);

    badgeService = CommonsUtils.getService(BadgeService.class);

    ruleService = CommonsUtils.getService(RuleService.class);

    fileService = CommonsUtils.getService(FileService.class);

    uploadService = CommonsUtils.getService(UploadService.class);

    identityManager = CommonsUtils.getService(IdentityManager.class);


  }

  @GET
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
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }

  @POST
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
        badgeDTO.setCreatedDate(formatter.format(new Date()));
        /**
         * Gamification rely on FileService, thus we don't need to persist icon in
         * Gamification DB
         */
        // badgeDTO.setIcon(badgeDTO.getIcon());

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

      } catch (EntityExistsException e) {

        LOG.error("Badge with title {} and domain {} already exist", badgeDTO.getTitle(), badgeDTO.getDomain(), e);

        return Response.notModified()
                .cacheControl(cacheControl)
                .entity("Badge already exists")
                .build();

      } catch (Exception e) {

        LOG.error("Error adding new badge {} by {} ", badgeDTO.getTitle(), currentUserName, e);

        return Response.serverError().cacheControl(cacheControl).entity("Error adding new badge").build();

      }

    } else {

      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }

  @PUT
  @Path("/update")
  public Response updateBadge(@Context UriInfo uriInfo, @Context HttpServletRequest request, BadgeDTO badgeDTO) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      String currentUserName = conversationState.getIdentity().getUserId();
      try {

        // TODO : Load locale
        Locale lc = request.getLocale();

        if (badgeDTO.getUploadId() != null) {
          UploadResource uploadResource = uploadService.getUploadResource(badgeDTO.getUploadId());

          if (uploadResource != null) {
            /** Upload badge's icon into DB */
            FileItem fileItem = null;

            fileItem = new FileItem(null,
                                    badgeDTO.getTitle().toLowerCase(),
                                    uploadResource.getMimeType(),
                                    "gamification",
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
        badgeDTO.setLastModifiedDate(formatter.format(new Date()));

        // --- Update rule
        badgeDTO = badgeService.updateBadge(badgeDTO);

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false).getId();
        LOG.info("service=gamification operation=edit-badge parameters=\"user_social_id:{},badge_id:{},badge_title:{},badge_description:{}\"", actorId, badgeDTO.getId(), badgeDTO.getTitle(), badgeDTO.getDescription());

        return Response.ok().cacheControl(cacheControl).entity(badgeDTO).build();
      }
        catch (EntityExistsException e) {

          LOG.error("Badge with title {} and domain {} already exist", badgeDTO.getTitle(), badgeDTO.getDomain(), e);

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

      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }

  @DELETE
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
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }

}
