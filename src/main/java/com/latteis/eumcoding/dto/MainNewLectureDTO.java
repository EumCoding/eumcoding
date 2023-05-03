package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainNewLectureDTO {
    private int lectureId;
    private String lectureName;
    private String lectureThumb;
    private int teacherId;
    private String teacherName;
    private String teacherProfileImage;
}
