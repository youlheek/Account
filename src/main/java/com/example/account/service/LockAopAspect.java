package com.example.account.service;

import com.example.account.aop.AccountLockIdInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    // pjp.proceed()가 어느 포인트지점에서 동작할건지를 집어줌
    // ~.AccountLock 부근(Around)에서 실행할거야!
    // before 인지 after인지는 코드로 명시할거야!
    @Around("@annotation(com.example.account.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            AccountLockIdInterface request) throws Throwable {

        // lock 취득 시도
        lockService.lock(request.getAccountNumber());
        try {
            // 실제 동작 before
            return pjp.proceed();
            // 실제 동작 after
        } finally {
            // lock 해제
            lockService.unlock(request.getAccountNumber());
        }
    }
}
