package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMylectureDTO {

    private int lectureId;
    private int teacherId;
    private int score;
    private int progress;
    private String teacherName;
    private String lectureName;

}
