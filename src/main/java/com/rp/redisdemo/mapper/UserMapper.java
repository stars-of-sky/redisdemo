package com.rp.redisdemo.mapper;

import com.rp.redisdemo.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);


    com.rp.redisdemo.entity.User findUserById(Long id);

    List<com.rp.redisdemo.entity.User> findAll();

    int updateById(com.rp.redisdemo.entity.User user);

    void deleteById(Long id);
}