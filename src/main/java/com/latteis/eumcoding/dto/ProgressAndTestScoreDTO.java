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
public class ProgressAndTestScoreDTO {

    private int memberId;
    private int lectureId;
    private int teacherId;
    private int rating; //평점
    private int progress;
    private String teacherName;
    private String lectureName;
    private String thumb;
    private int perfectScore;
    private List<MainTestDTO.MainTestScoreDTO> mainTestScoreDTOs;

}
