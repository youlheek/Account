package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// 스프리에서 JPA를 더 쓰기쉽게만들어줌
                                                    // < PK, 엔티티의 primary 타입>
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 📍 실제 구현은 어디에 있나요..?
    // 인프런 김영한 강의 - JPA 강의 듣기
    Optional<Account> findFirstByOrderByIdDesc();

    Optional<Account> findByAccountNumber(String AccountNumber);

    Integer countByAccountUser(AccountUser accountUser);

    // JPA 관련 기능 중 : Account에 연관관계로 포함된 AccountUser가 있기 때문에
    // 이 메서드가 이 인터페이스 안에서 자동으로 생성되는 것임
    List<Account> findByAccountUser(AccountUser accountUser);
}
