package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService { // 실제로 락을 걸고 해제하는 로직을 담당 -> LockAopAspect에서 사용됨
    // 그리고 LockAopAspect는 AOP를 통해 특정 메서드에 자동으로 락을 적용하는 역할을 함

    private final RedissonClient redissonClient;
    // RedisRepositoryConfig 클래스에 등록된 Bean 이름과 같으면 자동으로 매칭됨
    // redissonClient()

    public void lock(String accountNumber) {
        
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            // (최대 1주동안 락을 기다려서 획득하기, 획득한 락을 15초동안 갖고있다 풀어주기, 시간 단위)

            if(!isLock) {
                log.error("=================Lock acquisition failed=================");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch(AccountException e) {
            throw e;
        } catch (Exception e) {
            log.error("Redis lock failed");
        }
    }

    public void unlock(String accountNumber) {
        log.debug("Unlock for accountNumber : {}", accountNumber);

        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private String getLockKey(String accountNumber) {
        return "ACLK" + accountNumber;
        // accountLock + accountNumber 로 락 넘버링 만들기
    }
}
