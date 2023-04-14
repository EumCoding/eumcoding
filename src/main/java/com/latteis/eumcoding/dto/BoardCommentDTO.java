package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.BoardComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCommentDTO {

    // 댓글 아아디만 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "댓글 ID", example = "1")
        private int id;

    }

    // 댓글 작성 요청 DTO
    @Getter
    @NoArgsConstructor
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
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "수정할 댓글 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "내용", example = "내용입니다")
        private String content;

    }

    // 댓글 목록 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class ListResponseDTO {

        BoardCommentDTO boardCommentDTO;

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

        @PositiveOrZero(message = "양수와 0만 가능합니다.")
        @ApiModelProperty(value = "답글이 존재하면 0, 아니면 1", example = "0")
        private int existReply;

        public ListResponseDTO(Object object){
            this.id = (int) object;
            this.memberId = (int) object;
            this.nickname = (String) object;
            this.content = (String) object;
            this.commentDay = boardCommentDTO.timestampToLocalDateTime((Timestamp) object);
            this.existReply = Integer.parseInt(String.valueOf(object));
        }
        public ListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.memberId = (int) objects[1];
            this.nickname = (String) objects[2];
            this.content = (String) objects[3];
            this.commentDay = boardCommentDTO.timestampToLocalDateTime((Timestamp) objects[4]);
            this.existReply = (int) objects[5];
        }

    }

    private int id; // 사용자에게 고유하게 부여되는 값
    private int memberId;

    private String nickname;

    private LocalDateTime commentDay;

    private int step;

    private int groupNum;

    private int modified; // 수정여부

    // Timestamp -> LocalDateTime 변환
    public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime;
    }

}
