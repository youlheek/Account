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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.account.type.AccountStatus.*;

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
        // 1. 사용자가 있는지 조회(확인)
        // 2. 계좌의 번호 생성
        // 3. 계좌 저장, 저장된 정보를 넘김

        AccountUser accountUser
                = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

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

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }
}
