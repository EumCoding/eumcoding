package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.stats.StatsDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    private final LectureProgressRepository lectureProgressRepository;

    private final VideoProgressRepository videoProgressRepository;

    private final VideoRepository videoRepository;

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

    // 이번 달 총 판매량 비율
    public long getTotalStudent(Authentication authentication) {

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

        long studentCnt = payLectureRepository.getTotalStudent(member);
        return studentCnt;

    }

    // 기간별 강의별 수익 분포
    public List<StatsDTO.RevenueDistributionResponseDTO> getRevenueDistribution(Authentication authentication, StatsDTO.PeriodOptionRequestDTO periodOptionRequestDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        int periodOption = periodOptionRequestDTO.getPeriodOption();
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }
        // dateOption에 유효한 값이 넘어왔는지 검사
        if (periodOption > StatsDTO.PeriodOption.YEAR) {
            throw new ResponseMessageException(ErrorCode.PERIOD_OPTION_NOT_FOUND);
        }

        // 받아온 기간옵션에 맞는 기간 저장
        int dateOption = getDateOption(periodOption);

        // 현재 날짜
        LocalDate currentDate = LocalDate.now();
        // 변수 초기화
        int year = 0;
        int month = 0;
        int day = 0;
        List<Object[]> objects = new ArrayList<>();
        // 반환 DTO List 생성
        List<StatsDTO.RevenueDistributionResponseDTO> responseDTOList = new ArrayList<>();

        List<Lecture> lectureList = lectureRepository.findByMemberId(member.getId());
        // dateOption이 0일때까지 반복
        while (dateOption > 0) {
            // 반환 DTO 생성
            StatsDTO.RevenueDistributionResponseDTO responseDTO = new StatsDTO.RevenueDistributionResponseDTO();
            // 기간 옵션이 일주일이라면
            if (periodOption == StatsDTO.PeriodOption.WEEK) {
                // 년월일 저장
                year = currentDate.getYear();
                month = currentDate.getMonthValue();
                day = currentDate.getDayOfMonth();
                // 해당 일에 맞는 강의별 데이터 가져오기
                objects = payLectureRepository.getRevenueDistributionByDay(member, year, month, day);
                // 반환 DTO에 현재 날짜 담기
                responseDTO.setDate(currentDate);
                // 현재 날짜 - 1일
                currentDate = currentDate.minusDays(1);
                // dateOption - 1
                dateOption--;
            }
            // 일주일보다 크다면
            else {
                // 년월 저장
                year = currentDate.getYear();
                month = currentDate.getMonthValue();
                // 해당 월에 맞는 강의별 데이터 가져오기
                objects = payLectureRepository.getRevenueDistributionByMonth(member, year, month);
                // 반환 DTO에 현재 날짜 담기
                responseDTO.setDate(currentDate);
                // 현재 날짜 - 1달
                currentDate = currentDate.minus(1, ChronoUnit.MONTHS);
                // dateOption - 1
                dateOption--;
            }

            // 받아온 오브젝트 DTO에 담기
            // DTO 담을 DTO List
            List<StatsDTO.RevenueDistributionDTO> revenueDistributionDTOList = new ArrayList<>();
            for (Object[] object : objects) {
                StatsDTO.RevenueDistributionDTO dto = new StatsDTO.RevenueDistributionDTO(object);
                revenueDistributionDTOList.add(dto);
            }

            // 값이 0이여서 출력되지 않은 강의 추가
            boolean isMatch = false;
            for (Lecture lecture : lectureList) {
                isMatch = false;
                for (StatsDTO.RevenueDistributionDTO statsDTO : revenueDistributionDTOList) {
                    if (lecture.getId() == statsDTO.getLectureId()) {
                        isMatch = true;
                    }
                }
                if (isMatch == false) {
                    revenueDistributionDTOList.add(new StatsDTO.RevenueDistributionDTO(lecture.getId(), lecture.getName(), 0L));
                }
            }

            // 강의 id로 정렬
            Comparator<StatsDTO.RevenueDistributionDTO> comparator = Comparator.comparing(StatsDTO.RevenueDistributionDTO::getLectureId, Comparator.naturalOrder());
            List<StatsDTO.RevenueDistributionDTO> newRevenueDistributionDTOList = revenueDistributionDTOList.stream().sorted(comparator).collect(Collectors.toList());

            // 반환 DTO에 강의별 데이터 List 담기
            responseDTO.setRevenueDistributionDTOList(newRevenueDistributionDTOList);
            // 반환 DTO List에 반환 DTO 담기
            responseDTOList.add(responseDTO);

        }
        // 최종 응답 DTO List
        return responseDTOList;

    }

    public int getDateOption(int periodOption) {
        switch (periodOption) {
            case StatsDTO.PeriodOption.WEEK :
                return 7;
            case StatsDTO.PeriodOption.A_MONTH:
                return 1;
            case StatsDTO.PeriodOption.THREE_MONTH:
                return 3;
            case StatsDTO.PeriodOption.SIX_MONTH:
                return 6;
            case StatsDTO.PeriodOption.YEAR:
                return 12;
            default:
                throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }
    }

    /*
    * 종합 판매 추이 가져오기
    */
    public List<StatsDTO.SalesVolumeProgressResponseDTO> getSalesVolumeProgress(Authentication authentication, StatsDTO.PeriodOptionRequestDTO periodOptionRequestDTO) {

        try {

            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            Member member = memberRepository.findByMemberId(memberId);
            int periodOption = periodOptionRequestDTO.getPeriodOption();
            // 등록된 회원인지 검사
            if (member == null) {
                throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
            }
            // 강사 회원인지 검사
            if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
                throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
            }
            // dateOption에 유효한 값이 넘어왔는지 검사
            if (periodOption > StatsDTO.PeriodOption.YEAR) {
                throw new ResponseMessageException(ErrorCode.PERIOD_OPTION_NOT_FOUND);
            }

            // 받아온 기간 옵션에 맞는 기간 저장
            int dateOption = getDateOption(periodOption);
            // 현재 날짜
            LocalDate currentDate = LocalDate.now();
            // 시작 날짜
            LocalDate startDate = null;
            // 반환 DTO List
            List<StatsDTO.SalesVolumeProgressResponseDTO> responseDTOList = new ArrayList<>();

            if (periodOption <= StatsDTO.PeriodOption.A_MONTH) {
                if (periodOption == StatsDTO.PeriodOption.A_MONTH) {
                    startDate = currentDate.minus(dateOption, ChronoUnit.MONTHS);
                } else {
                    startDate = currentDate.minusDays(dateOption - 1);
                }
                LocalDateTime localDateTime = startDate.atStartOfDay();
                // 해당 일에 맞는 강의별 데이터 가져오기
                List<Object[]> objects = payLectureRepository.getSalesVolumeProgressByDay(member, localDateTime);
                // DTO에 담기
                for (Object[] object : objects) {
                    StatsDTO.SalesVolumeProgressResponseDTO salesVolumeProgressDTO = new StatsDTO.SalesVolumeProgressResponseDTO(object);
                    responseDTOList.add(salesVolumeProgressDTO);
                }
                //
                boolean isMatch = false;
                while (!startDate.isAfter(currentDate)) {
                    isMatch = false;
                    for (StatsDTO.SalesVolumeProgressResponseDTO dto : responseDTOList) {
                        if (startDate.isEqual(dto.getDate())) {
                            isMatch = true;
                            continue;
                        }
                    }
                    if (isMatch == false) {
                        responseDTOList.add(new StatsDTO.SalesVolumeProgressResponseDTO(startDate, 0));
                    }
                    startDate = startDate.plusDays(1);
                }
            } else {
                while (dateOption > 0) {
                    // 현재 날짜에서 년 월 가져오기
                    int year = currentDate.getYear();
                    int month = currentDate.getMonthValue();
                    // 년 월에 맞는 데이터 가져오기
                    int salesVolume = payLectureRepository.getSalesVolumeProgressByMonth(member, year, month);

                    StatsDTO.SalesVolumeProgressResponseDTO salesVolumeProgressDTO = new StatsDTO.SalesVolumeProgressResponseDTO(currentDate, salesVolume);
                    responseDTOList.add(salesVolumeProgressDTO);

                    // 현재 날짜 - 1달
                    currentDate = currentDate.minus(1, ChronoUnit.MONTHS);
                    // dateOption - 1
                    dateOption--;
                }
            }

            // 날짜로 정렬
            Comparator<StatsDTO.SalesVolumeProgressResponseDTO> comparator = Comparator.comparing(StatsDTO.SalesVolumeProgressResponseDTO::getDate, Comparator.naturalOrder());
            List<StatsDTO.SalesVolumeProgressResponseDTO> newresponseDTOList = responseDTOList.stream().sorted(comparator).collect(Collectors.toList());

            return newresponseDTOList;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /*
    * 강의 비교 판매 현황
    */
    public StatsDTO.CompareLectureSalesVolumeResponseDTO getCompareLectureSalesVolume(Authentication authentication, StatsDTO.PeriodAndLectureRequestDTO periodAndLectureRequestDTO) {

        try {

            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            Member member = memberRepository.findByMemberId(memberId);
            int periodOption = periodAndLectureRequestDTO.getPeriodOption();
            Lecture lecture1 = lectureRepository.findById(periodAndLectureRequestDTO.getFirstLectureId());
            Lecture lecture2 = lectureRepository.findById(periodAndLectureRequestDTO.getSecondLectureId());
            List<Lecture> lectureList = new ArrayList<>();
            lectureList.add(lecture1);
            lectureList.add(lecture2);
            // 등록된 회원인지 검사
            if (member == null) {
                throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
            }
            // 강사 회원인지 검사
            if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
                throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
            }
            // dateOption에 유효한 값이 넘어왔는지 검사
            if (periodOption > StatsDTO.PeriodOption.YEAR) {
                throw new ResponseMessageException(ErrorCode.PERIOD_OPTION_NOT_FOUND);
            }
            // lectureId에 유효한 값이 넘어왔는지 검사
            if (lecture1 == null || lecture2 == null) {
                throw new ResponseMessageException(ErrorCode.LECTURE_NOT_FOUND);
            }
            int year = 0;
            int month = 0;
            int day = 0;
            // 받아온 기간 옵션에 맞는 기간 저장
            int dateOption = getDateOption(periodOption);
            // 현재 날짜
            LocalDate currentDate = LocalDate.now();
            LocalDate tempDate = currentDate;
            // 시작 날짜
            LocalDate startDate = currentDate;
            if (periodOption == StatsDTO.PeriodOption.A_MONTH) {
                dateOption = 30;
            }

            // 반환 DTO 생성
            StatsDTO.CompareLectureSalesVolumeResponseDTO responseDTO = new StatsDTO.CompareLectureSalesVolumeResponseDTO();
            List<StatsDTO.CompareLectureSalesVolumeDTO> compareLectureSalesVolumeDTOList = new ArrayList<>();
            // dateOption이 0일때까지 반복
            while (dateOption > 0) {
                List<Object[]> objects;
                // 기간 옵션이 일주일이라면
                if (periodOption <= StatsDTO.PeriodOption.A_MONTH) {
/*
                    if (periodOption == StatsDTO.PeriodOption.A_MONTH) {
                        startDate = currentDate.minus(dateOption, ChronoUnit.MONTHS);
                    } else {
                        startDate = currentDate.minusDays(dateOption - 1);
                    }*/
                    // 년월일 저장
                    year = currentDate.getYear();
                    month = currentDate.getMonthValue();
                    day = currentDate.getDayOfMonth();
                    // 해당 일에 맞는 강의별 데이터 가져오기
                    objects = payLectureRepository.getLectureSalesVolumeByDay(lecture1, lecture2, year, month, day);
                    // 현재 날짜 - 1일
                    tempDate = currentDate.minusDays(1);


                    // dateOption - 1
                    dateOption--;
                }
                // 한달보다 크다면
                else {
                    // 년월 저장
                    year = currentDate.getYear();
                    month = currentDate.getMonthValue();
                    // 해당 월에 맞는 강의별 데이터 가져오기
                    objects = payLectureRepository.getLectureSalesVolumeByMonth(lecture1, lecture2, year, month);
                    // 현재 날짜 - 1달
                    tempDate = currentDate.minus(1, ChronoUnit.MONTHS);
                    // dateOption - 1
                    dateOption--;
                }

                // 받아온 오브젝트 DTO에 담기
                // DTO 담을 DTO List
                for (Object[] object : objects) {
                    StatsDTO.CompareLectureSalesVolumeDTO dto = new StatsDTO.CompareLectureSalesVolumeDTO(object, currentDate);
                    compareLectureSalesVolumeDTOList.add(dto);
                }

                // 값이 0이여서 출력되지 않은 강의 추가
              /*  boolean isMatch = false;
                for (Lecture lecture : lectureList) {
                    isMatch = false;
                    for (StatsDTO.RevenueDistributionDTO statsDTO : revenueDistributionDTOList) {
                        if (lecture.getId() == statsDTO.getLectureId()) {
                            isMatch = true;
                        }
                    }
                    if (isMatch == false) {
                        revenueDistributionDTOList.add(new StatsDTO.RevenueDistributionDTO(lecture.getId(), lecture.getName(), 0L));
                    }
                }*/

                // 강의 id로 정렬
                /*Comparator<StatsDTO.RevenueDistributionDTO> comparator = Comparator.comparing(StatsDTO.RevenueDistributionDTO::getLectureId, Comparator.naturalOrder());
                List<StatsDTO.RevenueDistributionDTO> newRevenueDistributionDTOList = revenueDistributionDTOList.stream().sorted(comparator).collect(Collectors.toList());
*/
                currentDate = tempDate;
            }
                // 반환 DTO에 강의별 데이터 List 담기
                responseDTO.setCompareLectureSalesVolumeDTOList(compareLectureSalesVolumeDTOList);
                responseDTO.setLectureName1(lecture1.getName());
                responseDTO.setLectureName2(lecture2.getName());
            // 최종 응답 DTO List
            return responseDTO;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /*
    * 구간별 수강률 추이
    */
    public StatsDTO.LectureProgressDTO getLectureProgress(Authentication authentication, LectureDTO.IdRequestDTO idRequestDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        Lecture lecture = lectureRepository.findById(idRequestDTO.getId());
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 강사 회원인지 검사
        if (member.getRole() != MemberDTO.MemberRole.TEACHER) {
            throw new ResponseMessageException(ErrorCode.TEACHER_INVALID_PERMISSION);
        }
        // lectureId에 유효한 값이 넘어왔는지 검사
        if (lecture == null) {
            throw new ResponseMessageException(ErrorCode.LECTURE_NOT_FOUND);
        }

        // 해당 강의를 수강하는 학생들의 진행도 리스트 가져오기
        List<Object[]> objects = videoProgressRepository.getLectureProgress(lecture.getId());

        // 구간별 진행도 수를 저장할 배열 생성
        int[] progress = new int[10];

        // 가져온 오브젝트 리스트 1개씩 처리
        for (Object[] object : objects) {
            // 난
            int i = Integer.parseInt(String.valueOf(object[1])) / 10;
            if (i >= 9) {
                progress[9]++;
            }
            else {
                progress[i]++;
            }
        }
        StatsDTO.LectureProgressDTO lectureProgressDTO = new StatsDTO.LectureProgressDTO(progress);

        return lectureProgressDTO;
    }
}
