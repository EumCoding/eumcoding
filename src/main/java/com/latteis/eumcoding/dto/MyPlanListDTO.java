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
public class MyPlanListDTO {
    private int curriculumId;

    private LocalDate date;

    private int over; // 0: 안지남, 1: 지남

    private List<SectionDTO> sectionDTOList;
}
