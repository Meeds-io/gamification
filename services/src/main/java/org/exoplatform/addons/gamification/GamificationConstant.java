package org.exoplatform.addons.gamification;

public interface GamificationConstant {

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE LIKE GAMIFICATION PROCESS *************************************************/
    /***************************************************************************************************************************************************/
    /** LIKE COMMENT ON USER CONTEXT */
    String GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM                           = "likeCommentOnNetworkStream";
    /** LIKE COMMENT ON SPACE CONTEXT */
    String GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM                             = "likeCommentOnspaceStream";
    /** LIKE COMMENT BY A USER */
    String GAMIFICATION_SOCIAL_LIKE_COMMENT                                          = "likeComment";

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE COMMENT GAMIFICATION PROCESS **********************************************/
    /***************************************************************************************************************************************************/
    /**  COMMENT ON USER CONTEXT */
    String GAMIFICATION_SOCIAL_COMMENT_NETWORK_STREAM                                = "commentOnNetworkStream";
    /**  COMMENT ON SPACE CONTEXT */
    String GAMIFICATION_SOCIAL_COMMENT_SPACE_STREAM                                  = "commentOnSpaceStream";
    /**  COMMENT BY A USER */
    String GAMIFICATION_SOCIAL_COMMENT_ADD                                           = "addComment";

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
    /************************************************** CONSTANTS TO MANAGE FORUM GAMIFICATION PROCESS *************************************************/
    /***************************************************************************************************************************************************/
    /** ADD POST IN FORUM */
    String GAMIFICATION_FORUM_ADD_POST                                                  = "addForumPost";
    /** ADD TOPIC IN FORUM */
    String GAMIFICATION_FORUM_ADD_TOPIC                                                 = "addForumTopic";
    /** ADD TOPIC IN FORUM */
    String GAMIFICATION_FORUM_VOTE_TOPIC                                                = "voteForumTopic";


    /***************************************************************************************************************************************************/
    /************************************************** CONSTANTS TO MANAGE WIKI GAMIFICATION PROCESS **************************************************/
    /***************************************************************************************************************************************************/
    /** ADD POST IN WIKI  */
    String GAMIFICATION_WIKI_ADD_PAGE                                                   = "addWikiPage";
    /** ADD TOPIC IN WIKI */
    String GAMIFICATION_WIKI_UPDATE_PAGE                                                = "updateWikiPage";

    /***************************************************************************************************************************************************/
    /************************************************** CONSTANTS TO MANAGE TASK GAMIFICATION PROCESS **************************************************/
    /***************************************************************************************************************************************************/
    /** CREATE NEW TASK IN TAS ADDON */
    String GAMIFICATION_TASK_ADDON_CREATE_TASK                                          = "createNewTask";
    /** COMPLETED TASK IN TAS ADDON ASSIGNED */
    String GAMIFICATION_TASK_ADDON_COMPLETED_TASK_ASSIGNED                              = "completeTaskAssigned";
    /** COMPLETED TASK IN TASK ADDON COWORKER */
    String GAMIFICATION_TASK_ADDON_COMPLETED_TASK_COWORKER                              = "completeTaskCoworker";
    /** COMMENT TASK IN TASK ADDON */
    String GAMIFICATION_TASK_ADDON_COMMENT_TASK                                         = "commentTask";
    /** UPDATE TASK IN TASK ADDON */
    String GAMIFICATION_TASK_ADDON_UPDATE_TASK                                          = "updateTask";

    /***************************************************************************************************************************************************/
    /*************************************************** CONSTANTS TO MANAGE Knowledge GAMIFICATION Upload Documents **********************************************/
    /***************************************************************************************************************************************************/
    /** UPLOAD AND SHARE DOCUMENT ON USER'S NETWORK CONTEXT */
    String GAMIFICATION_KNOWLEDGE_SHARE_UPLOAD__DOCUMENT_NETWORK_STREAM                           = "uploaddocumentOnNetworkStream";






}
