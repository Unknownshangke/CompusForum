package com.xzp.forum.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.xzp.forum.dao.UserDao;
import com.xzp.forum.model.User;
import com.xzp.forum.util.HostHolder;
import com.xzp.forum.service.UserService;
import com.xzp.forum.dao.MessageDao;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageDao messageDao;

    @GetMapping("/users")
    public String userManagement(Model model) {
        User currentUser = hostHolder.getUser();

        // 检查用户是否为管理员
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/topics/all/1";
        }

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", currentUser);
        model.addAttribute("newMessage", messageDao.countMessageByToId(currentUser.getId()));
        return "admin/userManagement";
    }

    @PostMapping("/users/{userId}/role")
    @ResponseBody
    public String updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        // 获取当前登录用户
        User currentUser = hostHolder.getUser();

        // 检查用户是否为管理员
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "error:unauthorized";
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return "用户不存在";
        }

        switch (role) {
            case "ADMIN":
                user.setRole("ADMIN");
                break;
            case "MODERATOR":
                user.setRole("MODERATOR");
                break;
            case "USER":
                user.setRole("USER");
                break;
            default:
                return "无效的角色";
        }

        userService.updateUser(user);
        return "角色更新成功";
    }

    @PostMapping("/users/{userId}/moderator-sections")
    @ResponseBody
    public String updateModeratorSections(@PathVariable Long userId, @RequestParam("sections") List<String> sections) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "用户不存在";
        }

        if (!"MODERATOR".equals(user.getRole())) {
            return "该用户不是版务";
        }

        user.getModeratorSections().clear();
        sections.forEach(user::addModeratorSection);
        userService.updateUser(user);

        return "版块权限更新成功";
    }

    @GetMapping("/users/{userId}/moderator-sections")
    @ResponseBody
    public List<String> getModeratorSections(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return null;
        }
        return new ArrayList<>(user.getModeratorSections());
    }

    @PostMapping("/users/{userId}/ban")
    @ResponseBody
    public String banUser(@PathVariable Long userId, @RequestParam long endTime, @RequestParam String reason) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return "用户不存在";
            }

            if (user.isAdmin()) {
                return "不能封禁管理员";
            }

            user.setBanned(true);
            user.setBanReason(reason);

            // 设置封禁结束时间
            if (endTime > 0) {
                // 将时间戳转换为 Date 对象
                Date unbanDate = new Date(endTime);
                user.setUnbanDate(unbanDate);
            } else {
                // 永久封禁
                user.setUnbanDate(null);
            }

            userService.updateUser(user);
            return "用户已封禁";
        } catch (Exception e) {
            e.printStackTrace();
            return "封禁用户失败：" + e.getMessage();
        }
    }

    @PostMapping("/users/{userId}/unban")
    @ResponseBody
    public String unbanUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return "用户不存在";
            }

            user.setBanned(false);
            user.setBanReason(null);
            user.setUnbanDate(null);

            userService.updateUser(user);
            return "用户已解封";
        } catch (Exception e) {
            e.printStackTrace();
            return "解封用户失败：" + e.getMessage();
        }
    }

    @PostMapping("/users/{userId}/delete")
    @ResponseBody
    public String deleteUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "用户不存在";
        }

        if (user.isAdmin()) {
            return "不能删除管理员";
        }

        userService.deleteUser(userId);
        return "用户已删除";
    }
}
