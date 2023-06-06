package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MyLectureListDTO;
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




    public List<MyLectureListDTO> getMyLectureList(int memberId, int page) {

        Member member = memberRepository.findByIdAndRole(memberId, 0);

        // 각 MyLectureListDTO에 대해 정렬을 수행합니다.
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(direction));

        Page<Payment> paymentsPage;
        List<Payment> payments = paymentRepository.findByMemberIdPayment(member.getId());



        // 각 Payment에 연결된 PayLecture 목록을 가져옴
        List<Lecture> lectures = new ArrayList<>();
        for (Payment payment : payments) {
            List<PayLecture> payLectures = payLectureRepository.findByPaymentId(payment.getId());
            if(payment.getState() == 1){
                for (PayLecture payLecture : payLectures) {
                    lectures.add(payLecture.getLecture());
                }
            }

        }

        List<MyLectureListDTO> lectureProgressList = new ArrayList<>();

        for (Lecture lecture : lectures) {
            int totalVideos = 0;
            int completedVideos = 0;


            LocalDateTime payDay = null;
            LocalTime lastView = null;
            boolean isLectureCompleted = true; // 강의 수강 완료 여부

            // Payment로부터 payDay를 얻어옴
            for (Payment payment : payments) {
                List<PayLecture> payLectures = payLectureRepository.findByPaymentId(payment.getId());
                if (payment.getState() == 1 && payLectures.stream().anyMatch(payLecture -> payLecture.getLecture().getId() == lecture.getId())) {
                    payDay = payment.getPayDay();
                    break;
                }
            }
            // memberId와 lectureId를 사용해 LectureProgress를 찾아 lastView를 얻어옴
            List<LectureProgress> lectureProgresses = lectureProgressRepository.findByMemberIdAndLectureId(memberId, lecture.getId());
            for (LectureProgress lectureProgress : lectureProgresses) {
                List<VideoProgress> videoProgresses = videoProgressRepository.findByLectureProgressId(lectureProgress.getId());
                for (VideoProgress videoProgress : videoProgresses) {
                    // lastView 업데이트
                    if (videoProgress.getLastView() != null) {
                        if (lastView == null || lastView.isBefore(videoProgress.getLastView())) {
                            lastView = videoProgress.getLastView();
                        }
                    }

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
            int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideos * 100 / totalVideos);

            Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
            if (averageRating == null) averageRating = 0;

            MyLectureListDTO myLectureListDTO = MyLectureListDTO.builder()
                    .lectureId(lecture.getId())
                    .teacherId(lecture.getMember().getId())
                    .score(averageRating)
                    .progress(progress)
                    .teacherName(lecture.getMember().getName())
                    .lectureName(lecture.getName())
                    .thumb(lecture.getThumb())
                    .payDay(payDay)
                    .lastView(lastView)
                    .build();

            lectureProgressList.add(myLectureListDTO);
        }
        return lectureProgressList;
    }
}