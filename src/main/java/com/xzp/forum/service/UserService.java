package com.xzp.forum.service;

import com.xzp.forum.model.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    void updateUser(User user);
    User getUserByUsername(String username);
    void createUser(User user);
    void deleteUser(Long id);
}
