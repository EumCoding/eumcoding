package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    @Getter
    @NoArgsConstructor
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다")
        @ApiModelProperty(value = "리뷰 ID", example = "1")
        private int id;

    }

    // 리뷰 작성 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class WriteRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "평점", example = "1")
        private int rating;

    }

    // 리뷰 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "리뷰 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "평점", example = "1")
        private int rating;

    }

    // 리뷰 목록 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class ListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "리뷰 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "유저 ID", example = "1")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "닉네임", example = "닉네임입니다")
        private String nickname;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "평점", example = "1")
        private int rating;

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime 작성일;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "좋아요", example = "1")
        private int heart;

//        @NotBlank("필수 입력 값입니다.")

    }

    // 내가 작성한 리뷰 목록 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MyListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "리뷰 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "평점", example = "1")
        private int rating;

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "좋아요", example = "1")
        private int heart;

        public MyListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.content = (String) objects[1];
            this.rating = (int) objects[2];
            this.createdDay = timestampToLocalDateTime((Timestamp) objects[3]);
            this.heart = Integer.parseInt(String.valueOf(objects[4]));
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }
    private int reviewId;

    private int memberId;

    private int lectureId;

    private String nickname;

    private String lectureName;

    private int rating;

    private String content;

    private int heart;

    private LocalDateTime createdDay;
}
