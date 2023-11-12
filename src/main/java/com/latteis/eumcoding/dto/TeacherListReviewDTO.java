package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class TeacherListReviewDTO {
    private long count;
    private List<ListTeacherResponseDTO> teacherMyReviewList;

    // 나에 대한 리뷰 목록 응답 DTO(강사입장에서)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "나에 대한 리뷰 목록 응답 DTO")
    public static class ListTeacherResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "리뷰 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강좌 ID", example = "1")
        private int lectureId;

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

        //ReviewCommentDTO.ListCommentResponseDTO listCommentResponseDTO;

     /*   public ListTeacherResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.lectureId = (int) objects[1];
            this.memberId = (int) objects[2];
            this.nickname = (String) objects[3];
            this.content = (String) objects[4];
            this.rating = (int) objects[5];
            this.createdDay = timestampToLocalDateTime((Timestamp) objects[6]);
            this.heart = Integer.parseInt(String.valueOf(objects[7]));
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
*/    }


}
