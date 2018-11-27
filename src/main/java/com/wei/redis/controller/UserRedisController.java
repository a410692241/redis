package com.wei.redis.controller;

import com.wei.redis.bo.User;
import com.wei.redis.bo.UserExample;
import com.wei.redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 最基础的redis实现方法和springboot下的redis缓存使用方法
 * @author user
 */
@Controller
@RequestMapping("/userRedis")
public class UserRedisController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public Object All(Model model) {
        List<User> users = userService.selectByExample(new UserExample());
        model.addAttribute("userList", users);
        return "All";
    }


    @RequestMapping("/index")
    public Object index(Model model) {
        List<User> users = userService.selectByExample(new UserExample());
        model.addAttribute("userList", users);
        return "index";
    }

    /**多用于查询业务,如果该查询结果能在缓存服务器查询,如果返回服务器没该数据,则调用标记方法,根据方法返回值进行缓存的重建
     * @param id
     * @return
     */
    @RequestMapping("/cacheGet")
    @ResponseBody
    public Object cacheGet(int id) {
        User user = userService.selectByPrimaryKey(id);
        return user;
    }



    /**CachePut用于更新数据时候缓存进行更新
     * @param user
     * @return
     */
    @RequestMapping("/saveUser")
    public Object updateUser(User user) {
        if (user.getId() != null) {
             userService.updateByPrimaryKey(user);
        }else {
             userService.insert(user);
        }
        return "All";
    }



    /**CacheEvict用于更新数据时候缓存进行更新
     * @param id
     * @return
     */

    @RequestMapping("/delUser")
    public Object delUser(int id) {
        userService.deleteByPrimaryKey(id);
        return "All";
    }

    @RequestMapping("/onless100")
    @ResponseBody
    public Object onless100(int id) {
        return userService.unless100(id);
    }


    @RequestMapping("/lgin")
    public Object lgin(Model model) {
        return "login";
    }
    @RequestMapping("/login")
    @ResponseBody
    public Object login(User user) {
        return userService.login(user);
    }
}
