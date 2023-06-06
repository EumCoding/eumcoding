package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDTO {
    private int basketId;
    private int memberId;
    private int lectureId;
    private String lectureName;
    private int price;
    private String teacherName;
    private String thumb;
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "장바구니추가")
    public static class BasketAddDTO {
        private int lectureId;
    }


}
