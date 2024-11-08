package com.example.account.repository;

import com.example.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// 스프리에서 JPA를 더 쓰기쉽게만들어줌
                                                    // < PK, 엔티티의 primary 타입>
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findFirstByOrderByIdDesc();


}
