package com.example.account.aop;

public interface AccountLockIdInterface {
    // 서로 다른 타입의 Request 객체를 공통적으로 사용하기 위해 만들어진 인터페이스

    String getAccountNumber();
    // UseBalance 클래스와 CancelBalance 클래스 에 있는 accountNumber를 동일하게 쓸 수 있는 메서드
    // -> 어떻게 클래스가 다른데 동일하게 사용할 수 있느냐?
    // => Request class에 @Getter가 선언되어있기 때문에 자동 생성된 getAccountNumber() 가 상속됨
}
