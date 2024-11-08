package com.example.account.domain;

import com.example.account.type.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // 자바 객체처럼 보이지만 사실은 설정파일이지롱!
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
// AuditingEntityListener는 config에 설정을 넣어줘야만 사용할 수 있다
// JpaAuditingConfiguration 파일
public class Account { // 이 클래스의 멤버변수처럼 보이는 것들은 테이블의 컬럼이 됨

    @Id // Account라는 테이블의 PK를 지정하겠다
    @GeneratedValue
    Long id;

    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING) // Enum 값의 실제 문자열을 DB에 저장하게 됨
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unregistedAt;

    // @CreatedDate 와 @LastModifiedDate는
    // @EntityListeners 가 있어야만 사용할 수 있다
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;



}
