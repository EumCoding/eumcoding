package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainTestMultipleChoiceViewDTO {

    private int mainTestListMultipleId; // 사용자에게 고유하게 부여되는 값

    private int mainTestListId;

    private String content; // 내용

    private int sequence; // 보기 순서. 0부터.
}
