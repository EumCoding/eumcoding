package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.model.QuestionComment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
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

    // 댓글 작성 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "질문 게시물 댓글 작성 요청 DTO")
    public static class WriteRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "질문게시물 ID", example = "1")
        private int questionId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 대댓글 작성 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "질문 게시물 대댓글 작성 요청 DTO")
    public static class WriteReplyRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "부모 댓글 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "게시물 ID", example = "1")
        private int questionId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @PositiveOrZero(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "부모 댓글의 step", example = "0")
        private int step;

    }

    // 댓글 수정 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "질문 게시물 댓글 수정 요청 DTO")
    public static class updateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "질문게시물 ID", example = "1")
        private int questionCommentId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 댓글 삭제 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "질문 게시물 댓글 삭제 요청 DTO")
    public static class deleteRequestDTO {
        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "질문게시물 ID", example = "1")
        private int questionCommentId;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QnACommentListDTO{
        private int id; //질문답변 아이디
        private int questionId;//질문 아이디
        private String nickname;
        private String title;
        private String content;
        private int isMyComment; // 내가 쓴 댓글인지 확인
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createDay;

        public QnACommentListDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.questionId = (int) objects[1];
            this.title = (String) objects[2];
            this.content = (String) objects[3];
            this.createDay = timestampToLocalDateTime((Timestamp) objects[4]);
            this.nickname = (String) objects[5];
        }

        public QnACommentListDTO(QuestionComment questionComment, int isMyComment) {
            this.id = questionComment.getId();
            this.questionId = questionComment.getQuestion().getId();
            this.title = questionComment.getQuestion().getTitle();
            this.content = questionComment.getContent();
            this.createDay = questionComment.getCreatedDay();
            this.nickname = questionComment.getMember().getNickname();
            this.isMyComment = isMyComment;
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }
}
