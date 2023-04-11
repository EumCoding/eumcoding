package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    // 글 작성 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class CreateRequestDTO {

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "제목")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "내용")
        private String content;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "0:자유게시판, 1:공지사항, 2:건의사항")
        private int type; // 0:자유게시판, 1:공지사항, 2:건의사항

    }

    // 글 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        private int boardId;

        @Positive(message = "양수만 가능합니다.")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "제목")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "내용")
        private String content;

    }

    // 글 삭제 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class DeleteRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        private int boardId;

        @Positive(message = "양수만 가능합니다.")
        private int memberId;

    }

    // 글 목록 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ListRequestDTO {

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "정렬 기준 0 : 날짜, 1 : 좋아요, 2 : 조회수")
        private int sort; // 정렬 기준 0 : 날짜, 1 : 좋아요, 2 : 조회수

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "0:자유게시판, 1:공지사항, 2:건의사항")
        private int type; // 0:자유게시판, 1:공지사항, 2:건의사항

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "0부터 1페이지")
        private int page;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(example = "페이지 번호")
        private int pageSize;

    }

    // 글 목록 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class ListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "제목")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "닉네임")
        private String nickname;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "조회수")
        private int views; // 조회수

        @ApiModelProperty(example = "작성일")
        private LocalDateTime created_day;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "좋아요")
        private int heart; // 좋아요

        public ListResponseDTO(Object[] objects){
            this.id = (int) objects[0];
            this.memberId = (int) objects[1];
            this.title = (String) objects[2];
            this.views = (int) objects[3];
            this.nickname = (String) objects[4];
            this.created_day = timestampToLocalDateTime((Timestamp) objects[5]);
            this.heart = Integer.parseInt(String.valueOf(objects[6]));
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            return localDateTime;
        }

    }

    // 글 보기 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ViewResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "제목")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "내용")
        private String content;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(example = "닉네임")
        private String nickname;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "조회수")
        private int views; // 조회수

        @ApiModelProperty(example = "작성일")
        private LocalDateTime created_day;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(example = "좋아요")
        private int heart; // 좋아요

        public ViewResponseDTO(Object[] objects){
            this.id = (int) objects[0];
            this.memberId = (int) objects[1];
            this.title = (String) objects[2];
            this.content = (String) objects[3];
            this.views = (int) objects[4];
            this.created_day = timestampToLocalDateTime((Timestamp) objects[5]);
            this.nickname = (String) objects[6];
            this.heart = Integer.parseInt(String.valueOf(objects[7]));
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            return localDateTime;
        }

    }


    private LocalDateTime updated_day;



    private List<MultipartFile> imageRequest;

    private String imageResponse;

    private int heart; // 좋아요 수

    private int commentCount; // 댓글 수
}
