package com.wei.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * 有序集合,hu 9;js 19 通过数据后分数排序
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisZList {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void zadd() {
        Set<ZSetOperations.TypedTuple> set = new HashSet<>();
        ZSetOperations.TypedTuple<Object> tupleJava = new DefaultTypedTuple("java",10.0);
        ZSetOperations.TypedTuple<Object> tupleC = new DefaultTypedTuple("c",12.0);
        ZSetOperations.TypedTuple<Object> tupleJS = new DefaultTypedTuple("js",24.0);
        set.add(tupleC);
        set.add(tupleJS);
        set.add(tupleJava);
        redisTemplate.opsForZSet().add("name", set);
    }


    /**
     *获取下标内的数值
     */
    @Test
    public void zRange() {
        Set names = redisTemplate.opsForZSet().range("name", 0, 3);
        System.out.println(names);
    }

    /**
     * 权值内的数个数
     */
    @Test
    public void zCount() {
        Long zCount = redisTemplate.opsForZSet().count("name",20,30);
        System.out.println(zCount);
    }


    /**
     * 获取到数量
     */
    @Test
    public void zCard() {
        Long zCard = redisTemplate.opsForZSet().zCard("name");
        System.out.println(zCard);
    }

    /**
     * 增score,score值越大,越排在后面
     */
    @Test
    public void zIncrBy() {
        redisTemplate.opsForZSet().incrementScore("name", "js", 2);
        System.out.println(redisTemplate.opsForZSet().rangeByScore("name",0, 100));
    }

    /**
     * 获取权值
     */
    @Test
    public void getScore() {
        System.out.println(redisTemplate.opsForZSet().score("name", "js"));
    }

}
