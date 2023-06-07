package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MyLectureListDTO;
import com.latteis.eumcoding.dto.SearchMylectureDTO;
import com.latteis.eumcoding.dto.payment.PaymentDTO;
import com.latteis.eumcoding.dto.payment.PaymentOKRequestDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyLectureListService {
    private final LectureRepository lectureRepository;

    private final VideoRepository videoRepository;

    private final SectionRepository sectionRepository;

    private final PaymentRepository paymentRepository;

    private final PayLectureRepository payLectureRepository;

    private final VideoProgressRepository videoProgressRepository;

    private final LectureProgressRepository lectureProgressRepository;

    private final MemberRepository memberRepository;




    public List<MyLectureListDTO> getMyLectureList(int memberId, int page, int size, int sort) {
        Member member = memberRepository.findByIdAndRole(memberId, 0);

        Sort sortObj;
        switch (sort) {
            case 0:
                sortObj = Sort.by(Sort.Direction.DESC, "payDay");
                break;
            default:
                sortObj = Sort.unsorted();
        }

        int offset = (page - 1) * size;//페이지 마다 해당 사이즈 크기에 맞게 내용 출력
        int currentLectureCount = 0;

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sortObj);

        Page<Payment> paymentsPage = paymentRepository.findAllByMemberIdAndState(member.getId(), pageable);

        List<Lecture> lectures = new ArrayList<>();

        for (Payment payment : paymentsPage) {
            List<PayLecture> payLectures = payLectureRepository.findByPaymentId(payment.getId());
            if (payment.getState() == 1) {
                for (PayLecture payLecture : payLectures) {
                    if (currentLectureCount >= offset + size) break;
                    if (currentLectureCount >= offset) {
                        lectures.add(payLecture.getLecture());
                    }
                    currentLectureCount++;
                }
            }
            if (currentLectureCount >= offset + size) break;
        }


        // 강의에 대한 정보를 저장할 리스트를 생성
        List<MyLectureListDTO> lectureProgressList = new ArrayList<>();

        for (Lecture lecture : lectures) {
            LocalDateTime payDay = null;

            // Payment로부터 payDay를 얻어옴
            for (Payment payment : paymentsPage) {
                List<PayLecture> payLecturesFromPayment = payLectureRepository.findByPaymentId(payment.getId());
                if (payment.getState() == 1 && payLecturesFromPayment.stream().anyMatch(payLecture -> payLecture.getLecture().getId() == lecture.getId())) {
                    payDay = payment.getPayDay();
                    break;
                }
            }

            // 비디오와 강의 진행 상태 업데이트
            boolean isLectureCompleted = updateVideoAndLectureProgresses(memberId, lecture);

            // 전체 비디오 수와 완료된 비디오 수 계산
            int[] videoCounts = countTotalAndCompletedVideos(memberId, lecture);
            int totalVideos = videoCounts[0];
            int completedVideos = videoCounts[1];
            int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideos * 100 / totalVideos);

            Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
            if (averageRating == null) averageRating = 0;

            MyLectureListDTO myLectureListDTO = MyLectureListDTO.builder()
                    .memberId(memberId)
                    .lectureId(lecture.getId())
                    .teacherId(lecture.getMember().getId())
                    .score(averageRating)
                    .progress(progress)
                    .teacherName(lecture.getMember().getName())
                    .lectureName(lecture.getName())
                    .thumb(lecture.getThumb())
                    .payDay(payDay)
                    .build();

            lectureProgressList.add(myLectureListDTO);
        }
        return lectureProgressList;
    }



    public List<SearchMylectureDTO> getSearchMyLecture(int memberId, int page, int sort, int size, String keyword) {
        Sort sortObj;
        switch (sort) {
            case 0:
                sortObj = Sort.by(Sort.Direction.ASC, "name");
                break;
            /*case 1:
                sortObj = Sort.by(Sort.Direction.DESC, "payDay");
                break;*/
            default:
                sortObj = Sort.unsorted();
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        //강의 검색 (강의 이름에 keyword가 포함된 강의들)
        Page<Lecture> lecturePage = lectureRepository.findByName(keyword, pageable);

        //검색된 강의들을 SearchMylectureDTO로 변환
        List<SearchMylectureDTO> searchMylectureDTOList = new ArrayList<>();

        for (Lecture lecture : lecturePage) {
            // 강의와 사용자 ID를 이용해 강의 진행도 계산
            int[] videoCounts = countTotalAndCompletedVideos(memberId, lecture);
            int totalVideos = videoCounts[0];
            int completedVideos = videoCounts[1];
            int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideos * 100 / totalVideos);

            // 평점 계산
            Integer score = lectureRepository.findAverageRatingByLectureId(lecture.getId());
            if (score == null) score = 0;

            // SearchMylectureDTO 객체 생성
            SearchMylectureDTO dto = SearchMylectureDTO.builder()
                    .lectureId(lecture.getId())
                    .score(score)
                    .teacherId(lecture.getMember().getId())
                    .teacherName(lecture.getMember().getName())
                    .lectureName(lecture.getName())
                    .progress(progress)
                    .build();

            searchMylectureDTOList.add(dto);
        }

        return searchMylectureDTOList;
    }
    
    
    


    
    //공통메서드로 뺌. progress계산하는거랑 비디오 다들으면 lectureProgress가 0->1로 바뀌게하는 코드
    private boolean updateVideoAndLectureProgresses(int memberId, Lecture lecture) {
        boolean isLectureCompleted = true;

        List<LectureProgress> lectureProgresses = lectureProgressRepository.findByMemberIdAndLectureId(memberId, lecture.getId());
        for (LectureProgress lectureProgress : lectureProgresses) {
            List<VideoProgress> videoProgresses = videoProgressRepository.findByLectureProgressId(lectureProgress.getId());
            for (VideoProgress videoProgress : videoProgresses) {

                // Video의 전체 재생 시간과 VideoProgress의 lastView를 비교하여 state 업데이트
                Video video = videoProgress.getVideo();
                if (video != null) {
                    long playTimeSeconds = ChronoUnit.SECONDS.between(LocalTime.MIDNIGHT, video.getPlayTime());
                    long lastViewSeconds = ChronoUnit.SECONDS.between(LocalTime.MIDNIGHT, videoProgress.getLastView());
                    if (lastViewSeconds >= playTimeSeconds) {
                        videoProgress.setState(1); // 수강 완료
                    } else {
                        videoProgress.setState(0); // 수강 중
                        isLectureCompleted = false; // 만약 이 VideoProgress가 완료되지 않았다면, 전체 강의를 완료하지 않은 것으로 표시
                    }
                    videoProgressRepository.save(videoProgress);
                }

                // videoProgress의 상태가 1이 아니면 강의를 완료하지 않은 것으로 간주
                if (videoProgress.getState() != 1) {
                    isLectureCompleted = false;
                    break;
                }
            }

            // 만약 어떤 비디오 진행 상태가 완료되지 않았다면,
            // 이 강의 진행 상태도 완료되지 않았다고 간주하므로 break;
            if (!isLectureCompleted) {
                lectureProgress.setState(0); // 강의 진행 상태를 미완료로 변경
                lectureProgressRepository.save(lectureProgress);
                break;
            }

            // 만약 모든 videoProgress의 state가 1이면, 해당 lectureProgress의 state를 1로 변경
            if (isLectureCompleted) {
                lectureProgress.setState(1);
                lectureProgressRepository.save(lectureProgress); // DB에 변경 사항 저장
            }
        }

        return isLectureCompleted;
    }

    private int[] countTotalAndCompletedVideos(int memberId, Lecture lecture) {
        int totalVideos = 0;
        int completedVideos = 0;

        List<Section> sections = sectionRepository.findByLectureId(lecture.getId());

        for (Section section : sections) {
            List<Video> videos = videoRepository.findBySectionId(section.getId());
            totalVideos += videos.size();

            for (Video video : videos) {
                Optional<VideoProgress> videoProgress = videoProgressRepository.findByMemberIdAndVideoId(memberId, video.getId());
                if (videoProgress.isPresent()) {
                    if (videoProgress.get().getState() == 1) { // 수강 종료 상태 체크
                        completedVideos++;
                    }
                }
            }
        }

        return new int[] { totalVideos, completedVideos };
    }
}