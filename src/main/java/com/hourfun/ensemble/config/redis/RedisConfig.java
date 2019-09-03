package com.hourfun.ensemble.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.io.IOException;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {
    private final Environment env;
    private String redisHost;
    private int redisPort;
    private int redisDatabase;

    @PostConstruct
    public void setConnectionInfo() throws IOException {
        this.redisHost = env.getProperty("spring.redis.host", String.class, "localhost");
        this.redisPort = env.getProperty("spring.redis.port", int.class, 6379);
        this.redisDatabase = env.getProperty("spring.redis.database", int.class, 0);
    }

    /**
     * 만약 여러개의 database 쓸려면 2개 이상 factory, template 만들 것
     * 하나이상이면 default로 쓸 factory에 @Primary 추가
     * @return
     */
    @Bean(name="redisConnectionFactory")
    public LettuceConnectionFactory redisConnectionFactory() {
        final RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        redisConfig.setDatabase(redisDatabase);
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean(name="redisTemplate")
    public <K, V> RedisTemplate<K, V> redisTemplate() {
        return getRedisTemplate(redisConnectionFactory());
    }


    private <K, V> RedisTemplate<K, V> getRedisTemplate(final LettuceConnectionFactory connectionFactory) {
        final RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
