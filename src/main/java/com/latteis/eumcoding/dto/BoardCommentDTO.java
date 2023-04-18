package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class BoardCommentDTO {

    // 댓글 아아디만 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "댓글 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "댓글 ID", example = "1")
        private int id;

    }

    // 댓글 작성 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "게시물 댓글 작성 요청 DTO")
    public static class WriteRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "게시물 ID", example = "1")
        private int boardId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 대댓글 작성 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "게시물 대댓글 작성 요청 DTO")
    public static class WriteReplyRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "부모 댓글 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "게시물 ID", example = "1")
        private int boardId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @PositiveOrZero(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "부모 댓글의 step", example = "0")
        private int step;

    }

    // 댓글 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "게시물 댓글 수정 요청 DTO")
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "수정할 댓글 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 대댓글 목록 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "게시물 대댓글 목록 요청 DTO")
    public static class ListRequestDTO {

        @ApiModelProperty(value = "최상단 댓글 요청이면 0. 아니라면 댓글의 step", example = "0")
        private int step;

        @ApiModelProperty(value = "수정할 댓글 ID", example = "1")
        private int id;

    }

    // 댓글 목록 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "게시물 댓글 목록 응답 DTO")
    public static class ListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "댓글 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "작성자 ID", example = "1")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "닉네임", example = "닉네임입니다")
        private String nickname;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime commentDay;

        @ApiModelProperty(value = "답글이 존재하면 trye, 아니면 false", example = "0")
        private boolean existReply;

        public ListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.memberId = (int) objects[1];
            this.nickname = (String) objects[2];
            this.content = (String) objects[3];
            this.commentDay = timestampToLocalDateTime((Timestamp) objects[4]);
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }

    // 내가 작성한 댓글 목록 응답 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "내가 작성한 게시판 댓글 목록 응답 DTO")
    public static class MyListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "댓글 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "게시물 ID", example = "1")
        private int boardId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "제목", example = "제목입니다")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime commentDay;

        public MyListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.boardId = (int) objects[1];
            this.title = (String) objects[2];
            this.content = (String) objects[3];
            this.commentDay = timestampToLocalDateTime((Timestamp) objects[4]);
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }

}
