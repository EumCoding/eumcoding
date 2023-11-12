package com.latteis.eumcoding.service;



import com.latteis.eumcoding.dto.MyLectureListDTO;
import com.latteis.eumcoding.dto.SearchMylectureDTO;

import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;

import java.util.*;



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


    @Value("${file.path.lecture.image}")
    private String lecturePath;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;

    public File getLectureDirectoryPath() {
        File file = new File(lecturePath);
        file.mkdirs();

        return file;
    }


    /**
     *내가 듣는 강좌에 대한 진도율(section별 진도율이랑 다름 getMyPlanInfo랑 다름)
     */
    public List<MyLectureListDTO> getMyLectureList(int memberId, int page, int size, int sort) {

        Member member = memberRepository.findByMemberId(memberId);

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
            // 전체 비디오 수와 완료된 비디오 수 계산
            int[] videoCounts = countTotalAndCompletedVideos(memberId, lecture);
            int totalVideos = videoCounts[0];
            int completedVideos = videoCounts[1];
            //여기서 진도율은 해당 과목을 수강중인 회원들의 progress의 평균
            //구하는 방법은, 각 회원의 MyLectureList에 progress를 가져와서 총 회원수로 나눔
            int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideos * 100 / totalVideos);

            Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());//해당 과목 전체 평균임,각 멤버가준 점수가아니라
            if (averageRating == null) averageRating = 0;

            MyLectureListDTO myLectureListDTO = MyLectureListDTO.builder()
                    .memberId(memberId)
                    .lectureId(lecture.getId())
                    .teacherId(lecture.getMember().getId())
                    .rating(averageRating)
                    .progress(progress)
                    .teacherName(lecture.getMember().getName())
                    .lectureName(lecture.getName())
                    .thumb(domain + port + "/eumCodingImgs/lecture/thumb/" + lecture.getThumb())
                    .payDay(payDay)
                    .build();

            lectureProgressList.add(myLectureListDTO);
        }
        return lectureProgressList;
    }



    public List<SearchMylectureDTO> getSearchMyLecture(int memberId, int page, int sort, int size, String keyword) {
        Page<Lecture> lecturePage = null;
        switch (sort) {
            case 0:
                lecturePage = lectureRepository.findByName(keyword, memberId, PageRequest.of(page - 1, size));
                break;
            case 1:
                lecturePage = lectureRepository.findByPayDayDesc(keyword, memberId, PageRequest.of(page - 1, size));
                break;
            default:
                break;
        }

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
            SearchMylectureDTO searchMylectureDTO = SearchMylectureDTO.builder()
                    .lectureId(lecture.getId())
                    .score(score)
                    .teacherId(lecture.getMember().getId())
                    .teacherName(lecture.getMember().getName())
                    .lectureName(lecture.getName())
                    .progress(progress)
                    .build();

            searchMylectureDTOList.add(searchMylectureDTO);
        }

        return searchMylectureDTOList;
    }



/*  DB에서 완료된 강좌, 총 강좌갯수 구한게아니라, 해당 메서드에서 이루어짐, 그래서 아래 메서드로 변경(DB에서 계산하고 가져오는식으로)
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
    }*/

    //DB에서 해당 과목에 속하는 섹션들의 비디오들의 총 갯수(total), 다들은 비디오 갯수(completed)를 구함
    //만약 나중에 이거 문제생기면 위에 코드상에서 계산한걸로 사용
    private int[] countTotalAndCompletedVideos(int memberId, Lecture lecture) {
        int totalVideos = 0;
        int completedVideos = 0;

        List<Section> sections = sectionRepository.findByLectureId(lecture.getId());

        for (Section section : sections) {
            List<Object[]> videoCounts  = videoRepository.findTotalAndCompletedVideosForSection(memberId, section.getId());
            if (!videoCounts.isEmpty()) {
                Object[] counts = videoCounts.get(0);
                totalVideos += toInt(counts[0]);
                completedVideos += toInt(counts[1]);
            }
        }
        return new int[] { totalVideos, completedVideos };
    }

    //형변환 에러방지
    private int toInt(Object obj) {
        if (obj instanceof BigInteger) {
            return ((BigInteger) obj).intValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + obj.getClass().getName());
        }
    }

}