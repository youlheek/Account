package com.example.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.Redis;
import redis.embedded.RedisServer;

@Configuration
public class LocalRedisConfig {
    @Value("${spring.redis.port}") // 설정파일에 이 경로를 넣어줄 것 (application.yml)
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws Exception {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
