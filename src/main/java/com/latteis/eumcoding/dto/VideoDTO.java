package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Video;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class VideoDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 ID", example = "1")
        private int id;

        public IdRequestDTO(int id) {
            this.id = id;
        }
    }

        // 동영상 업로드 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 업로드 요청 DTO")
    public static class UploadRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int sectionId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "동영상 이름", example = "동영상 이름입니다")
        private String name;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "동영상 설명", example = "동영상 설명입니다")
        private String description;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "0 : 미리보기 금지, 1 : 미리보기 가능", example = "1")
        private int preview;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 수정 요청 DTO")
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 ID", example = "1")
        private int Id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "동영상 이름", example = "동영상 이름입니다")
        private String name;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "동영상 설명", example = "동영상 설명입니다")
        private String description;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "0 : 미리보기 금지, 1 : 미리보기 가능", example = "1")
        private int preview;

    }


    // 섹션 리스트 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "섹션 리스트 응답 DTO")
    public static class SectionListDTO {

        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "비디오 이름", example = "비디오입니다")
        private String name;

        @ApiModelProperty(value = "0 : 미리보기 금지, 1 : 미리보기 가능", example = "1")
        private int preview;

        @ApiModelProperty(value = "재생시간", example = "1")
        private LocalTime playTime;

        @ApiModelProperty(value = "순서", example = "1")
        private int sequence;

        @ApiModelProperty(value = "비디오 썸네일", example = "~.png")
        private String thumb;

        public SectionListDTO(Video video) {
            this.id = video.getId();
            this.name = video.getName();
            this.preview = video.getPreview();
            this.playTime = video.getPlayTime();
            this.sequence = video.getSequence();
        }
    }

    /*
    * 동영상 정보 응답
    */
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 정보 응답 DTO")
    public static class ViewResponseDTO {

        @ApiModelProperty(value = "비디오 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "비디오 이름", example = "비디오입니다")
        private String name;

        @ApiModelProperty(value = "비디오 설명", example = "비디오 설명입니다")
        private String description;

        @ApiModelProperty(value = "0 : 미리보기 금지, 1 : 미리보기 가능", example = "1")
        private int preview;

        @ApiModelProperty(value = "재생시간", example = "1")
        private LocalTime playTime;

        @ApiModelProperty(value = "업로드 날짜", example = "")
        private LocalDateTime uploadDay;

        @ApiModelProperty(value = "순서", example = "1")
        private int sequence;

        @ApiModelProperty(value = "비디오 파일", example = "~.mp4")
        private String path;

        public ViewResponseDTO(Video video) {
            this.id = video.getId();
            this.name = video.getName();
            this.description = video.getDescription();
            this.preview = video.getPreview();
            this.playTime = video.getPlayTime();
            this.uploadDay = video.getUploadDate();
            this.sequence = video.getSequence();
        }
    }

    private int videoId; // 사용자에게 고유하게 부여되는 값

    private SectionDTO sectionDTO;

    private String name; // 비디오 이름

    private LocalTime playTime; // 총 재생시간

    private String description; // 영상 설명

    private LocalDateTime uploadDate; // 영상 업로드 날짜

    private int preview; // 0:미리보기불가, 1:미리보기허용

    private String path; // 영상이 저장된 위치

    private String thumb; // 썸네일이 저장된 위치

    private List<MultipartFile> thumbRequest;

    private String thumbResponse;

    public static class VideoPreview {

        // 미리보기 불가
        public static final int IMPOSSIBLE = 0;

        // 미리보기 가능
        public static final int POSSIBLE = 1;

    }
}
