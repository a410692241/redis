package com.wei.redis.service;

import com.wei.redis.bo.User;
import com.wei.redis.bo.UserExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface UserService {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);
    User selectByPrimaryKey(Integer id);

    User unless100(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);
    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
