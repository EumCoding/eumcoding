package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchTeacherDTO {

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
