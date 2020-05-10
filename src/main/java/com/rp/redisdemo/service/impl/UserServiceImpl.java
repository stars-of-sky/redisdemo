package com.rp.redisdemo.service.impl;

/**
 * Create By yrp.
 * Date:2020/3/29
 */

import com.rp.redisdemo.entity.User;
import com.rp.redisdemo.mapper.UserMapper;
import com.rp.redisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 在Spring Boot中，使用Redis缓存，既可以使用RedisTemplate自己来实现，也可以使用使用这种方式，
 * 这种方式是Spring Cache提供的统一接口，实现既可以是Redis，也可以是Ehcache或者其他支持这种规范的缓存框架。
 * 从这个角度来说，Spring Cache和Redis、Ehcache的关系就像JDBC与各种数据库驱动的关系
 */

/**
 * @CacheConfig 这个注解在类上使用，用来描述该类中所有方法使用的缓存名称，当然也可以不使用该注解，
 * 直接在具体的缓存注解上配置名称
 * 当我们需要缓存的地方越来越多，你可以使用@CacheConfig(cacheNames = {"myCache"})注解来统一指定value的值，
 * 这时可省略value，如果你在你的方法依旧写上了value，那么依然以方法的value值为准。
 */
@Service
@CacheConfig(cacheNames = "c1")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void addUser(User user) {
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param id
     * @return
     * @Cacheable 这个注解一般加在查询方法上，表示将一个方法的返回值缓存起来，默认情况下，缓存的key就是方法的参数，
     * 缓存的value就是方法的返回值。
     * 当有多个参数时，默认就使用多个参数来做key，如果只需要其中某一个参数做key，则可以在@Cacheable注解中，
     * 通过key属性来指定key，如上代码就表示只使用id作为缓存的key，如果对key有复杂的要求，可以自定义keyGenerator。
     * @Cacheable注解会先查询是否已经有缓存，有会使用缓存，没有则会执行方法并缓存。
     */
    @Override
    @Cacheable(key = "#id")
    public User getUserById(Long id) {
        System.out.println("getUserById");
        return userMapper.findUserById(id);
    }

    @Override
    public List<User> getAll() {
        return userMapper.findAll();
    }

    /**
     * @CachePut 这个注解一般加在更新方法上，当数据库中的数据更新后，缓存中的数据也要跟着更新，
     * 使用该注解，可以将方法的返回值自动更新到已经存在的key上
     * 和 @Cacheable 不同的是，它每次都会触发真实方法的调用 。简单来说就是用户更新缓存数据。
     */
    @Override
    @CachePut(key = "#user.id")
    public void updateUserById(User user) {
        int count = userMapper.updateById(user);
    }

    /**
     * @CacheEvict 这个注解一般加在删除方法上，当数据库中的数据删除后，相关的缓存数据也要自动清除，
     * 该注解在使用的时候也可以配置按照某种条件删除（condition属性）或者或者配置清除所有缓存（allEntries属性）
     */
    @Override
    @CacheEvict()
    public void deleteUserById(Long id) {
        userMapper.deleteById(id);
    }

    // *********另种可以切库的方式*********************

    @Autowired
    private RedisTemplate redisTemplate0;

    @Autowired
    private RedisTemplate redisTemplate1;

    public void setDb0() {
    }
}
