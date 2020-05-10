package com.rp.redisdemo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rp.redisdemo.entity.User;
import com.rp.redisdemo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Create By yrp.
 * Date:2020/3/29
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping(value = "/users")
    public PageInfo getUserList(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) {
        PageHelper.startPage(start, size, "id desc");
        List<User> users = userService.getAll();
        PageInfo<User> pageInfo = new PageInfo(users);
        return pageInfo;
    }

    @PostMapping("/save")
    public void addUser(@RequestBody User user) {
        if (Objects.isNull(user)) {
            return;
        }
        userService.addUser(user);
    }

    @PutMapping()
    public void updateUser(@RequestBody User user) {
        if (Objects.isNull(user) || (Objects.nonNull(user) && Objects.nonNull(user.getId()))) {
            return;
        }
        userService.updateUserById(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }
}
