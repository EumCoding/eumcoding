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

    //private int over; // 0: 안지남, 1: 지남

    private int videoProgress; //강의 진행과정(한 강의 에서 얼마나 들었는지..)

    private int sectionProgress;//강좌 진행과정(전체 강좌에서 몇개 강좌를 완주하고있는지..)

    //plan
    private List<SectionDTO.SectionDTOList> sectionDTOList;
}
