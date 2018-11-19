package com.wei.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisHash {
    @Autowired
    private RedisTemplate redisTemplate;

    private static Jedis jedis = new Jedis("192.168.9.128", 6379);

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
}
