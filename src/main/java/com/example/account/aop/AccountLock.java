package com.example.account.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // 이 어노테이션이 어디에 정의될 수 있는지 정의 -> 메서드에만 사용할 수 있어!
@Retention(RetentionPolicy.RUNTIME) // 이 어노테이션이 어느 시점까지 유지될 것인지를 정의
// -> 런타임까지 유지될거야 (런타임에 이 어노테이션을 읽을거야)
@Documented
@Inherited // 서브 클래스가 상속할 수 있을거야
// (부모 클래스에 이 어노테이션이 있을 경우, 자식 클래스도 자동으로 상속됨)
public @interface AccountLock {

    long tryLockTime() default 5000L;
    // 어노테이션의 속성 정의
    // 이 어노테이션을 사용할 때 특별히 값을 지정하지 않으면, 기본적으로 5000밀리초(5초)의 값을 사용
    // 속성 값을 지정하고 싶다면, 어노테이션 사용 시 @AccountLock(tryLockTime = 3000L)와 같이 명시
}
