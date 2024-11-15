package com.example.account.domain;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // 자바 객체처럼 보이지만 사실은 설정파일이지롱!
@Getter
@Setter // 웬만하면 닫아주는게 좋다~~~ : 데이터베이스 영속성을 어떻게 관리할지
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
//@EntityListeners(AuditingEntityListener.class) -> BaseEntity에 설정했기 때문에 주석처리
// AuditingEntityListener는 config에 설정을 넣어줘야만 사용할 수 있다
// JpaAuditingConfiguration 파일
public class Account extends BaseEntity{ // 이 클래스의 멤버변수처럼 보이는 것들은 테이블의 컬럼이 됨

//    @Id // Account라는 테이블의 PK를 지정하겠다
//    @GeneratedValue
//    Long id;

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
//    @CreatedDate
//    private LocalDateTime createdAt;
//    @LastModifiedDate
//    private LocalDateTime updatedAt;


    public void useBalance(Long amount) {
        if(amount > balance) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }

        balance -= amount;
    }

    public void cancelBalance(Long amount) {
        if(amount < 0) {
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }

        balance += amount;
    }
}
