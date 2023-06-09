package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {

    private int count;
    private List<contentsDTO> content;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class contentsDTO {
        private int lectureId;
        private String lectureName;
        private String lectureThumb;
        private int teacherId;
        private String teacherName;
        private String teacherProfileImage;
        private int price;
        private int rating;
    }
}
