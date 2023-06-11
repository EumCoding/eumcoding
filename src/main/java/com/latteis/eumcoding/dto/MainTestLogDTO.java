package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainTestLogDTO {
    private int mainTestLogId; // 사용자에게 고유하게 부여되는 값

    private int memberId;

    private int mainTestListId;

    private String snbAnswer; // 제출한 답변 기록
}
