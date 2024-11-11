package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    // AccountService 가 AccountRepository를 의존하고 있기 때문에 가짜로 만들어줘야 한다
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;
    // 위의 두개의 @Mock 을 AccountService에 담아주면서 AccountService mock 완성

    @Test
    void createAccountSuccess () {
        //given
        AccountUser user
                = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                                .accountUser(user)
                                .accountNumber("1000000012").build()));
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        // accountRepository를 1번 실행해서 저장할건데 그 부분을 capture할거야

        assertEquals(12L, accountDto.getUserId());
//        assertEquals("1000000013", accountDto.getAccountNumber());
        assertEquals("1000000013", captor.getValue().getAccountNumber());
    }

    @Test
    void createFirstAccountSuccess () {
        //given
        AccountUser user
                = AccountUser.builder()
                .id(15L)
                .name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        // accountRepository를 1번 실행해서 저장할건데 그 부분을 capture할거야

        assertEquals(15L, accountDto.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());
        // accountNumber를 "1000000013"으로 설정했는데도 테스트가 통과하는 이유
        // -> captor이 실행되는 시점이 accountRespository.save()이기 때문임
        // accountRespository.save() 전에
        // .willReturn(Optional.empty()) 로 설정했기 때문에 **기존 계좌가 없는 상태**로 간주되고, 따라서 새로운 계좌 번호는 10000000로 설정됨
        // 반환값으로 "1000000013"설정한 것은 save() 메서드가 호출된 후 반환되는 값일 뿐,
        // 저장하기 전의 Account 객체에는 여전히 "1000000000"이라는 계좌번호가 설정되어 있음
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound () {
        //given
        AccountUser user
                = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty()); // 유저 없음 -> AccountException 발생

        //when
        AccountException exception
                = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌가 10개 - 계좌 생성 실패")
    void createAccount_maxAccountIs10 () {
        //given
        AccountUser user
                = AccountUser.builder()
                .id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(user))
                .willReturn(10);

        //when
        AccountException exception
                = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.USER_MAX_COUNT_PER_USER_10, exception.getErrorCode());
    }
}
