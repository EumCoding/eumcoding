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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

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
    private final KakaoPayService kakaoPayService;
    private final KakaoPayRepository kakaoPayRepository;

    @Transactional
    public void completePayment(int memberId, PaymentOKRequestDTO paymentOKRequestDTO) throws Exception {

        Member member = memberRepository.findByMemberId(memberId);

        Basket basket = basketRepository.findByMemberIdAndLectureId(memberId,paymentOKRequestDTO.getLectureId());
        if (basket == null) {
            throw new Exception("해당 강의가 장바구니에 없습니다.");
        }else {
            basketRepository.delete(basket);
        }

        Lecture lecture = lectureRepository.findById(paymentOKRequestDTO.getLectureId());

        // lecture의 state가 1이 아니라면 예외 처리 0:미승인강좌
        if (lecture.getState() != 1) {
            throw new Exception("등록 되지 않은 강좌입니다.");
        }

        List<PayLecture> existingPayLecture = payLectureRepository.findByMemberAndLecture(memberId, paymentOKRequestDTO.getLectureId());
        if (existingPayLecture != null && !existingPayLecture.isEmpty()) {  //빈문자열이 들어올경우에 이미 결제완료된 강좌입니다로 표시된다 이를방지해야함
            throw new Exception("이미 결제 완료된 강좌입니다.");
        }

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setPayDay(LocalDateTime.now());

        // Payment 저장
        Payment savedPayment = paymentRepository.save(payment);

        // 만약 Payment가 정상적으로 저장되었으면, 결제 상태를 결제 완료:1로 설정
        if (savedPayment != null) {
            savedPayment.setState(1);
            paymentRepository.save(savedPayment);
        }

        // 새로운 PayLecture 생성
        PayLecture payLecture = new PayLecture();
        payLecture.setPayment(savedPayment);
        payLecture.setLecture(lecture);
        payLecture.setPrice(lecture.getPrice());

        // PayLecture 저장
        payLectureRepository.save(payLecture);

        //Curriculum에 해당 lecture에 속한 section들 저장
        List<Section> sections = sectionRepository.findAllByLecture(payLecture.getLecture());
        for(Section section : sections){
            Curriculum curriculum = new Curriculum();
            curriculum.setSection(section);
            curriculum.setMember(member);
            curriculum.setTimeTaken(10);
            curriculum.setScore(0);
            curriculum.setCreateDate(LocalDate.now());
            curriculum.setEdit(0);
            curriculum.setStartDay(LocalDateTime.now());
            curriculumRepository.save(curriculum);
        }
    }

    //내 결제 목록
   
    public List<PaymentDTO> getMyPayments(int memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        int pageNumber = pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0;
        PageRequest modifiedPageable = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort());

        Page<Payment> payments = paymentRepository.findByMemberId(memberId,startDate,endDate, modifiedPageable);

        List<PaymentDTO> paymentDTOs = new ArrayList<>();

        for (Payment payment : payments) {
            List<PayLecture> payLectures = payLectureRepository.findByPaymentId(payment.getId());
            List<LectureDTO.PayLectureIdNameDTO> lectureDTOList = new ArrayList<>();

                for (PayLecture payLecture : payLectures) {
                    Lecture lecture = payLecture.getLecture();
                    Integer existLectureReview = reviewRepository.existsByPayLectureIdAndMemberId(memberId,payLecture.getLecture().getId());
                    String reviewStatus = existLectureReview == 1 ? "리뷰작성완료" : "리뷰를 작성 해주세요";

                    LectureDTO.PayLectureIdNameDTO lectureDTO = LectureDTO.PayLectureIdNameDTO.builder()
                            .id(lecture.getId())
                            .name(lecture.getName())//강좌 제목
                            .teacherName(lecture.getMember().getName()) //강사이름
                            .price(lecture.getPrice())
                            .lectureImg(domain + port + "/eumCodingImgs/payment/" + lecture.getThumb())
                            .reviewStatus(reviewStatus)
                            .build();

                    lectureDTOList.add(lectureDTO);

                PaymentDTO paymentDTO = PaymentDTO.builder()
                        .paymentId(payment.getId())
                        .memberId(payment.getMember().getId())
                        .date(payment.getPayDay())
                        .lectureDTOList(lectureDTOList)
                        .stateDescription(convertStateToKorean(payment.getState()))
                        .build();

                paymentDTOs.add(paymentDTO);
            }
        }
        return paymentDTOs;
}

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