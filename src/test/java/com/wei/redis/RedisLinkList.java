package com.wei.redis;

import com.sun.media.sound.SoftTuning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.stubbing.defaultanswers.ReturnsDeepStubs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import sun.java2d.opengl.OGLSurfaceData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLinkList {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * rpush向右插入的结果:[hu,liao,xiaoming,weilao,gao]
     * lpush就和插入的顺序相反了[gao,weilao,xiaoming,liao,hu]
     */
    @Test
    public void ipushALl() {
        List<String> list = new ArrayList<>();
        list.add("hu");
        list.add("liao");
        list.add("xiaoming");
        list.add("weilao");
        list.add("gao");
//        redisTemplate.opsForList().leftPush("person", "abc");
        redisTemplate.opsForList().leftPushAll("person", list);
//        System.out.println(aLong);
        String person0 = (String) redisTemplate.opsForList().index("person", 0);
//        //gao
        System.out.println("person0:"+person0);
    }


    /**
     * 获取集合下标之间的值
     */
    @Test
    public void getRange() {
        List persons = redisTemplate.opsForList().range("person", 0, 4);
        System.out.println(persons);
    }

    /**
     *
     */
    @Test
    public void lSize() {
        Long person = redisTemplate.opsForList().size("person");
        System.out.println("size"+person);
    }


    /**
     * 向左或者向右一个接一个得剔除集合值
     */
    @Test
    public void LOrRPop() {
        Long aLong = redisTemplate.opsForList().leftPushAll("person");
        System.out.println(aLong);
    }

    /**
     * index
     */
    @Test
    public void index() {
        //获取最左边元素
        Object person = redisTemplate.opsForList().index("person", 0);
        System.out.println("最左边元素是:"+person);
    }


    /**
     * 获取所有的元素
     */
    @Test
    public void getAllElement() {
        List range = redisTemplate.opsForList().range("person",0 , redisTemplate.opsForList().size("person"));
        System.out.println(range);
    }

    /**
     * trim截端保留,留下0到3之间的元素
     */
    @Test
    public void trim() {
        redisTemplate.opsForList().trim("person",0,3);
        System.out.println(redisTemplate.opsForList().range("person",0,5));
    }


    /**从左拿出1个元素
     * 10秒的时间没有拿到东西就退出
     */
    @Test
    public void leftPop() {
        //有个重载方法,第二三个参数是描述一个时间(阻塞操作)
        Object person = redisTemplate.opsForList().leftPop("person", 10, TimeUnit.SECONDS);
        System.out.println(person);
    }


}
