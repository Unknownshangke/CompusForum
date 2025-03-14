package com.xzp.forum.service;

import com.xzp.forum.model.Topic;
import java.util.List;

public interface TopicService {
    Topic getTopicById(Long id);
    List<Topic> getTopicsBySection(String section, int page);
    void deleteTopic(Long id);
    void lockTopic(Long id);
    void unlockTopic(Long id);
    boolean hasModeratorPermission(Long topicId);
}
