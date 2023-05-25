package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.payment.PaymentDTO;
import com.latteis.eumcoding.dto.payment.PaymentOKRequestDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final LectureRepository lectureRepository;

    private final MemberRepository memberRepository;

    private final PaymentRepository paymentRepository;

    private final PayLectureRepository payLectureRepository;

    private final BasketRepository basketRepository;

    @Transactional
    public void completePayment(int memberId, PaymentOKRequestDTO paymentOKRequestDTO) throws Exception {

        Member member = memberRepository.findByMemberId(memberId);

        Basket basket = basketRepository.findByMemberIdAndLectureId(memberId,paymentOKRequestDTO.getLectureId());
        if (basket == null) {
            throw new Exception("해당 강의가 장바구니에 없습니다.");
        }

        Lecture lecture = lectureRepository.findById(paymentOKRequestDTO.getLectureId());

        // lecture의 state가 1이 아니라면 예외 처리 0:미승인강좌
        if (lecture.getState() != 1) {
            throw new Exception("등록 되지 않은 강좌입니다.");
        }

        PayLecture existingPayLecture = payLectureRepository.findByMemberAndLecture(memberId, paymentOKRequestDTO.getLectureId());
        if (existingPayLecture != null) {
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
    }


    public List<PaymentDTO> getMyPayments(int memberId, Pageable pageable) {
        int pageNumber = pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0;
        PageRequest modifiedPageable = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort());

        Page<Payment> payments = paymentRepository.findByMemberId(memberId, modifiedPageable);

        List<PaymentDTO> paymentDTOs = new ArrayList<>();

        for (Payment payment : payments) {
            List<PayLecture> payLectures = payLectureRepository.findByPaymentId(payment.getId());

            List<LectureDTO.PayLectureIdNameDTO> lectureDTOList = new ArrayList<>();

            for (PayLecture payLecture : payLectures) {
                Lecture lecture = payLecture.getLecture();

                LectureDTO.PayLectureIdNameDTO lectureDTO = LectureDTO.PayLectureIdNameDTO.builder()
                        .id(lecture.getId())
                        .name(lecture.getName())
                        .price(lecture.getPrice())
                        .build();

                lectureDTOList.add(lectureDTO);
            }

            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .paymentId(payment.getId())
                    .date(payment.getPayDay())
                    .lectureDTOList(lectureDTOList)
                    .build();

            paymentDTOs.add(paymentDTO);
        }

        return paymentDTOs;
}


}