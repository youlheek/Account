package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
//    private final LockService redisTestService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request) {
        // @Valid는 그냥 단다고 해서 끝난게 아니라
        // 그 뒤 파라미터인 Request 클래스에서 뭘 어떻게 Valid 해야할지를 알려줘야한다

        AccountDto accountDto = accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance()
        );

        return CreateAccount.Response.from(accountDto);
    }

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request) {

        AccountDto accountDto = accountService.deleteAccount(
                request.getUserId(),
                request.getAccountNumber()
        );

        return DeleteAccount.Response.from(accountDto);
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId) {

        return accountService.getAccountByUserId(userId)
                // 📍 코드 이해하고 넘어가자
                .stream().map(accountDto ->
                        AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());

    }

/*    @GetMapping("/get-lock")
    public String getLock() {
        return redisTestService.getLock();
    }*/

    @GetMapping("/create-account")
    public String createAccount() {
//        accountService.createAccount();
        return "Account created";
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id) {
        // @PathVariable은 이름이 같으면 명시적으로 생략할 수 있다

        return accountService.getAccount(id);
    }
}
