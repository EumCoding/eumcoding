package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class StatsDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "기간 요청 DTO")
    public static class DateRequestDTO {

        @ApiModelProperty(value = "시작일", example = "2023-04-13")
        private LocalDate startDate;

        @ApiModelProperty(value = "종료일", example = "2023-04-13")
        private LocalDate endDate;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "메인 응답 DTO")
    public static class MainResponseDTO {

        @ApiModelProperty(value = "전체 판매량", example = "1")
        private int totalSalesVolume;

        @ApiModelProperty(value = "전체 판매금액", example = "1")
        private int totalSalesRevenue;

        List<StatsDTO.StatsResponseDTO> statsResponseDTOList;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "통계 응답 DTO")
    public static class StatsResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "강의명", example = "강의명입니다")
        private String name;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "가격", example = "10000")
        private int price;

        @ApiModelProperty(value = "등록일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

        @ApiModelProperty(value = "강의 썸네일", example = ".jpg")
        private String thumb;

        @ApiModelProperty(value = "판매량", example = "1")
        private int salesVolume;

        @ApiModelProperty(value = "판매금액", example = "1")
        private int salesRevenue;

        @ApiModelProperty(value = "리뷰 평점", example = "1")
        private double reviewRating;

        public StatsResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.name = (String) objects[1];
            this.price = (int) objects[2];
            this.createdDay = (LocalDateTime) objects[3];
            this.thumb = objects[4] != null ? (String) objects[4] : null;
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "통계 응답 DTO")
    public static class TempStatsDTO {

        @ApiModelProperty(value = "판매금액", example = "1")
        private int salesRevenue;

        @ApiModelProperty(value = "판매량", example = "1")
        private int salesVolume;

        @ApiModelProperty(value = "리뷰 평점", example = "1")
        private double reviewRating;

        public TempStatsDTO(Object[] objects) {
            if (ObjectUtils.isEmpty(objects)){
                this.salesRevenue =  0;
                this.salesVolume =  0;
                this.reviewRating =  0;

            }else{
                objects = (Object[]) objects[0];
                this.salesRevenue = objects[0] != null ? Integer.parseInt(String.valueOf(objects[0])) : 0;
                this.salesVolume = objects[1] != null ? Integer.parseInt(String.valueOf(objects[1])) : 0;
                this.reviewRating = objects[2] != null ? Double.parseDouble(String.valueOf(objects[2])) : 0;
            }
        }
    }

}
