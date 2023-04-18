package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
