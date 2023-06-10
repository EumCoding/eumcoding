package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.StatsDTO;
import com.latteis.eumcoding.persistence.LectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    // 강의 통계 보기
    public StatsDTO.MainResponseDTO viewStats(int memberId, StatsDTO.DateRequestDTO dateRequestDTO) {

        // 받아온 날짜 할당
        LocalDate startDate = dateRequestDTO.getStartDate();
        LocalDate endDate = dateRequestDTO.getEndDate();

        // 시작일과 종료일 중 하나만 선택했다면
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)){
            log.warn("시작일과 종료일 중 하나만 선택하셨습니다. 시작일과 종료일을 모두 선택하시거나 아무 것도 선택하지 마세요.");
            throw new RuntimeException("시작일과 종료일 중 하나만 선택하셨습니다. 시작일과 종료일을 모두 선택하시거나 아무 것도 선택하지 마세요.");
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
            statsResponseDTO.setThumb("http://localhost:8081/eumCodingImgs/lecture/thumb/" + statsResponseDTO.getThumb());

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

}
