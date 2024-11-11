package com.example.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class DeleteAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10) // 10자리
        private String accountNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor // 파라미터가 없는 기본 생성자를 만들어줌
    @AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자를 만들어줌
    @Builder
    public static class Response {
        private Long userId;
        private String accountNumber;
        private LocalDateTime unRegisteredAt;

        // AccountDto를 CreateAccount.Response 객체로 변환해줌
        public static Response from(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unRegisteredAt(accountDto.getUnregisteredAt())
                    .build();
        }
    }
}
