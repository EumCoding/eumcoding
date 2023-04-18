package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ReviewCommentDTO {

    @Getter
    @NoArgsConstructor
    @ApiModel(value = "리뷰 댓글 Id 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "리뷰 댓글 ID", example = "1")
        private int id;

    }

    // 리뷰 댓글 작성 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "리뷰 댓글 등록 요청")
    public static class WriteRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "리뷰 ID", example = "1")
        private int reviewId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 리뷰 댓글 수정 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "리뷰 댓글 수정 요청")
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "수정할 리뷰 댓글 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 리뷰 댓글 목록 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "리뷰 댓글 목록 응답")
    public static class ListCommentResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강사 ID", example = "1")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "닉네임", example = "닉네임입니다")
        private String nickname;

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime commentDay;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "0 : 수정 안 됨, 1 : 수정됨", example = "1")
        private boolean modified;

        public ListCommentResponseDTO(Object[] objects) {
            this.memberId = (int) objects[0];
            this.content = (String) objects[1];
            this.nickname = (String) objects[2];
            this.commentDay = timestampToLocalDateTime((Timestamp) objects[3]);
            this.modified = (boolean) objects[4];
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }

}
