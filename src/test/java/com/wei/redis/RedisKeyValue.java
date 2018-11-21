package com.wei.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisKeyValue {
    @Autowired
    private RedisTemplate redisTemplate;

    private static Jedis jedis = new Jedis("192.168.9.128", 6379);

    /*设置密码,在redis.conf 的requirepass注释给去掉了
     *如果不设置密码,可以修改在redis.conf的protected-mode yes为no,即注解保护模式
     */
    @PostConstruct
    @PreDestroy
    public void init() {
        /*
         * 此方法用于指定Spring序列化
         */
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        jedis.auth("root");

    }


    @Test
    public void contextLoads() {

    }

    @Test
    public void redisCtrl() {
//        jedis.auth("root");
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
        System.out.println("一秒的操作:" + i + "次");
    }

    @Test
    public void getKey() {
        //opsForValue当前要操作什么数据类型,默认String

        //get key
//        redisTemplate.opsForValue().set("hu", "22");
        /*这里虽然money只有两个序列化的key
        1) "\xac\xed\x00\x05t\x00\x05money"
        2) "\xac\xed\x00\x05t\x00\x03age"
        但是spring照样会找到原始key值
        */
        String hu = (String) redisTemplate.opsForValue().get("money");
        System.out.println(hu);
    }

    @Test
    public void getMoney() {
        /*这里数据库只有
         1) "\xac\xed\x00\x05t\x00\x05money"
        2) "\xac\xed\x00\x05t\x00\x03age"
        *   两个key,但是能查到值,因为Spring对redis操作的时候(set),已经把key转化为序列化值,读取也是先通过序列化,再读
        */
        System.out.println(redisTemplate.opsForValue().get("money"));
    }

    @Test
    public void setMoney() {
        /*这里数据库只有
         1) "\xac\xed\x00\x05t\x00\x05money"
        2) "\xac\xed\x00\x05t\x00\x03age"
        *   两个key,但是能查到值,因为Spring对redis操作的时候(set),已经把key转化为序列化值,读取也是先通过序列化,再读
        */
        redisTemplate.opsForValue().set("money",1000);
        System.out.println(redisTemplate.opsForValue().get("money"));
    }



    @Test
    public void del() {
        //当前要操作String
        redisTemplate.delete("name");

    }

    /**
     * 获取长度,正确应先用程序获得,然后获取长度,解析不出来
     */
    @Test
    public void size() {
        Long age = redisTemplate.opsForValue().size("age");
        System.out.println(age);
    }


    /**
     * 先取值后赋值
     */
    @Test
    public void getset() {
        String age = (String) redisTemplate.opsForValue().getAndSet("age", "25");
        System.out.println(age);
    }

    /**
     * 追加,因为是对序列化的结果进行追加,解析不出来
     * 结果是"\xac\xed\x00\x05t\x00\x03age",注意age
     */
    @Test
    public void append() {
        redisTemplate.opsForValue().append("age", "abv");
        System.out.println(redisTemplate.opsForValue().get("age"));
    }

    /**
     * 自增
     */
    @Test
    public void increament() {

        //这里会报错,因为1000序列化之后是个特别长的字符串.所以应该用String类型显示
        redisTemplate.opsForValue().set("money", "10000");
        //自增
        redisTemplate.opsForValue().increment("money", 1000);
        String age = (String) redisTemplate.opsForValue().get("money");
        System.out.println(age);
    }

    /**
     * 自减
     */
    @Test
    public void decreament() {
        RedisConnection jedis = redisTemplate.getConnectionFactory().getConnection();
        //默认减1,返回结果
        Long decr = jedis.decr("money".getBytes());
        System.out.println(decr);
    }

    /**
     * jdk序列化
     */
    @Test
    public void jdkSerializer() {
        RedisConnection jedis = redisTemplate.getConnectionFactory().getConnection();

        //调用jdk对redis键 序列化方法
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        byte[] bytes = jedis.get(jdkSerializationRedisSerializer.serialize("money"));

        //结果是:�� sr java.lang.Integer⠤���8 I valuexr java.lang.Number������  xp  �
        //因为结果也是序列化的结果,所以解序列化之后
        System.out.println("结果是:"+new String(bytes));
        Integer deserialize = (Integer) jdkSerializationRedisSerializer.deserialize(bytes);

        //1000
        System.out.println(deserialize);
    }


}
