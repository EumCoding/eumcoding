package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Section;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {




    // 섹션 리스트 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "섹션 리스트 응답 DTO")
    public static class SectionListDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "비디오 이름", example = "비디오입니다")
        private String name;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "0 : 미리보기 금지, 1 : 미리보기 가능", example = "1")
        private int preview;

        @ApiModelProperty(value = "재생시간", example = "1")
        private LocalTime playTime;

        public SectionListDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.name = (String) objects[1];
            this.preview = (int) objects[2];
            this.playTime = timeToLocalTime((Time)objects[3]);
        }

        public LocalTime timeToLocalTime(Time time) {
            return time.toLocalTime();
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

    private String sequence; // 영상 순서. 0부터 시작
}
