package com.example.account.service;

import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {

    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndUnlock_evenIfThrow() throws Throwable {
        //given (lock과 unlock에 들어갈 계좌번호)
        ArgumentCaptor<String> lockArgumentCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unLockArgumentCaptor =
                ArgumentCaptor.forClass(String.class);
        UseBalance.Request request =
                new UseBalance.Request(123L, "1234", 1000L);

        // Error케이스를 발생시켜보자면
        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));


        //when
        assertThrows(AccountException.class, () ->
        lockAopAspect.aroundMethod(proceedingJoinPoint, request));

        //then
        verify(lockService, times(1))
                .lock(lockArgumentCaptor.capture());
        verify(lockService, times(1))
                .unlock(unLockArgumentCaptor.capture());

        assertEquals("1234", lockArgumentCaptor.getValue());
        assertEquals("1234", unLockArgumentCaptor.getValue());

    }

}