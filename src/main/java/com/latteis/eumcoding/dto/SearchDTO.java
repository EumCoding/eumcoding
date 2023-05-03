package com.latteis.eumcoding.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private int lectureId;
    private String lectureName;
    private String lectureThumb;
    private int teacherId;
    private String teacherName;
    private String teacherProfileImage;
    private int price;
    private int rating;

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class SearchGradeDTO{
        private int grade;
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
