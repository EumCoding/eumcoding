package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class ReviewDTO {


    @Getter
    @NoArgsConstructor
    @ApiModel(value = "리뷰 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다")
        @ApiModelProperty(value = "리뷰 ID", example = "1")
        private int id;

    }

    // 내가 작성한 리뷰 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "내가 작성한 리뷰 요청 DTO")
    public static class MyListRequestDTO {
        @ApiModelProperty(value = "시작일", example = "2021-04-13 01:47:52")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startDate;

        @ApiModelProperty(value = "종료일", example = "2021-04-13 01:47:52")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endDate;
    }

    // 리뷰 작성 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "리뷰 작성 요청 DTO")
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
    @ApiModel(value = "리뷰 수정 요청 DTO")
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
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "리뷰 목록 응답 DTO")
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
        private LocalDateTime createdDay;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "좋아요", example = "1")
        private int heart;

        ReviewCommentDTO.ListCommentResponseDTO listCommentResponseDTO;

        public ListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.memberId = (int) objects[1];
            this.nickname = (String) objects[2];
            this.content = (String) objects[3];
            this.rating = (int) objects[4];
            this.createdDay = timestampToLocalDateTime((Timestamp) objects[5]);
            this.heart = Integer.parseInt(String.valueOf(objects[6]));
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
    }

    // 내가 작성한 리뷰 목록 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "내가 작성한 리뷰 목록 요청 DTO")
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

        @ApiModelProperty(value = "강의명", example = "강의명입니다")
        private String lectureName; // 강의명

        @ApiModelProperty(value = "강의 썸네일", example = "강의 썸네일입니다")
        private String lectureThumbnail; // 강의 썸네일

        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId; // 강의 ID

        private ReviewCommentDTO.ListCommentResponseDTO listCommentResponseDTO;

        public MyListResponseDTO(Object[] objects, String lectureName, String lectureThumbnail) {
            this.id = (int) objects[0];
            this.content = (String) objects[1];
            this.rating = (int) objects[2];
            this.createdDay = timestampToLocalDateTime((Timestamp) objects[3]);
            this.heart = Integer.parseInt(String.valueOf(objects[4]));
            this.lectureId = (int)objects[5]; // lectureId
            this.lectureName = lectureName;
            this.lectureThumbnail = lectureThumbnail;
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }



}
