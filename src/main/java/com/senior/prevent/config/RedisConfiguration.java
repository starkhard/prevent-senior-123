package com.senior.prevent.config;

import com.senior.prevent.model.FileModel;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfiguration {
    @Bean
    public RedisTemplate<Long, FileModel> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, FileModel> template = new RedisTemplate<>();
        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
