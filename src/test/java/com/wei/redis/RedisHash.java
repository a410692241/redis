package com.wei.redis;

import jdk.jfr.events.ErrorThrownEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.PipedReader;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisHash {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getMKey() {
        //get mkey(mapkey)
        Map<String, Object> map = new HashMap<>();
        map.put("name", "hu");
        map.put("age", 22);
        map.put("sex", "男");
        redisTemplate.opsForValue().multiSet(map);
        List<String> list = new ArrayList<>();
        list.add("name");
        list.add("age");
        List valueList = redisTemplate.opsForValue().multiGet(list);
        //redis里可能keys * 的时候会有 \xac\xed\x00\x05t这样类似的key值,这些只是序列化的结果
        System.out.println(valueList);
    }


    @Test
    public void getOrSetHash() {
        redisTemplate.opsForHash().put("person", "name", "xiaoming");
        redisTemplate.opsForHash().put("person", "age", "22");
        redisTemplate.opsForHash().put("person", "sex", "男");
        Object o = redisTemplate.opsForHash().get("person", "name");
        System.out.println(o.toString());
    }

    /**
     * 一次获得多个属性
     */
    @Test
    public void multiGet() {
        HashMap<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("name");
        list.add("age");
        list.add("sex");
        List person = redisTemplate.opsForHash().multiGet("person", list);
        System.out.println(person);
    }


    @Test
    public void delete() {
        redisTemplate.opsForHash().put("person", "name", "xiaoming");
        Long delete = redisTemplate.opsForHash().delete("person", "name", "age");
        System.out.println("删除结果是:" + delete);
        List<String> list = new ArrayList<>();
        list.add("name");
        list.add("age");
        list.add("sex");
        System.out.println(redisTemplate.opsForHash().multiGet("person", list));
    }

    @Test
    public void hasKey() {
        Boolean aBoolean = redisTemplate.opsForHash().hasKey("person", "name");
        System.out.println("是否存在:" + aBoolean);
        List person = redisTemplate.opsForHash().values("person");
        System.out.println(person);
    }

    @Test
    public void values() {
        List person = redisTemplate.opsForHash().values("person");
        System.out.println(person);
    }

    @Test
    public void keys() {
//      获取属性的个数
        Set keys = redisTemplate.opsForHash().keys("person");
        System.out.println("属性的key:" + keys);
    }

    @Test
    public void size() {
        Long aLong = redisTemplate.opsForHash().size("person");
        System.out.println("size:" + aLong);
    }

    @Test
    public void lengthOfValue() {
        Long lengthOfValue = redisTemplate.opsForHash().lengthOfValue("person", "name");
        System.out.println("value:" + redisTemplate.opsForHash().get("person", "name"));
//      长度是序列化的长度
        System.out.println("lengthOfValue:" + lengthOfValue);
    }

    /**
     * 相当于hmgetAll获取全部 属性:值
     */
    @Test
    public void entries() {
        Map person = redisTemplate.opsForHash().entries("person");
        System.out.println(person);
    }

    /**
     * 比较序列化之后的key和value的长度,注意凡是求值的长度的时候.
     * 注意,可能你得到的并不是你想要的值,得到的只是序列化的值,是什么呢类似于\xac\xed\x00\x05t\x00\x06person
     */
    @Test
    public void getKeySerializer() {
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        String value = (String) redisTemplate.opsForHash().get("person", "name");
        byte[] serialize = keySerializer.serialize(value);
        System.out.println(serialize.toString().length());
        System.out.println("lengthOfValue:" + redisTemplate.opsForHash().lengthOfValue("person", "name"));
    }



    @Test
    public void set() {
        
    }

}
