package com.wei.redis.service.Impl;

import com.wei.redis.bo.User;
import com.wei.redis.bo.UserExample;
import com.wei.redis.dao.UserDao;
import com.wei.redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public long countByExample(UserExample example) {
        return userDao.countByExample(example);
    }
    @Override
    public int deleteByExample(UserExample example) {
        return userDao.deleteByExample(example);
    }

    @CacheEvict(key = "'user'", value = "cache")
    @Override
    public int deleteByPrimaryKey(Integer id) {
        System.out.println("查询了数据库");
        return userDao.deleteByPrimaryKey(id);
    }

    /**更新一定要删除集合缓存,同时更新该条数据
     * @param user
     * @return
     */
    @CachePut(key = "'user'+#p0.id",value = "cache")
    @CacheEvict(key = "'user'", value = "cache")
    @Override
    public int insert(User user) {
        System.out.println("查询了数据库");
        return userDao.insert(user);
    }

    @Override
    public int insertSelective(User record) {
        return userDao.insertSelective(record);
    }


    @Cacheable(key = "'user'",value = "cache")
    @Override
    public List<User> selectByExample(UserExample example) {
        System.out.println("查询了数据库");
        return userDao.selectByExample(example);
    }

    /**condition表示后面这个表达式满足的话使用缓存,id!= 6的缓存
     * //  @Cacheable(key = "'user'+#id", value = "cache",condition = "#id != 6")
     * @param id
     * @return
     */
    @Cacheable(key = "'user'+#id", value = "cache")
    @Override
    public User selectByPrimaryKey(Integer id) {
        System.out.println("查询了数据库");
        return userDao.selectByPrimaryKey(id);
    }

    /**unless只缓存结果中不满足条件的
     * @Cacheable(key = "'user'+#id", value = "cache",unless = "#result.age < 100")
     * @param id
     * @return
     */
    @Override
    @Cacheable(key = "'user'+#id", value = "cache")
    public User unless100(Integer id) {
        System.out.println("查询了数据库");
        return userDao.selectByPrimaryKey(id);
    }

    @Override
    public int updateByExampleSelective(User record, UserExample example) {
        return userDao.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(User record, UserExample example) {
        return userDao.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(User record) {
        return userDao.updateByPrimaryKeySelective(record);
    }


    /**CachePut一定会添加到缓存中,而且将返回结果放到缓存中,多用于添加和修改的方法
     * @param record
     * @return
     */
    @CachePut(key = "'user'+#id", value = "cache")
    @Override
    public int updateByPrimaryKey(User record) {
        System.out.println("查询了数据库");
        return userDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public Object login(User user) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo("");
        User userRS = userDao.selectByExample(userExample).stream().findFirst().orElse(null);
        if (userRS.getUsername().equals(user.getPassword())) {
            return true;
        }
        return false;
    }
}
