package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 글 아이디만 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "게시물 ID", example = "1")
        private int id;

    }

    // 글 작성 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class CreateRequestDTO {

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "제목", example = "제목입니다")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = ":자유게시판, 1:건의사항, 2:공지사항", example = "0")
        private int type; //

    }

    // 글 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiParam(value = "수정할 게시물 ID", example = "1")
        private int boardId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "제목", example = "제목입니다")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 내가 쓴 글 목록 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MyListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "게시글 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "제목", example = "제목입니다")
        private String title;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "조회수", example = "0")
        private int views; // 조회수

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

    }

    // 글 목록 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ListRequestDTO {

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiParam(value = "정렬 기준 0 : 날짜, 1 : 좋아요, 2 : 조회수", example = "0")
        private int sort; // 정렬 기준 0 : 날짜, 1 : 좋아요, 2 : 조회수

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiParam(value = "0:자유게시판, 1:건의사항, 2:공지사항", example = "0")
        private int type;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiParam(value = "0부터 1페이지", example = "0")
        private int page;

        @Positive(message = "양수만 가능합니다.")
        @ApiParam(value = "페이지 번호", example = "5")
        private int pageSize;

    }

    // 글 목록 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class ListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "게시글 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "유저 ID", example = "1")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "제목", example = "제목입니다")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "닉네임", example = "닉네임입니다")
        private String nickname;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "조회수", example = "0")
        private int views;

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "좋아요", example = "0")
        private int heart;

        public ListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.memberId = (int) objects[1];
            this.title = (String) objects[2];
            this.views = (int) objects[3];
            this.nickname = (String) objects[4];
            this.createdDay = timestampToLocalDateTime((Timestamp) objects[5]);
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
        @ApiModelProperty(value = "게시글 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "유저 ID", example = "1")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "제목", example = "제목입니다")
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;


        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "닉네임", example = "닉네임입니다")
        private String nickname;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "조회수", example = "0")
        private int views;

        @ApiModelProperty(value = "작성일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "좋아요", example = "0")
        private int heart;

        public ViewResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.memberId = (int) objects[1];
            this.title = (String) objects[2];
            this.content = (String) objects[3];
            this.views = (int) objects[4];
            this.createdDay = timestampToLocalDateTime((Timestamp) objects[5]);
            this.nickname = (String) objects[6];
            this.heart = Integer.parseInt(String.valueOf(objects[7]));
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            return localDateTime;
        }

    }


    private LocalDateTime updatedDay;

    private List<MultipartFile> imageRequest;

    private String imageResponse;

}
