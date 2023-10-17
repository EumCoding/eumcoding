package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.StatsDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.PayLectureRepository;
import com.latteis.eumcoding.persistence.ReviewRepository;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final LectureRepository lectureRepository;

    private final MemberRepository memberRepository;

    private final ReviewRepository reviewRepository;

    private final PayLectureRepository payLectureRepository;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;


    // 강의 통계 보기
    public StatsDTO.MainResponseDTO viewStats(int memberId, StatsDTO.DateRequestDTO dateRequestDTO) {

        // 받아온 날짜 할당
        LocalDate startDate = dateRequestDTO.getStartDate();
        LocalDate endDate = dateRequestDTO.getEndDate();

        Member member = memberRepository.findByMemberId(memberId);


        // 시작일과 종료일 중 하나만 선택했다면
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)){
            log.warn("시작일과 종료일 중 하나만 선택하셨습니다. 시작일과 종료일을 모두 선택하시거나 아무 것도 선택하지 마세요.");
            throw new RuntimeException("시작일과 종료일 중 하나만 선택하셨습니다. 시작일과 종료일을 모두 선택하시거나 아무 것도 선택하지 마세요.");
//            throw new ResponseMessageException(ErrorCode.TWO_DATE_PRECONDITION_FAILED);
        }

        // 전체 판매 수량
        int totalSalesVolume = 0;
        // 전체 판매액
        int totalSalesRevenue = 0;

        // 해당 강사의 모든 강의 정보 가져옴
        List<Object[]> objects = lectureRepository.getStatsLectureList(memberId);
        List<StatsDTO.StatsResponseDTO> statsResponseDTOList = new ArrayList<>();

        for (Object[] object : objects) {
            // 강의 정보를 dto에 저장
            StatsDTO.StatsResponseDTO statsResponseDTO = new StatsDTO.StatsResponseDTO(object);
            statsResponseDTO.setThumb(domain + port + "/eumCodingImgs/lecture/thumb/" + statsResponseDTO.getThumb());

            // 기간을 선택하지 않았다면
            if (startDate == null && endDate == null){
                // 시작 날짜에 강의 생성 날짜 저장
                startDate = statsResponseDTO.getCreatedDay().toLocalDate();
                // 종료 날짜에 오늘 날짜 저장
                endDate = LocalDate.now();
            }

            // 해당 강의의 통계 가져옴
            Object[] statsObject = lectureRepository.getStatsByDate(memberId, statsResponseDTO.getId(), startDate, endDate);
            // dto에 저장
            StatsDTO.TempStatsDTO tempStatsDTO = new StatsDTO.TempStatsDTO(statsObject);
            // dto에 가져온 통계 저장
            statsResponseDTO.setSalesVolume(tempStatsDTO.getSalesVolume());
            statsResponseDTO.setSalesRevenue(tempStatsDTO.getSalesRevenue());
            statsResponseDTO.setReviewRating(tempStatsDTO.getReviewRating());
            // dto list에 dto 저장
            statsResponseDTOList.add(statsResponseDTO);
            // total 변수에 더하기
            totalSalesVolume += tempStatsDTO.getSalesVolume();
            totalSalesRevenue += tempStatsDTO.getSalesRevenue();
        }

        // 판매 수량으로 정렬
        Comparator<StatsDTO.StatsResponseDTO> comparator = Comparator.comparing(StatsDTO.StatsResponseDTO::getSalesVolume, Comparator.reverseOrder());
        List<StatsDTO.StatsResponseDTO> newStatsResponseDTOList = statsResponseDTOList.stream().sorted(comparator).collect(Collectors.toList());
        // 반환할 dto에 저장
        StatsDTO.MainResponseDTO mainResponseDTO = new StatsDTO.MainResponseDTO();
        mainResponseDTO.setStatsResponseDTOList(newStatsResponseDTOList);
        mainResponseDTO.setTotalSalesVolume(totalSalesVolume);
        mainResponseDTO.setTotalSalesRevenue(totalSalesRevenue);

        return mainResponseDTO;

    }

    // 총 강의 수
    public StatsDTO.TotalLectureCntDTO getTotalLectureCnt(Authentication authentication) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }

        int totalCnt = lectureRepository.countByMemberId(member.getId());
        StatsDTO.TotalLectureCntDTO totalLectureCntDTO = new StatsDTO.TotalLectureCntDTO(totalCnt);

        return totalLectureCntDTO;

    }

    // 이번 달 총 평점
    public StatsDTO.TotalRatingDTO getTotalRatingThisMonth(Authentication authentication) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }

        String ratingAvg = reviewRepository.ratingAvgThisMonth(member);
        if (ratingAvg == null) {
            ratingAvg = "0.0";
        }
        StatsDTO.TotalRatingDTO totalRatingDTO = new StatsDTO.TotalRatingDTO(Double.parseDouble(ratingAvg));

        return totalRatingDTO;

    }

    // 이번 달 총 판매량
    public StatsDTO.TotalVolumeDTO getTotalVolumeThisMonth(Authentication authentication) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }

        int volumeCnt = payLectureRepository.cntTotalVolumeThisMonth(member);
        StatsDTO.TotalVolumeDTO totalVolumeDTO = new StatsDTO.TotalVolumeDTO(volumeCnt);

        return totalVolumeDTO;

    }

    // 이번 달 총 강의 수익
    public StatsDTO.TotalRevenueDTO getTotalRevenueThisMonth(Authentication authentication) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }

        String totalRevenue = payLectureRepository.sumTotalRevenueThisMonth(member);
        if (totalRevenue == null) {
            totalRevenue = "0";
        }
        StatsDTO.TotalRevenueDTO totalVolumeDTO = new StatsDTO.TotalRevenueDTO(Integer.parseInt(totalRevenue));

        return totalVolumeDTO;

    }


    // 이번 달 총 판매량 비율
    public StatsDTO.TotalVolumePercentageResponseDTO getTotalVolumePercentageListThisMonth(Authentication authentication) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }

        List<Object[]> objects = payLectureRepository.cntVolumeOrderByCnt(member);
        List<StatsDTO.TotalVolumePercentageDTO> totalVolumePercentageDTOList = new ArrayList<>();

        // 판매량 총합
        int allLectureTotalVolume = 0;
        // 정보를 dto에 저장
        for (Object[] object : objects) {
            StatsDTO.TotalVolumePercentageDTO totalVolumePercentageDTO = new StatsDTO.TotalVolumePercentageDTO(object);
            // 판매량 총합 변수에 판매량 추가
            allLectureTotalVolume += totalVolumePercentageDTO.getSalesVolume();
            // dto 리스트에 저장
            totalVolumePercentageDTOList.add(totalVolumePercentageDTO);
        }
        // 퍼센테이지 구하기
        for (StatsDTO.TotalVolumePercentageDTO totalVolumePercentageDTO : totalVolumePercentageDTOList) {
            double percentage = ((double) totalVolumePercentageDTO.getSalesVolume() / allLectureTotalVolume) * 100;
            // 저장
            totalVolumePercentageDTO.setPercentage(Math.round(percentage*100)/100.0);
        }

        // 응답 DTO에 총판매량과 DTO List 저장
        StatsDTO.TotalVolumePercentageResponseDTO responseDTO = new StatsDTO.TotalVolumePercentageResponseDTO();
        responseDTO.setTotalSalesVolume(allLectureTotalVolume);
        responseDTO.setTotalVolumePercentageDTOList(totalVolumePercentageDTOList);

        return responseDTO;

    }

    // 이번 달 총 강의 수익
    public StatsDTO.RevenueDistributionDTO getRevenueDistribution(Authentication authentication, StatsDTO.PeriodOptionRequestDTO periodOptionRequestDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }
        // dateOption에 유효한 값이 넘어왔는지 검사
        if (periodOptionRequestDTO.getPeriodOption() > StatsDTO.PeriodOption.YEAR) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }


    }
}
