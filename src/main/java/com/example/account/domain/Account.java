package com.example.account.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity // 자바 객체처럼 보이지만 사실은 설정파일이지롱!
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account { // 이 클래스의 멤버변수처럼 보이는 것들은 테이블의 컬럼이 됨
    @Id // Account라는 테이블의 PK를 지정하겠다
    @GeneratedValue
    Long id;

    private String accountNumber;

    private ACcountStatus aCcountStatus;




}
