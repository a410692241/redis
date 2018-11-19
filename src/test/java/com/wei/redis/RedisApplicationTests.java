package com.wei.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTests {
    private static Jedis jedis = new Jedis("192.168.9.128", 6379);
    //设置密码,在redis.conf 的required注释给去掉了

    static {
        jedis.auth("root");
    }

    @Test
    public void contextLoads() {

    }

    @Test
    public void redisCtrl() {
        jedis.set("money", "1000");
        jedis.close();
    }

    /**
     * redis测试性能
     */
    @Test
    public void testRun() {
        int i = 0;
        long time = System.currentTimeMillis();
        while (true) {
            long newTime = System.currentTimeMillis();
            if (newTime - time > 1000) {
                break;
            }
            i++;
            jedis.set("test" + i, i + "");
        }
        jedis.close();
        //一秒操作1956次,因为代码运行的瓶颈,官方描述可以写入8万次
        System.out.println("一秒的操作:"+i+"次");
    }

}
