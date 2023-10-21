package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.payment.PaymentDTO;
import com.latteis.eumcoding.dto.payment.PaymentOKRequestDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommonPaymentService {

    @Value("${file.path}")
    private String filePath;

    @Value("${file.path.lecture.thumb}")
    private String lecturePath;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;

    public File getMemberDirectoryPath() {
        File file = new File(filePath);
        file.mkdirs();

        return file;
    }

    public File getLectureDirectoryPath() {
        File file = new File(lecturePath);
        file.mkdirs();

        return file;
    }

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final PayLectureRepository payLectureRepository;
    private final BasketRepository basketRepository;
    private final SectionRepository sectionRepository;
    private final CurriculumRepository curriculumRepository;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final VideoRepository videoRepository;




    @Transactional
    public void cancelPayment(int memberId, int paymentId) throws Exception {
        // 회원 확인
        Member member = memberRepository.findByMemberId(memberId);

        List<PayLecture> payLectures = payLectureRepository.findByPaymentId(paymentId);
        for(PayLecture payLecture : payLectures){
            Lecture lecture = payLecture.getLecture();
            int progress = calculateLectureProgress(memberId, lecture);
            if (progress >= 10) {
                throw new Exception("강좌 진행률이 10% 이상이므로 취소가 불가능합니다.");
            }
        }

        // 결제 확인
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new Exception("해당 결제가 존재하지 않습니다."));


        // 결제 회원 확인
        if (payment.getMember().getId() != member.getId()) {
            throw new Exception("해당 회원의 결제가 아닙니다.");
        }

        // 결제 상태 확인
        if (payment.getState() != 1) { // 1: 결제 완료
            throw new Exception("이미 취소되었거나 완료되지 않은 결제입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(payment.getPayDay(),now);
        if(duration.toDays() > 7){
            throw new Exception("강좌 취소 날짜가 지났습니다.");
        }

        // 결제 상태를 취소로 변경 (0: 취소 상태로 설정 -> 0:실패 2:취소)
        payment.setState(2);
        paymentRepository.save(payment);

        // 관련된 커리큘럼 삭제
        List<Curriculum> curriculums = curriculumRepository.findByDeleteMemberId(memberId);
        for (Curriculum curriculum : curriculums) {
            curriculumRepository.delete(curriculum);
        }

        //관련된 lectureProgress삭제
        List<LectureProgress> lectureProgresses = lectureProgressRepository.findByDeleteLectureProgressId(memberId);
        for (LectureProgress lectureProgress : lectureProgresses){
            lectureProgressRepository.delete(lectureProgress);
        }

        //관련된 videoProgress삭제
        List<VideoProgress> vp = videoProgressRepository.findByDeleteVideoProgressId(memberId);
        for (VideoProgress videoProgress : vp){
            videoProgressRepository.delete(videoProgress);
        }

    }

    //결제 상태 저장메서드
    private String convertStateToKorean(int state) {
        switch (state) {
            case 0:
                return "실패";
            case 1:
                return "성공";
            case 2:
                return "취소";
            default:
                throw new IllegalArgumentException("Invalid payment state: " + state);
        }
    }




    private int calculateLectureProgress(int memberId, Lecture lecture) {
        int[] videoCounts = countTotalAndCompletedVideos(memberId, lecture);
        int totalVideos = videoCounts[0];
        int completedVideos = videoCounts[1];
        return totalVideos == 0 ? 0 : (int) Math.round((double) completedVideos * 100 / totalVideos);
    }


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