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
import java.time.LocalDate;
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

    private final SectionRepository sectionRepository;

    private final CurriculumRepository curriculumRepository;

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
            curriculumRepository.save(curriculum);
        }
    }


    public List<PaymentDTO> getMyPayments(int memberId, Pageable pageable) {
        int pageNumber = pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0;
        PageRequest modifiedPageable = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort());

        Page<Payment> payments = paymentRepository.findByMemberId(memberId, modifiedPageable);

        List<PaymentDTO> paymentDTOs = new ArrayList<>();

        for (Payment payment : payments) {
            List<PayLecture> payLectures = payLectureRepository.findByPaymentId(payment.getId());

            List<LectureDTO.PayLectureIdNameDTO> lectureDTOList = new ArrayList<>();

            if(payment.getState() == 1){
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
                        .memberId(payment.getMember().getId())
                        .date(payment.getPayDay())
                        .lectureDTOList(lectureDTOList)
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

        // 결제 상태를 취소로 변경 (0: 취소 상태로 설정)
        payment.setState(0);
        paymentRepository.save(payment);

        // 관련된 PayLecture도 삭제
        List<PayLecture> payLectures = payLectureRepository.findByPaymentId(paymentId);
        for (PayLecture payLecture : payLectures) {
           payLectureRepository.delete(payLecture);
        }
    }



}