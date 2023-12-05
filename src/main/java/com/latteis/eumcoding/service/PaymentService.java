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
    private final CommonPaymentService commonPaymentService;


    /**
     * 강좌 결제, 여러개 구매할수있게 List로
     * 11.12 -> lectureId로 결제하는걸 basketId로 결제하도록 변경
     */
    @Transactional
    public void completePayment(int memberId, List<Integer> basketIds) throws Exception {

        Member member = memberRepository.findByMemberId(memberId);
        Payment payment = new Payment();
        payment.setMember(member);
        payment.setPayDay(LocalDateTime.now());
        payment.setState(1); // 결제 완료 상태로 설정
        // Payment 저장
        Payment savedPayment = paymentRepository.save(payment);

        for (Integer basketId : basketIds) {
            Basket basket = basketRepository.findByBasketId(memberId, basketId);

            if (basket == null) {
                throw new Exception("해당 강의가 장바구니에 없습니다.");
            }
            Lecture lecture = basket.getLecture();
            // lecture의 state가 1이 아니라면 예외 처리 0:미승인강좌
            if (lecture.getState() != 1) {
                throw new Exception("등록 되지 않은 강좌입니다.");
            }

            List<PayLecture> existingPayLecture = payLectureRepository.findByMemberAndLecture(memberId, lecture.getId());
            if (existingPayLecture != null && !existingPayLecture.isEmpty()) {  //빈문자열이 들어올경우에 이미 결제완료된 강좌입니다로 표시된다 이를방지해야함
                throw new Exception("이미 결제 완료된 강좌입니다.");
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

            // 섹션을 저장할 때 첫 섹션의 시작 날짜를 오늘로 설정
            LocalDateTime currentStartDay = LocalDateTime.now();

            for (Section section : sections) {
                Curriculum curriculum = new Curriculum();
                curriculum.setSection(section);
                curriculum.setMember(member);
                curriculum.setTimeTaken(section.getTimeTaken());
                curriculum.setScore(0);
                curriculum.setCreateDate(LocalDate.now());
                curriculum.setEdit(1);
                curriculum.setStartDay(currentStartDay);
                curriculumRepository.save(curriculum);

                // 다음 섹션의 startDay는 현재 섹션의 startDay에 timeTaken만큼 더한 날짜
                currentStartDay = currentStartDay.plusDays(curriculum.getTimeTaken());

            }
            basketRepository.delete(basket); // 장바구니에서 해당 항목 제거

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

            }

            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .paymentId(payment.getId())
                    .memberId(payment.getMember().getId())
                    .date(payment.getPayDay())
                    .lectureDTOList(lectureDTOList)
                    .stateDescription(convertStateToKorean(payment.getState()))
                    .build();

            paymentDTOs.add(paymentDTO);
        }

        return paymentDTOs;
    }

    @Transactional
    public void cancelPayment(int memberId, int paymentId) throws Exception {
        commonPaymentService.cancelPayment(memberId,paymentId);
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

}