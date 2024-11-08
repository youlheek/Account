package com.example.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTestService {
    private final RedissonClient redissonClient;
    // RedisRepositoryConfig 클래스에 등록된 Bean 이름과 같으면 자동으로 매칭됨
    // redissonClient()

    public String getLock() {
        RLock lock = redissonClient.getLock("sampleLock");

        try {
            boolean isLock = lock.tryLock(1, 5, TimeUnit.SECONDS);
            // (최대 1주동안 락을 기다려서 획득하기, 획득한 락을 5초동안 갖고있다 풀어주기, 시간 단위)
            if(isLock) {
                log.error("=================Lock acquisition failed=================");
                return "Lock failed";
            }
        } catch (Exception e) {
            log.error("Redis lock failed");
        }

        return "Lock success";
    }
}
