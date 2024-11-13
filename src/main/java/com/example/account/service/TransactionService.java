package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /*- 정책 : 1. 사용자 없는 경우
             2. 사용자 아이디 != 계좌 소유주
             3. 계좌가 이미 해지 상태인 경우
             4. 거래금액 > 잔액
             5. 거래금액이 너무 작거나 큰 경우
            ➡️실패 응답
				*/
    @Transactional // 동시에 일어나거나 동시에 일어나지 않거나
    // 밑의 코드 상에서는 useBlanace (update) 와 save() (insert) 가 동시에 일어나거나 동시에 실패하거나
    public TransactionDto useBalance(
            Long userId, String accountNumber, Long amount){

        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 실패 응답 정책
        validateUseBalance(user, account, amount);

        // 거래금액 사용
        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(TransactionResultType.S, TransactionType.USE, amount, account));
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        // 2. 사용자 아이디 != 계좌 소유주
        if (!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        // 3. 계좌가 이미 해지 상태인 경우
        if (account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        // 4. 거래금액 > 잔액
        if (account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }

    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account =
                accountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionResultType.F, TransactionType.USE, amount, account);
    }

    private Transaction saveAndGetTransaction(
            TransactionResultType transactionResultType,
            TransactionType transactionType,
            Long amount, Account account) {

        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build());
    }


/*
    정책 : 1. 거래 아이디에 해당하는 거래가 없는 경우
         2. 계좌가 없는 경우, 거래와 계좌가 일치하지 않는 경우
         3. 거래금액과 거래 취소 금액이 다른경우(부분 취소 불가능)
         ➡️ 실패 응답
        - 1년이 넘은 거래는 사용 취소 불가능
        - 해당 계좌에서 거래(사용, 사용 취소)가 진행 중일 때
        다른 거래 요청이 오는 경우 해당 거래가 동시에 잘못 처리되는 것을 방지해야 한다.
    */
    @Transactional
    public TransactionDto cancelBalance(
            String transactionId, String accountNumber, Long amount) {

        Transaction transaction =
                transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));

        Account account =
                accountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancleBalance(transaction, account, amount);

        account.cancelBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(TransactionResultType.S, TransactionType.CANCLE, amount, account));
    }

    private void validateCancleBalance(Transaction transaction, Account account, Long amount) {

        // 2. 거래와 계좌가 일치하지 않는 경우
        if (!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH);
        }

        // 3. 거래금액과 거래 취소 금액이 다른경우(부분 취소 불가능)
        if (!Objects.equals(transaction.getAmount(), amount)) {
            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
        }

        // - 1년이 넘은 거래는 사용 취소 불가능
        if (transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(ErrorCode.TOO_OLD_ORDER_TO_CANCEL);
        }
    }

    @Transactional
    public void saveFailedCancelTransaction(
            String accountNumber, Long amount) {

        Account account =
                accountRepository.findByAccountNumber(accountNumber)
                                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionResultType.F, TransactionType.CANCLE, amount, account);
    }


/*
    해당 거래 아이디의 거래가 없는 경우
	➡️ 실패 응답
*/
    @Transactional
    public TransactionDto queryTransaction(String transactionId) {

        Transaction transaction =
                transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));

        return TransactionDto.fromEntity(transaction);
    }
}
