package com.example.account.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AccountInfo { // Account의 특정 정보들을 날라다줄 몇가지 정보만 뽑아서 사용자에게 응답을 주는
// 클라이언트 <-> 컨트롤러(어플리케이션) 간의 정보를 주고받을 응답
// 💡 전용 Dto를 만들지 않고 다목적 Dto를 만들다보면
// -> 너무 복잡한 상황이 생기고, 그런 상황을 처리하다보면 의도치 않은 동작을 하게 되어 에러가 발생될 확률이 높음
    private String accountNumber;
    private Long balance;
}
