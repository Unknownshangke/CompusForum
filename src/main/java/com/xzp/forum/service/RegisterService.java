package com.xzp.forum.service;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.xzp.forum.dao.UserDao;
import com.xzp.forum.model.User;
import com.xzp.forum.service.QiniuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册的service层
 *
 * @author xiezhiping
 *
 */
@Service
public class RegisterService {
	private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

	@Autowired
	UserDao userDao;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	QiniuService qiniuService;

	public String reg(String username, String password, String email, String introduction, MultipartFile avatar) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setEmail(email);
		user.setRole("USER");
		user.setAdmin(false);
		user.setModerator(false);

		if (Objects.equals(introduction, "")) {
			user.setIntroduction(null);
		} else {
			user.setIntroduction(introduction);
		}

		// 处理头像
		if (avatar != null && !avatar.isEmpty()) {
			try {
				// 验证文件类型
				String contentType = avatar.getContentType();
				if (contentType != null && "image/png".equals(contentType)) {
					// 生成文件名：用户名_时间戳.png
					String timestamp = String.valueOf(System.currentTimeMillis());
					String fileName = "avatars/" + username + "_" + timestamp + ".png";
					String avatarUrl = qiniuService.saveImage(avatar, fileName);
					if (avatarUrl != null && !avatarUrl.isEmpty()) {
						user.setAvatar(avatarUrl);
					} else {
						logger.error("头像上传失败，使用默认头像");
						user.setAvatar("/images/default-avatar.png");
					}
				} else {
					logger.error("不支持的头像文件类型: " + contentType);
					user.setAvatar("/images/default-avatar.png");
				}
			} catch (IOException e) {
				logger.error("头像上传异常: " + e.getMessage());
				user.setAvatar("/images/default-avatar.png");
			}
		} else {
			user.setAvatar("/images/default-avatar.png");
		}

		user.setCreatedDate(new Date());
		userDao.createUser(user);
		return "/login";
	}
}
