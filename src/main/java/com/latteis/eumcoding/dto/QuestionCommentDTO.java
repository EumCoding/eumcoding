package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QnAAnswerListDTO{
        private int qnaId; //질문답변 아이디
        private int answerId;
        private int questionId;//질문 아이디
        private int memberId;
        private int lectureId;
        private String title;
        private String nickname;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime date;
        private String lectureName;
    }
}
