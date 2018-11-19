package com.wei.redis.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.context.annotation.Bean;

@EnableCaching
public class RedisConfiguration extends JCacheConfigurerSupport {

}
