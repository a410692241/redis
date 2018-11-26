package com.wei.redis.controller;

import com.wei.redis.bo.User;
import com.wei.redis.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.concurrent.TimeUnit;

/**
 * 最基础的redis实现方法和springboot下的redis缓存使用方法
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserDao userDao;

    @RequestMapping("/")
    public Object index() {
        return "index";
    }



    /**通过从缓存查询数据
     * 缓存失效压力大的情况:某一时刻多个缓存失效,重建缓存,解决办法,超时时间错开
     *          某一时刻,某个热门缓存失效,重建缓存,使用分布式锁(redis/zookeeper)
     * @param id
     * @return
     */
    @RequestMapping("/getAll")
    @ResponseBody
    public Object getAll(int id) {
        String key = "user" + id;
        User user = (User) redisTemplate.opsForValue().get(key);

        if (user == null) {
            user = userDao.selectByPrimaryKey(id);
            System.out.println("查询了数据库");

            //重建该缓存
            //尝试获取分布式锁
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            String lockKey = "lock" + id;
            if (connection.setNX(lockKey.getBytes(), "true".getBytes())) {

                //说明获取到分布式锁,给锁设置默认时间*秒
                connection.expire(lockKey.getBytes(), 60);
                redisTemplate.opsForValue().set(key, user);
                redisTemplate.expire(key, 5, TimeUnit.MINUTES);
                redisTemplate.delete(lockKey);
            } else {

                //未取到锁,等待对象在redis重建
                while (user != null) {
                    user = (User) redisTemplate.opsForValue().get(key);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }

    /**修改或者添加商品同步redis
     * @param user
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Object save(User user) {
        int result;
        if (user.getId() != null) {
            result = userDao.updateByPrimaryKey(user);
        } else {
            result = userDao.insert(user);

        }
        redisTemplate.opsForValue().set("user" + user.getId(), user);
        redisTemplate.expire("user" + user.getId(), 5, TimeUnit.MINUTES);
        return result;
    }

    /**删除商品同步redis
     * @param user
     * @return
     */
    @RequestMapping("/del")
    @ResponseBody
    public Object del(User user) {
        Integer id = user.getId();
        int result = userDao.deleteByPrimaryKey(id);
        redisTemplate.delete("user"+id);
        redisTemplate.expire("user"+id,5, TimeUnit.MINUTES);
        return result;
    }

}
