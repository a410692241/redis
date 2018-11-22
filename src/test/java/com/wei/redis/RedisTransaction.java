package com.wei.redis;

import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTransaction {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void transactionTest() {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.watch("mn");
                redisOperations.multi();
                redisOperations.opsForValue().get("mn");
                redisOperations.opsForValue().set("mn",10000);
                //以下exec是以上操作的结果集
                List exec = redisOperations.exec();
                System.out.println(exec);
                return null;
            }
        });
    }

    /**
     * redis流水线操作
     */
    @Test
    public void setTest() {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                long preTime = System.currentTimeMillis();
                int i = 0;
                while (true) {
                    long newTime = System.currentTimeMillis();
                    if (newTime - preTime > 1000) {
                        break;
                    }
                    redisTemplate.opsForValue().set("name", i++);
                }
                //1371次
                System.out.println(i+"次");
                return null;
            }
        });

    }


    /**
     * paper赋值速度
     */
    @Test
    public void executePipelined() {
        redisTemplate.executePipelined(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                long preTime = System.currentTimeMillis();
                int i = 0;
                while (true) {
                    long newTime = System.currentTimeMillis();
                    if (newTime - preTime > 1000) {
                        break;
                    }
                    redisTemplate.opsForValue().set("name", i++);
                }
                //68721次
                System.out.println(i+"次");
                return null;
            }
        });
    }

    @Test
    public void setTime() {
        redisTemplate.opsForValue().set("name", 10);
        redisTemplate.expire("name", 25, TimeUnit.SECONDS);
    }
}
