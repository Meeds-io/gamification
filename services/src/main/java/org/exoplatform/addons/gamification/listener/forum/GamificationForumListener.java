package org.exoplatform.addons.gamification.listener.forum;

import org.exoplatform.forum.service.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

public class GamificationForumListener extends ForumEventListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationForumListener.class);

    @Override
    public void saveCategory(Category category) {
    }

    @Override
    public void saveForum(Forum forum) {
    }

    @Override
    public void addPost(Post post) {

    }


    @Override
    public void updatePost(Post post) {

    }

    @Override
    public void updatePost(Post post, int type) {

    }

    @Override
    public void addTopic(Topic topic) {

    }

    @Override
    public void updateTopic(Topic topic) {


    }

    @Override
    public void updateTopics(List<Topic> topics, boolean isLock) {

    }


    @Override
    public void moveTopic(Topic topic, String toCategoryName, String toForumName) {

    }


    @Override
    public void mergeTopic(Topic newTopic, String removeActivityId1, String removeActivityId2) {

    }

    @Override
    public void splitTopic(Topic newTopic, Topic splitedTopic, String removeActivityId) {

    }

    @Override
    public void removeActivity(String activityId) {

    }

    @Override
    public void removeComment(String activityId, String commentId) {

    }

    @Override
    public void movePost(List<Post> list, List<String> list1, String s) {

    }
}
