package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AccountDto { // Account라는 Entity의 유사 객체
    // Controller <-> Service 간의 데이터를 주고 받는데 최적화된 DTO

    private Long userId;
    private String accountNumber;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    // 특정 Entity에서 특정 메서드로 변환시키기 위해서
    // 생성자를 쓰지 않고 static 메서드를 통해서 생성해주면 좀 더 깔끔해짐
    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregistedAt())
                .build();
    }

 /*   public AccountDto(Long userId, String accountNumber, Long balance, LocalDateTime registeredAt, LocalDateTime unregisteredAt) {
        this.userId = userId;
        this.accountNumber = accountNumber;

    }*/
    // 이렇게 생성자를 통해서 DTO를 만들수도 있지만,
    // 보통의 경우에는 엔티티를 받아와서 DTO를 생성하는 경우가 많기 때문에
}
