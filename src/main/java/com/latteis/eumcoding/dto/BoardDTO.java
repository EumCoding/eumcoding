package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
        private String content;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        private int type;

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
        private String title;

        @NotBlank(message = "필수 입력 값입니다.")
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

    private String nickname;

    private LocalDateTime updated_day;

    private LocalDateTime created_day;

    @PositiveOrZero(message = "0과 양수만 가능합니다.")
    private int views; // 조회수

    private List<MultipartFile> imageRequest;

    private String imageResponse;

    private int heart; // 좋아요 수

    private int commentCount; // 댓글 수
}
