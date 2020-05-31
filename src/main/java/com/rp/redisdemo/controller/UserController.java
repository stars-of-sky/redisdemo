package com.rp.redisdemo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rp.redisdemo.common.contants.ExceptionCodeEnum;
import com.rp.redisdemo.common.exception.CustomException;
import com.rp.redisdemo.entity.User;
import com.rp.redisdemo.entity.common.Result;
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
    public Result getUserList(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) {
        PageHelper.startPage(start, size, "id desc");
        List<User> users = userService.getAll();
        PageInfo<User> pageInfo = new PageInfo(users);
//        return pageInfo;
        return Result.ok().date("data", pageInfo).setMessage("用户列表");
    }

    @PostMapping("/save")
    public void addUser(@RequestBody User user) {
        if (Objects.isNull(user)) {
            throw new CustomException(ExceptionCodeEnum.INVALID_PARAM);
        }
//        userService.addUser(user);
        Result.ok().message("新增成功");
    }

    @PutMapping()
    public void updateUser(@RequestBody User user) {
        if (Objects.isNull(user) || (Objects.nonNull(user) && Objects.nonNull(user.getId()))) {
            throw new CustomException(ExceptionCodeEnum.INVALID_PARAM);
        }
        userService.updateUserById(user);
        Result.ok().message("更新成功");
    }

    /* @GetMapping("/{id}")
     public User getUserById(@PathVariable long id) {
         return userService.getUserById(id);
     }*/
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable long id) {
        return Result.ok().date("user", userService.getUserById(id));
    }
}
