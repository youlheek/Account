package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.account.type.AccountStatus.*;
import static com.example.account.type.ErrorCode.*;

@Service
@RequiredArgsConstructor // 꼭 필요한 argument가 들어간 생성자를 생성해줌 ex)Final 같은거
// AccountRepository를 사용해서 데이터를 저장하는 클래스
public class AccountService {

    //    @Autowired @Inject : 예전에는 이 어노테이션을 사용했었음
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    /**
     * 1. 사용자가 있는지 조회(확인)
     * 2. 계좌의 번호 생성
     * 3. 계좌 저장, 저장된 정보를 넘김
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {

        AccountUser accountUser
                = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        validateCreateAccount(accountUser);

        // 가장 최근 계좌번호를 가져와서 그것보다 +1 인 숫자를 넣어줄 것임
        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");

        Account account = accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()
        );

        return AccountDto.fromEntity(account);
        // 사실 account 같은 일회성 변수는
        // 생성하지 않고 걍 fromEntity() 안에 넣어주는 것을 선호하심 (강사님 선호)
    }

    // Exception 발생시키는 부분은 계속 더 추가될 수 있으니 따로 메서드로 뺀다!
    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) == 10) {
            throw new AccountException(USER_MAX_COUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }


    @Transactional
    public AccountDto deleteAccount(
            @NotNull @Min(1) Long userId, @NotBlank @Size(min = 10, max = 10) String accountNumber) {

        AccountUser accountUser
                = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account
                = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(UNREGISTERED);
        account.setUnregistedAt(LocalDateTime.now());

        accountRepository.save(account);
        // 필요 없는 코드이지만 Test에서 save(captor.capture())을 사용하기 위해서 씀

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if (accountUser.getId() != account.getAccountUser().getId()) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }
        if (account.getAccountStatus() == UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

    @Transactional // 이게 없으면 레이지로딩이 안되어서 제대로 로드가 안됨
    public List<AccountDto> getAccountByUserId(Long userId) {
        AccountUser accountUser
                = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        // 📍 stream을 왜 생성하는건지 모르겠다
        return accounts.stream()
                .map(AccountDto::fromEntity)
                // 위 코드 대신 .map(account -> AccountDto.fromEntity(account)) 도 가능
                .collect(Collectors.toList());
    }
}
