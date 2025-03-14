package com.xzp.forum.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import com.xzp.forum.service.RegisterService;
import com.xzp.forum.service.QiniuService;

/**
 * 注册接口
 *
 * @author xiezhiping
 *
 */
@Controller
public class RegisterController {

	@Autowired
	RegisterService registerService;

	@Autowired
	QiniuService qiniuService;

	@RequestMapping(path = "/register", method = RequestMethod.GET)
	public String register() {
		return "register";
	}

	@RequestMapping(path = "/register", method = RequestMethod.POST)
	public View register(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("email") String email,
			@RequestParam("introduction") String introduction,
			@RequestParam(value = "avatar", required = false) MultipartFile avatar,
			HttpServletRequest request) {

		String contextPath = request.getContextPath();
		String action = registerService.reg(username, password, email, introduction, avatar);
		return new RedirectView(contextPath + action);
	}
}
