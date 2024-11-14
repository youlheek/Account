package com.example.account.dto;

import com.example.account.aop.AccountLockIdInterface;
import com.example.account.type.TransactionResultType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class UseBalance {
    /*{
        "userId":1,
        "accountNumber":"1000000000",
        "amount":1000
    }*/
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockIdInterface {
        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10) @Max(1000_000_000)
        private Long amount;
    }


    /*
    {
        "accountNumber":"1234567890",
        "transactionResult":"S",
        "transactionId":"c2033bb6d82a4250aecf8e27c49b63f6",
        "amount":1000,
        "transactedAt":"2022-06-01T23:26:14.671859"
    }
    */
    @Getter
    @Setter
    @NoArgsConstructor // 파라미터가 없는 기본 생성자를 만들어줌
    @AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자를 만들어줌
    @Builder
    public static class Response {
        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResultType(transactionDto.getTransactionResultType())
                    .amount(transactionDto.getAmount())
                    .transactionId(transactionDto.getTransactionId())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
