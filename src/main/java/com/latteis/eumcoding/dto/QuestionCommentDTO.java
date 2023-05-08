package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class QuestionCommentDTO {
    private int answerId;

    private int questionId;

    private int memberId;

    private String nickname;

    private String content;

    private LocalDateTime updatedDay; // 수정된 날짜

    private LocalDateTime createdDay; // 생성된 날짜

    private int step;

    private int groupNum;
}
