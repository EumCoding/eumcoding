package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private List<BadgeListDTO> badgeListDTOList;

    @Data
    @Builder
    @AllArgsConstructor
    @ApiModel(value = "뱃지모음 ")
    public static class BadgeListDTO {
        private int memberId;
        private int lectureId;
        private String lectureName;
        private String badgeImg;
    }


}
