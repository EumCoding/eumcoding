package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.stats.StatsDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.service.StatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
@Api(tags = "Stats Controller", description = "강사 통계 컨트롤러")
public class StatsController {

    private final StatsService statsService;

    // (구버전) 강사 통계 컨트롤러
    @PostMapping(value = "/view")
    @ApiOperation(value = "통계 보기")
    public ResponseEntity<StatsDTO.MainResponseDTO> viewStats(@ApiIgnore Authentication authentication, @RequestBody StatsDTO.DateRequestDTO dateRequestDTO) {

        StatsDTO.MainResponseDTO mainResponseDTO = statsService.viewStats(Integer.parseInt(authentication.getPrincipal().toString()), dateRequestDTO);
        return ResponseEntity.ok().body(mainResponseDTO);

    }

    // 총 강의 수
    @PostMapping(value = "/total-cnt")
    @ApiOperation(value = "총 강좌 수")
    public ResponseEntity<StatsDTO.TotalLectureCntDTO> getTotalLectureCnt(@ApiIgnore Authentication authentication) {

        try {
            StatsDTO.TotalLectureCntDTO totalLectureCntDTO = statsService.getTotalLectureCnt(authentication);
            return ResponseEntity.ok().body(totalLectureCntDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    // 이번 달 총 평점
    @PostMapping(value = "/total-rating")
    @ApiOperation(value = "이번 달 총 평점")
    public ResponseEntity<StatsDTO.TotalRatingDTO> getTotalRatingThisMonth(@ApiIgnore Authentication authentication) {

        try {
            StatsDTO.TotalRatingDTO totalRatingDTO = statsService.getTotalRatingThisMonth(authentication);
            return ResponseEntity.ok().body(totalRatingDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    // 이번 달 총 판매량
    @PostMapping(value = "/total-volume")
    @ApiOperation(value = "이번 달 총 판매량")
    public ResponseEntity<StatsDTO.TotalVolumeDTO> getTotalVolumeThisMonth(@ApiIgnore Authentication authentication) {

        try {
            StatsDTO.TotalVolumeDTO totalVolumeDTO = statsService.getTotalVolumeThisMonth(authentication);
            return ResponseEntity.ok().body(totalVolumeDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    // 이번 달 강의 총 수익
    @PostMapping(value = "/total-revenue")
    @ApiOperation(value = "이번 달 강의 총 수익")
    public ResponseEntity<StatsDTO.TotalRevenueDTO> getTotalRevenueThisMonth(@ApiIgnore Authentication authentication) {

        try {
            StatsDTO.TotalRevenueDTO totalRevenueDTO = statsService.getTotalRevenueThisMonth(authentication);
            return ResponseEntity.ok().body(totalRevenueDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    // 총 수강생 수
    @PostMapping(value = "/total-student")
    @ApiOperation(value = "총 수강생 수")
    public ResponseEntity<?> getTotalStudent(@ApiIgnore Authentication authentication) {

        try {
            long studentCnt = statsService.getTotalStudent(authentication);
            return ResponseEntity.ok().body(studentCnt);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    // 이번 달 총 판매량 비율
    @PostMapping(value = "/total-volume-percentage")
    @ApiOperation(value = "이번 달 총 판매량 비율")
    public ResponseEntity<StatsDTO.TotalVolumePercentageResponseDTO> getTotalVolumePercentageListThisMonth(@ApiIgnore Authentication authentication) {

        try {
            StatsDTO.TotalVolumePercentageResponseDTO totalVolumePercentageResponseDTO = statsService.getTotalVolumePercentageListThisMonth(authentication);
            return ResponseEntity.ok().body(totalVolumePercentageResponseDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    /*
    * 기간별 강의별 수익 분포
    */
    @PostMapping(value = "/revenue-distribution")
    @ApiOperation(value = "기간별 강의별 수익 분포")
    public ResponseEntity<List<StatsDTO.RevenueDistributionResponseDTO>>getRevenueDistribution(@ApiIgnore Authentication authentication, @RequestBody StatsDTO.PeriodOptionRequestDTO periodOptionRequestDTO) {

        try {
            List<StatsDTO.RevenueDistributionResponseDTO> responseDTOList = statsService.getRevenueDistribution(authentication, periodOptionRequestDTO);
            return ResponseEntity.ok().body(responseDTOList);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    /*
    * 기간별 종합 판매 추이
    */
    @PostMapping(value = "/sales-volume-progress")
    @ApiOperation(value = "기간별 종합 판매 추이")
    public ResponseEntity<List<StatsDTO.SalesVolumeProgressResponseDTO>>getSalesVolumeProgress(@ApiIgnore Authentication authentication, @RequestBody StatsDTO.PeriodOptionRequestDTO periodOptionRequestDTO) {

        try {
            List<StatsDTO.SalesVolumeProgressResponseDTO> responseDTOList = statsService.getSalesVolumeProgress(authentication, periodOptionRequestDTO);
            return ResponseEntity.ok().body(responseDTOList);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    /*
    * 비교 판매 현황
    */
    @PostMapping(value = "/compare-lecture")
    @ApiOperation(value = "비교 판매 현황")
    public ResponseEntity<StatsDTO.CompareLectureSalesVolumeResponseDTO>getCompareLectureSalesVolume(@ApiIgnore Authentication authentication, @RequestBody StatsDTO.PeriodAndLectureRequestDTO periodAndLectureRequestDTO) {

        try {
            StatsDTO.CompareLectureSalesVolumeResponseDTO responseDTO = statsService.getCompareLectureSalesVolume(authentication, periodAndLectureRequestDTO);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    /*
    * 수강율 구간별 추이
    */
    @PostMapping(value = "/lecture-progress")
    @ApiOperation(value = "수강율 구간별 추이")
    public ResponseEntity<StatsDTO.LectureProgressDTO> getLectureProgress(@ApiIgnore Authentication authentication, @RequestBody LectureDTO.IdRequestDTO idRequestDTO) {

        try {
            StatsDTO.LectureProgressDTO lectureProgressDTO = statsService.getLectureProgress(authentication, idRequestDTO);
            return ResponseEntity.ok().body(lectureProgressDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }


}
