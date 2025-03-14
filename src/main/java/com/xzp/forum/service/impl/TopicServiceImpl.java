package com.xzp.forum.service.impl;

import com.xzp.forum.dao.TopicDao;
import com.xzp.forum.model.Topic;
import com.xzp.forum.model.User;
import com.xzp.forum.service.TopicService;
import com.xzp.forum.service.UserService;
import com.xzp.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public Topic getTopicById(Long id) {
        return topicDao.getTopicById(id);
    }

    @Override
    public List<Topic> getTopicsBySection(String section, int page) {
        return topicDao.getTopicsBySection(section, page);
    }

    @Override
    public void deleteTopic(Long id) {
        topicDao.deleteTopic(id);
    }

    @Override
    public void lockTopic(Long id) {
        topicDao.lockTopic(id);
    }

    @Override
    public void unlockTopic(Long id) {
        topicDao.unlockTopic(id);
    }

    @Override
    public boolean hasModeratorPermission(Long topicId) {
        User currentUser = hostHolder.getUser();
        if (currentUser == null) {
            return false;
        }

        Topic topic = getTopicById(topicId);
        if (topic == null) {
            return false;
        }

        return currentUser.hasModeratorPermission(topic.getSection());
    }
}
