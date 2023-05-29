package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.VideoTestMultipleList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTestMultipleListDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 객관식 문제 보기 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 객관식 보기 ID", example = "1")
        private int id;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 보기 추가 요청 DTO")
    public static class AddRequestDTO {

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "문제 보기", example = "문제 보기입니다")
        private String content; // 문제 제목

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 보기 추가 DTO")
    public static class AddDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 ID", example = "1")
        private int videoTestId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "문제 보기", example = "문제 보기입니다")
        private String content; // 문제 제목

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 보기 수정 DTO")
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 보기 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "문제 보기", example = "문제 보기입니다")
        private String content; // 문제 제목

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 보기 리스트 DTO")
    public static class ListResponseDTO {

        @ApiModelProperty(value = "동영상 문제 보기 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "문제 보기", example = "문제 보기입니다")
        private String content; // 문제 제목

        @ApiModelProperty(value = "동영상 문제 보기 순서", example = "1")
        private int sequence;

        public ListResponseDTO(VideoTestMultipleList videoTestMultipleList) {
            this.id = videoTestMultipleList.getId();
            this.content = videoTestMultipleList.getContent();
            this.sequence = videoTestMultipleList.getSequence();
        }
    }


        private int videoTestMultipleListId; // 사용자에게 고유하게 부여되는 값

    private int videoTestId;

    private String content; // 문제의 보기

    private int sequence; // 보기의 순서
}
