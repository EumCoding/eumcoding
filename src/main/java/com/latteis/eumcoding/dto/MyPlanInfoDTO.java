package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPlanInfoDTO {
    private int curriculumId;
    private LocalDate date;
    //plan
    private List<SectionDTO.SectionDTOMessageList> sectionDTOList;
}
