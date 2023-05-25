package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class QuestionDTO {
    private int questionId; // 사용자에게 고유하게 부여되는 값

    private int lectureId;

    private String lectureName;

    private int memberId;

    private String nickname;

    private String title;

    private String content;

    private LocalDateTime updatedDay; // 수정된 날짜

    private LocalDateTime createdDay; // 생성된 날짜

    private String image; // 이미지가 저장된 경로

    private int answerCount; // 답변 갯수

    private List<MultipartFile> imgRequest;

    private String imgResponse;

    
    //질문작성, /lecture/question/write
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class writeQuestionDTO{
       private int lectureId;
       private String title;
       private String content;
       private String image;
       private List<MultipartFile> imgRequest;
      public boolean checkProfileImgRequestNull() {
            return this.imgRequest != null;
      }

    }


    //질문수정, /lecture/question/update
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateQuestionDTO{
        private int questionId;
        private String title;
        private String content;
        private String image;
        private List<MultipartFile> imgRequest;
        public boolean checkProfileImgRequestNull() {
            return this.imgRequest != null;
        }

    }

    //질문삭제, /lecture/question/delete
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class deleteQuestionDTO{
        private int questionId;
    }

    //내 질문 목록, /lecture/question/mylist
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyQuestionListDTO{
        private String nickname;
        private int qnaId;
        private String title;
        private int answer;// 0:안달림, 1:달림
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime date;
        private int lectureId;
        private String lectureName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QnAQuestionListDTO{
        private int qnaId;
        private int memberId;
        private int lectureId;
        private int answer;
        private String title;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime date;
        private String lectureName;
    }
}
