package com.xzp.forum.service;

import com.xzp.forum.dao.UserDao;
import com.xzp.forum.model.User;
import com.xzp.forum.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 检查用户是否被封禁
        if (user.isBanned()) {
            Date now = new Date();
            Date unbanDate = user.getUnbanDate();

            if (unbanDate != null) {
                // 如果当前时间小于解封时间，说明还在封禁期内
                if (now.before(unbanDate)) {
                    // 计算剩余封禁时间（天）
                    long remainingTime = (unbanDate.getTime() - now.getTime()) / (24 * 60 * 60 * 1000);
                    throw new UsernameNotFoundException(String.format("账号已被封禁，剩余%d天\n封禁原因：%s\n解封时间：%s",
                        remainingTime,
                        user.getBanReason() != null ? user.getBanReason() : "未提供",
                        DateUtils.getParseDate(unbanDate)));
                } else {
                    // 封禁时间已过，自动解封
                    user.setBanned(false);
                    user.setBanReason(null);
                    user.setUnbanDate(null);
                    userDao.updateUser(user);
                }
            } else {
                // 永久封禁
                throw new UsernameNotFoundException(String.format("账号已被永久封禁\n封禁原因：%s",
                    user.getBanReason() != null ? user.getBanReason() : "未提供"));
            }
        }

        return user;
    }
}
