package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.StatsDTO;
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
    @PostMapping(value = "/totalCnt")
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
    @PostMapping(value = "/totalRating")
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
    @PostMapping(value = "/totalVolume")
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
    @PostMapping(value = "/totalRevenue")
    @ApiOperation(value = "이번 달 강의 총 수익")
    public ResponseEntity<StatsDTO.TotalRevenueDTO> getTotalRevenueThisMonth(@ApiIgnore Authentication authentication) {

        try {
            StatsDTO.TotalRevenueDTO totalRevenueDTO = statsService.getTotalRevenueThisMonth(authentication);
            return ResponseEntity.ok().body(totalRevenueDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    // 이번 달 강의 총 수익
    @PostMapping(value = "/totalVolumePercentage")
    @ApiOperation(value = "이번 달 총 판매량 비율")
    public ResponseEntity<StatsDTO.TotalVolumePercentageResponseDTO> getTotalVolumePercentageListThisMonth(@ApiIgnore Authentication authentication) {

        try {
            StatsDTO.TotalVolumePercentageResponseDTO totalVolumePercentageResponseDTO = statsService.getTotalVolumePercentageListThisMonth(authentication);
            return ResponseEntity.ok().body(totalVolumePercentageResponseDTO);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }


}
