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
package org.exoplatform.addons.gamification;

public interface GamificationConstant {

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE LIKE GAMIFICATION PROCESS *************************************************/
    /***************************************************************************************************************************************************/
    /** LIKE COMMENT ON USER CONTEXT */
    String GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM_TARGET                    = "likeCommentOnNetworkStreamTarget";
    /** LIKE COMMENT ON USER CONTEXT */
    String GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM_TARGET                      = "likeCommentOnSpaceStreamTarget";
    /** LIKE COMMENT ON SPACE CONTEXT */
    String GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM                             = "likeCommentOnspaceStream";
    /** LIKE COMMENT BY A USER */
    String GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM                           = "likeCommentOnNetworkStream";

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE COMMENT GAMIFICATION PROCESS **********************************************/
    /***************************************************************************************************************************************************/
    /**  COMMENT ON USER CONTEXT */
    String GAMIFICATION_SOCIAL_RECEIVE_COMMENT_NETWORK_STREAM                        = "receiveCommentOnNetworkStream";
    /**  COMMENT ON SPACE CONTEXT */
    String GAMIFICATION_SOCIAL_RECEIVE_COMMENT_SPACE_STREAM                          = "receiveCommentOnSpaceStream";
    /**  COMMENT BY A USER ON NETWORK STREAM*/
    String GAMIFICATION_SOCIAL_ADD_COMMENT_NETWORK_STREAM                            = "addCommentOnNetworkStream";
    /**  COMMENT BY A USER ON SPACE STREAM*/
    String GAMIFICATION_SOCIAL_ADD_COMMENT_SPACE_STREAM                              = "addCommentOnSpaceStream";

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE COMMENT GAMIFICATION PROCESS **********************************************/
    /***************************************************************************************************************************************************/
    /** ADD ACTIVITY ON USER'S NETWORK CONTEXT */
    String GAMIFICATION_SOCIAL_ADD_ACTIVITY_NETWORK_STREAM                           = "addActivityOnNetworkStream";
    /** ADD ACTIVITY ON SPACE CONTEXT */
    String GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM                             = "addActivityOnSpaceStream";
    /** ADD ACTIVITY ON MY STREAM */
    String GAMIFICATION_SOCIAL_ADD_ACTIVITY_MY_STREAM                                = "addActivityOnMyStream";
    /** ADD ACTIVITY ON MY STREAM */
    String GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_TARGET                             = "addActivityOnSpaceStreamTarget";
    /** ADD ACTIVITY ON MY STREAM */
    String GAMIFICATION_SOCIAL_ADD_ACTIVITY_TARGET_USER_STREAM                       = "addActivityTargetUserStream";

    /***************************************************************************************************************************************************/
    /************************************************ CONSTANTS TO MANAGE LIKE ACTIVITY GAMIFICATION PROCESS *******************************************/
    /***************************************************************************************************************************************************/
    /** LIKE ACTIVITY ON USER'S NETWORK CONTEXT */
    String GAMIFICATION_SOCIAL_LIKE_ACTIVITY_NETWORK_STREAM                           = "likeActivityOnNetworkStream";
    /** LIKE ACTIVITY ON SPACE STREAM */
    String GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM                             = "likeActivityOnSpaceStream";
    /** LIKE ACTIVITY ON MY STREAM */
    String GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_TARGET                             = "likeActivityOnSpaceStreamTarget";
    /** LIKE ACTIVITY ON MY STREAM */
    String GAMIFICATION_SOCIAL_LIKE_ACTIVITY_TARGET_USER_STREAM                       = "likeActivityTargetUserStream";

    /***************************************************************************************************************************************************/
    /************************************************ CONSTANTS TO MANAGE USER PROFILE GAMIFICATION PROCESS *******************************************/
    /***************************************************************************************************************************************************/
    /** ADD USER'S AVATAR  */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR                                     = "addUserProfileAvatar";
    /** ADD USER'S BANNER  */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER                                     = "addUserProfileBanner";
    /** ADD USER'S ABOUT ME  */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME                                    = "addUserProfileAboutMe";

    /***************************************************************************************************************************************************/
    /**************************************************** CONSTANTS TO MANAGE SPACE GAMIFICATION PROCESS ***********************************************/
    /***************************************************************************************************************************************************/
    /** CREATE NEW SPACE */
    String GAMIFICATION_SOCIAL_SPACE_ADD                                               = "addSpace";
    /** JOIN SPACE  */
    String GAMIFICATION_SOCIAL_SPACE_JOIN                                              = "joinSpace";
    /** GRANT USER AS LEAD WITHIN SPACE */
    String GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD                                     = "grantUserAsLead";

    /***************************************************************************************************************************************************/
    /*********************************************** CONSTANTS TO MANAGE RELATIONSHIP GAMIFICATION PROCESS *********************************************/
    /***************************************************************************************************************************************************/
    /** SEND RELATIONSHIP REQUEST */
    String GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER                                     = "sendRelationshipRequest";
    /** RECEIVE RELATIONSHIP REQUEST */
    String GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER                                   = "receiveRelationshipRequest";


    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE Knowledge GAMIFICATION Upload Documents **********************************************/
    /***************************************************************************************************************************************************/
    /** UPLOAD AND SHARE DOCUMENT ON USER'S NETWORK CONTEXT */
    String GAMIFICATION_KNOWLEDGE_SHARE_UPLOAD__DOCUMENT_NETWORK_STREAM                           = "uploaddocumentOnNetworkStream";

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE Attendance GAMIFICATION User Login **********************************************/
    /***************************************************************************************************************************************************/
    /** USER LOGIN CONTEXT */
    String GAMIFICATION_ATTENDANCE_USER_LOGIN                                           = "userLogin";


    /***************************************************************************************************************************************************/
    /*************************************************** Prefix fot default data **********************************************/
    /***************************************************************************************************************************************************/

    String GAMIFICATION_DEFAULT_DATA_PREFIX                          = "def_";



}
