package com.wei.redis;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.URLDecoder;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserLua {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 执行lua脚本
     */
    @Test
    public void evalScript() {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        String luaStr = "redis.call('set',KEYS[1],ARGV[1])";
        //lua方式不会序列化key value
//        connection.eval(luaStr.getBytes(), ReturnType.VALUE, 1, "dog".getBytes(), "cat".getBytes());
        String luaGetStr = "return redis.call('get',KEYS[1])";
        byte[] eval = connection.eval(luaGetStr.getBytes(), ReturnType.VALUE, 1, "dog".getBytes());
        System.out.println(new String(eval));
    }


    /**
     * 缓存lua脚本
     */
    @Test
    public void evalScriptChe() {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        String luaStr = "redis.call('set',KEYS[1],ARGV[1])";
        String returnStr = connection.scriptLoad(luaStr.getBytes());
        //7cfb4342127e7ab3d63ac05e0d3615fd50b45b06
        System.out.println(returnStr);
    }

    /**
     * 执行缓存的lua
     */
    @Test
    public void useSriptChe() {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
//        String luaCheStr = "7cfb4342127e7ab3d63ac05e0d3615fd50b45b06";
//        connection.evalSha(luaCheStr, ReturnType.VALUE, 1, "dog".getBytes(), "cat".getBytes());
        String luaStr = "return redis.call('get',KEYS[1])";
        byte[] result = connection.eval(luaStr.getBytes(), ReturnType.VALUE, 1, "dog".getBytes());
        System.out.println(new String(result));
        System.out.println(redisTemplate.opsForValue().get("dog"));
    }

    /**
     * 以文件形式运行lua文件
     */
    @Test
    public void runLuaWithFile() throws IOException {
        //获取classes编译目录
        String path = this.getClass().getResource("/").getPath();
        System.out.println(path);
        String decodePath = URLDecoder.decode(path);
        String luaPath = decodePath + "luaScript.lua";
        File file = new File(luaPath);
        if (file.exists()) {
            System.out.println("文件存在");
        }

        //将脚本文件转化成byte数组
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteOutputStream byteOutputStream = new ByteOutputStream();
        int len;
        byte[] bytes = new byte[1024 * 10];
        while ((len = fileInputStream.read(bytes)) != -1) {
            byteOutputStream.write(bytes, 0, len);
        }
        byte[] byteArray = byteOutputStream.toByteArray();
        byteOutputStream.close();
        //获取redis连接
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();

        //执行脚本
        byte[] eval = connection.eval(byteArray, ReturnType.VALUE, 1, "dog".getBytes());
        System.out.println(new String(eval));
    }
}
