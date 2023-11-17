package com.latteis.eumcoding.dto.stats;

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

    public static LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value = "총 강의 수 응답 DTO")
    public static class TotalLectureCntDTO {

        @ApiModelProperty(value = "총 강의 수", example = "1")
        private int totalLectureCnt;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value = "이번 달 총 평점 응답 DTO")
    public static class TotalRatingDTO {

        @ApiModelProperty(value = "총 평점", example = "4.0")
        private double totalRating;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value = "이번 달 총 판매량 응답 DTO")
    public static class TotalVolumeDTO {

        @ApiModelProperty(value = "총 판매량", example = "4")
        private double totalVolume;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value = "이번 달 강의 총 수익 응답 DTO")
    public static class TotalRevenueDTO {

        @ApiModelProperty(value = "총 수익", example = "4")
        private double totalRevenue;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "이번 달 판매 비율 DTO")
    public static class TotalVolumePercentageDTO {

        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @ApiModelProperty(value = "강의명", example = "강의명")
        private String lectureName;

        @ApiModelProperty(value = "판매량", example = "1")
        private int salesVolume;

        @ApiModelProperty(value = "퍼센테이지", example = "30.22")
        private double percentage;

        public TotalVolumePercentageDTO(Object[] objects) {
            this.lectureId = objects[0] != null ? Integer.parseInt(String.valueOf(objects[0])) : 0;
            this.lectureName = objects[1] != null ? String.valueOf(objects[1]) : "";
            this.salesVolume = objects[2] != null ? Integer.parseInt(String.valueOf(objects[2])) : 0;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "이번 달 판매 비율 응답 DTO")
    public static class TotalVolumePercentageResponseDTO {

        @ApiModelProperty(value = "총 판매량", example = "1")
        private int totalSalesVolume;

        List<TotalVolumePercentageDTO> totalVolumePercentageDTOList;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "기간 옵션 요청 DTO")
    public static class PeriodOptionRequestDTO {

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "일주일 : 0, 한달 : 1, 세달 : 2, 여섯달 : 3, 일년 : 4", example = "0")
        private int periodOption;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "기간별 강의별 수익 분포 응답 DTO")
    public static class RevenueDistributionResponseDTO {

        @ApiModelProperty(value = "날짜", example = "20##-##")
        private LocalDate date;

        List<RevenueDistributionDTO> revenueDistributionDTOList;

    }

    @Getter
    @Setter
    @ApiModel(value = "기간별 강의별 수익 분포 DTO")
    public static class RevenueDistributionDTO {

        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @ApiModelProperty(value = "강의명", example = "강의명")
        private String lectureName;

        @ApiModelProperty(value = "판매수익", example = "1")
        private Long revenue;

        public RevenueDistributionDTO(Object[] objects) {
            this.lectureId = objects[0] != null ? Integer.parseInt(String.valueOf(objects[0])) : 0;
            this.lectureName = objects[1] != null ? String.valueOf(objects[1]) : "";
            this.revenue = objects[2] != null ? Long.parseLong(String.valueOf(objects[2])) : 0;
        }

        public RevenueDistributionDTO(int lectureId, String lectureName, Long revenue) {
            this.lectureId = lectureId;
            this.lectureName = lectureName;
            this.revenue = revenue;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "종합 판매 추이 응답 DTO")
    public static class SalesVolumeProgressResponseDTO {

        @ApiModelProperty(value = "날짜", example = "20##-##")
        private LocalDate date;

        @ApiModelProperty(value = "판매량", example = "1")
        private Long salesVolume;

        public SalesVolumeProgressResponseDTO(Object[] objects) {
            this.date = localDateTimeToLocalDate((LocalDateTime) objects[0]);
            this.salesVolume = (Long) objects[1];
        }

        public SalesVolumeProgressResponseDTO(LocalDate date, int salesVolume) {
            this.date = date;
            this.salesVolume = (long) salesVolume;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "기간, 강의 요청 DTO")
    public static class PeriodAndLectureRequestDTO {

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "일주일 : 0, 한달 : 1, 세달 : 2, 여섯달 : 3, 일년 : 4", example = "0")
        private int periodOption;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "강의1 ID", example = "0")
        private int firstLectureId;

        @PositiveOrZero(message = "0과 양수만 가능합니다.")
        @ApiModelProperty(value = "강의2 ID", example = "0")
        private int secondLectureId;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "비교 판매 현황 DTO")
    public static class CompareLectureSalesVolumeResponseDTO {

        @ApiModelProperty(value = "강의1 이름", example = "강의명1")
        private String lectureName1;

        @ApiModelProperty(value = "강의2 이름", example = "강의명2")
        private String lectureName2;

        List<CompareLectureSalesVolumeDTO> compareLectureSalesVolumeDTOList;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "비교 판매 현황 DTO")
    public static class CompareLectureSalesVolumeDTO {

        @ApiModelProperty(value = "날짜", example = "20##-##")
        private LocalDate date;

        @ApiModelProperty(value = "강의1 판매량", example = "1")
        private int salesVolume1;

        @ApiModelProperty(value = "강의2 판매량", example = "1")
        private int salesVolume2;

        public CompareLectureSalesVolumeDTO(Object[] objects, LocalDate localDate) {
            this.salesVolume1 = Integer.parseInt(String.valueOf(objects[0]));
            this.salesVolume2 = Integer.parseInt(String.valueOf(objects[1]));
            this.date = localDate;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value = "구간별 수강률 DTO")
    public static class LectureProgressDTO {

        @ApiModelProperty(value = "해당 구간 수 (0~9%부터)", example = "1")
        private int[] progress;

    }

    // 기간 선택 날짜 옵션
    public static class PeriodOption {

        // 일주일
        public static final int WEEK = 0;

        // 한달
        public static final int A_MONTH = 1;

        // 세달
        public static final int THREE_MONTH = 2;

        // 여섯달
        public static final int SIX_MONTH = 3;

        // 1년
        public static final int YEAR = 4;

    }

}
