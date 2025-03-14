package com.xzp.forum.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.xzp.forum.dao.AnswerDao;
import com.xzp.forum.dao.MessageDao;
import com.xzp.forum.dao.TopicDao;
import com.xzp.forum.dao.UserDao;
import com.xzp.forum.model.Answer;
import com.xzp.forum.model.Topic;
import com.xzp.forum.model.User;
import com.xzp.forum.service.TopicsService;
import com.xzp.forum.service.TopicService;
import com.xzp.forum.service.UserService;
import com.xzp.forum.util.HostHolder;

@Controller
@RequestMapping("/topic")
public class TopicController {
	@Autowired
	private UserDao userDao;

	@Autowired
	private TopicDao topicDao;

	@Autowired
	private AnswerDao answerDao;

	@Autowired
	private MessageDao messageDao;

	@Autowired
	HostHolder hostHolder;

	@Autowired
	TopicsService topicsService;

	@Autowired
	private TopicService topicService;

	@Autowired
	private UserService userService;

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public String displayTopic(@PathVariable Long id, Model model) {
		User user = hostHolder.getUser();
		Topic topic = topicService.getTopicById(id);
		List<Answer> answers = answerDao.findAnswerByTopic_Id(id);

		model.addAttribute("user", user);
		model.addAttribute("newMessage", messageDao.countMessageByToId(user.getId()));
		model.addAttribute("topic", topic);
		model.addAttribute("answers", answers);
		model.addAttribute("idUser", user.getId());
		model.addAttribute("userDao", userDao);
		return "topic";
	}

	@PostMapping("/{id}/delete")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN') or @topicService.hasModeratorPermission(#id)")
	public String deleteTopic(@PathVariable Long id) {
		topicService.deleteTopic(id);
		return "删除成功";
	}

	@PostMapping("/{id}/lock")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN') or @topicService.hasModeratorPermission(#id)")
	public String lockTopic(@PathVariable Long id) {
		topicService.lockTopic(id);
		return "锁定成功";
	}

	@PostMapping("/{id}/unlock")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN') or @topicService.hasModeratorPermission(#id)")
	public String unlockTopic(@PathVariable Long id) {
		topicService.unlockTopic(id);
		return "解锁成功";
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.POST)
	public View updateAnswer(@RequestParam String id_topic, @RequestParam String action, @RequestParam String id_answer,
			@RequestParam(required = false) String state, HttpServletRequest request) {
		switch (action) {
		case "useful":
			answerDao.setUsefulForAnswer(!Boolean.valueOf(state), Long.valueOf(id_answer));
			break;
		case "delete":
			answerDao.deleteAnswerById(Long.valueOf(id_answer));
			break;
		}
		String contextPath = request.getContextPath();
		return new RedirectView(contextPath + "/topic/" + id_topic);
	}

	@RequestMapping(path = "/add", method = RequestMethod.POST)
	public View addAnswer(@RequestParam("content") String content, @RequestParam("code") String code,
			@RequestParam("id_topic") String id_topic, @RequestParam("id_user") String id_user,
			HttpServletRequest request) {
		topicsService.addAnswer(content, code, id_topic, id_user);
		String contextPath = request.getContextPath();
		return new RedirectView(contextPath + "/topic/" + id_topic);
	}

	@RequestMapping(path = "/message", method = RequestMethod.GET)
	public View topicTransform(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return new RedirectView(contextPath + "/message");
	}
}
