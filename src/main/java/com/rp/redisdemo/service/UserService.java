package com.rp.redisdemo.service;

import com.rp.redisdemo.entity.User;

import java.util.List;

/**
 * Create By yrp.
 * Date:2020/5/10
 */

public interface UserService {
    void addUser(User user);

    User getUserById(Long id);

    List<User> getAll();

    void updateUserById(User user);

    void deleteUserById(Long id);

}
