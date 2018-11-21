package com.wei.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * 交集并集的一些操作
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisList {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void sadd() {
        redisTemplate.opsForSet().add("person1","hu","xiaoming","gao" );
        redisTemplate.opsForSet().add("person2","dawei","xiaoming","wanger" );
        System.out.println(redisTemplate.opsForSet().members("person1"));
        System.out.println(redisTemplate.opsForSet().members("person2"));
    }


    @Test
    public void sdiff() {
        //diff keyb keya keyc 两个集合的差
        Set difference = redisTemplate.opsForSet().difference("person1", "person2");
        System.out.println(redisTemplate.opsForSet().members("person1"));
        System.out.println(redisTemplate.opsForSet().members("person2"));
        System.out.println(difference);

    }

    @Test
    public void sdiffstore() {
        //sdiffstore keyc keya keyb 两个集合的差存入数据库keyc
        redisTemplate.opsForSet().differenceAndStore("person2","person1", "personDiff");
        System.out.println(redisTemplate.opsForSet().members("personDiff"));
    }

    @Test
    public void intersect() {
        //sinter keyb keya keyc 两个集合的交集
        Set difference = redisTemplate.opsForSet().intersect("person1", "person2");
        System.out.println(difference);

    }

    @Test
    public void intersectStore() {
        //sinterStore keyc keya keyb 两个集合的交集存入数据库keyc
        redisTemplate.opsForSet().intersectAndStore("person2","person1", "personDiff");
        System.out.println(redisTemplate.opsForSet().members("personDiff"));
    }

    /**
     * 两集合并集
     */
    @Test
    public void sunsion() {
        //sunsion keyb keya keyc 两个集合的并集存入数据库
        Set union = redisTemplate.opsForSet().union("person1", "person2");
        System.out.println(union);
    }

    @Test
    public void sunsionStore() {
        //sunsion keyb keya keyc 两个集合的并集存入数据库keyc
        redisTemplate.opsForSet().unionAndStore("person2", "person1", "person5");
        System.out.println(redisTemplate.opsForSet().members("person5"));

    }


    @Test
    public void smember() {
        //smember keya 获取元素所有元素
        System.out.println(redisTemplate.opsForSet().members("person1"));
    }

    @Test
    public void srem() {
        //srem key value删减某元素的某个值
        Long remove = redisTemplate.opsForSet().remove("person1", "hu");
        System.out.println(redisTemplate.opsForSet().members("person1"));
    }
}
