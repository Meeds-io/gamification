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
package io.meeds.gamification.constant;

public interface GamificationConstant { // NOSONAR

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE COMMENT GAMIFICATION PROCESS **********************************************/
    /***************************************************************************************************************************************************/
    String GAMIFICATION_SOCIAL_POST_ACTIVITY                    = "postActivity";

    String GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT            = "postActivityComment";

    String GAMIFICATION_SOCIAL_LIKE_ACTIVITY                    = "likeActivity";

    String GAMIFICATION_SOCIAL_LIKE_ACTIVITY_COMMENT            = "likeActivityComment";

    String GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY                 = "receiveActivity";

    String GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT         = "receiveCommentOnActivity";

    String GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY            = "receiveLikeOnActivity";

    String GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY_COMMENT    = "receiveLikeOnActivityComment";

    String GAMIFICATION_SOCIAL_PIN_ACTIVITY_SPACE               = "pinActivityOnSpace";

    /***************************************************************************************************************************************************/
    /************************************************ CONSTANTS TO MANAGE LIKE ACTIVITY GAMIFICATION PROCESS *******************************************/
    /***************************************************************************************************************************************************/

    /***************************************************************************************************************************************************/
    /************************************************ CONSTANTS TO MANAGE USER PROFILE GAMIFICATION PROCESS *******************************************/
    /***************************************************************************************************************************************************/
    /** ADD USER'S AVATAR  */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR                                     = "addUserProfileAvatar";

    /** ADD USER'S BANNER  */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER                                     = "addUserProfileBanner";

    /** ADD USER'S ABOUT ME  */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME                                    = "addUserProfileAboutMe";

    /** ADD USER'S CONTACT INFORMATION */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_CONTACT_INFORMATION                        = "addUserProfileContactInformation";

    /** ADD USER'S Work Experience */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_WORK_EXPERIENCE                            = "addUserProfileWorkExperience";

    /** ADD USER'S Notification setting */
    String GAMIFICATION_SOCIAL_PROFILE_ADD_NOTIFICATION_SETTING                       = "addUserProfileNotificationSetting";

    /***************************************************************************************************************************************************/
    /**************************************************** CONSTANTS TO MANAGE SPACE GAMIFICATION PROCESS ***********************************************/
    /***************************************************************************************************************************************************/
    /** CREATE NEW SPACE */
    String GAMIFICATION_SOCIAL_SPACE_ADD                                               = "addSpace";

    String GAMIFICATION_SOCIAL_SPACE_UPDATE_AVATAR                                     = "updateSpaceAvatar";

    String GAMIFICATION_SOCIAL_SPACE_UPDATE_BANNER                                     = "updateSpaceBanner";

    String GAMIFICATION_SOCIAL_SPACE_UPDATE_DESCRIPTION                                = "updateSpaceDescription";

    String GAMIFICATION_SOCIAL_SPACE_UPDATE_APPLICATIONS                               = "updateSpaceApplications";

    /** JOIN SPACE  */
    String GAMIFICATION_SOCIAL_SPACE_JOIN                                              = "joinSpace";
    /** GRANT USER AS LEAD WITHIN SPACE */
    String GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD                                     = "becomeSpaceManager";
    /** GRANT USER AS LEAD WITHIN SPACE */
    String GAMIFICATION_SOCIAL_SPACE_INVITE_USER                                       = "inviteUserToSpace";

    /***************************************************************************************************************************************************/
    /*********************************************** CONSTANTS TO MANAGE RELATIONSHIP GAMIFICATION PROCESS *********************************************/
    /***************************************************************************************************************************************************/
    /** SEND RELATIONSHIP REQUEST */
    String GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER                                     = "sendRelationshipRequest";
    /** RECEIVE RELATIONSHIP REQUEST */
    String GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER                                   = "receiveRelationshipRequest";


    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE Attendance GAMIFICATION User Login **********************************************/
    /***************************************************************************************************************************************************/
    /** USER LOGIN CONTEXT */
    String GAMIFICATION_ATTENDANCE_USER_LOGIN                                           = "userLogin";


    /***************************************************************************************************************************************************/
    /*************************************************** Prefix fot default data **********************************************/
    /***************************************************************************************************************************************************/

    String GAMIFICATION_DEFAULT_DATA_PREFIX                          = "def_";


    //Achievement object types
    
    String IDENTITY_OBJECT_TYPE                                       = "identity";

    String SPACE_MEMBERSHIP_OBJECT_TYPE                               = "spaceMembership";

    String ACTIVITY_OBJECT_TYPE                                       = "activity";

    String BROADCAST_GAMIFICATION_EVENT_ERROR                         = "Error while broadcasting gamification event: {}";

    String OBJECT_ID_PARAM                                            = "objectId";

    String OBJECT_TYPE_PARAM                                          = "objectType";

    /**
     * @deprecated this constant name is ambiguous since its name reflect to
     *             Rule's title but its real reference is to Event name.
     *             Use EVENT_NAME constant instead.
     */
    @Deprecated(forRemoval = true, since = "2.5.0")
    String RULE_TITLE                                                 = "ruleTitle";

    String EVENT_NAME                                                 = "eventId";

    String SENDER_ID                                                  = "senderId";
    
    String SENDER_TYPE                                                = "senderType";

    String RECEIVER_ID                                                = "receiverId";

    String RECEIVER_TYPE                                              = "receiverType";

}
