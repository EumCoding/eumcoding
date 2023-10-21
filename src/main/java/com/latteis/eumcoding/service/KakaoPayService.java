package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.KakaoPayApproveResponseDTO;
import com.latteis.eumcoding.dto.KakaoPayCancelResponseDTO;
import com.latteis.eumcoding.dto.KakaoPayReadyResponseDTO;
import com.latteis.eumcoding.dto.payment.PaymentOKRequestDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {
    static final String cid = "TC0ONETIME"; // 가맹점 테스트 코드
    static final String admin_Key = "f8b4d6e4146a41ed25dcb659d9eea6c1"; // 공개 조심

    private KakaoPayReadyResponseDTO kakaoPayReadyResponseDTO;
    private final KakaoPayRepository kakaoPayRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;
    private final CurriculumRepository curriculumRepository;
    private final PaymentRepository paymentRepository;
    private final BasketRepository basketRepository;
    private final PayLectureRepository payLectureRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final VideoRepository videoRepository;
    private final PaymentService paymentService;


    public KakaoPayReadyResponseDTO kakaoPayReady(int memberId, PaymentOKRequestDTO paymentOKRequestDTO) {
        // UUID 생성하여 partner_order_id에 할당
        // 가맹점 주문 번호
        String partnerOrderId = String.valueOf(paymentOKRequestDTO.getLectureId());
        Integer partnerUserId = memberId; // 가맹점 회원 ID 생성(구매 회원id)
        // 회원 및 강좌 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("MemberId:" + memberId));
        Lecture lecture = lectureRepository.findById(paymentOKRequestDTO.getLectureId());
        int basketLectureCount = basketRepository.countByMemberAndLecture(member, lecture);
        // basket에 해당 강좌가 담겨 있는지 확인
        Optional<Basket> existingBasket = basketRepository.findByMemberAndLecture(member, lecture);
        if (!existingBasket.isPresent()) {
            throw new IllegalArgumentException("장바구니에 해당 강좌가 없습니다. 먼저 장바구니에 담아주세요.");
        }
        // 카카오페이 요청 양식
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", partnerOrderId);
        parameters.add("partner_user_id", String.valueOf(partnerUserId));
        log.info("Generated partnerOrderId: {}", partnerOrderId);
        log.info("Generated partnerUserId: {}", partnerUserId);
        parameters.add("item_name", lecture.getName());
        parameters.add("quantity", String.valueOf(basketLectureCount));
        parameters.add("total_amount", String.valueOf(basketLectureCount * lecture.getPrice()));
        parameters.add("vat_amount", "0");
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", "http://localhost:8088/payment/success"); // 성공 시 redirect url
        parameters.add("cancel_url", "http://localhost:8088/payment/cancel"); // 취소 시 redirect url
        parameters.add("fail_url", "http://localhost:8088/payment/fail"); // 실패 시 redirect url

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        try {
            kakaoPayReadyResponseDTO = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/ready",
                    requestEntity,
                    KakaoPayReadyResponseDTO.class);

            log.info("카카오페이api: {}", kakaoPayReadyResponseDTO);
            // 수동으로 partner_order_id와 partner_user_id 설정
            kakaoPayReadyResponseDTO.setPartner_order_id(partnerOrderId);
            kakaoPayReadyResponseDTO.setPartner_user_id(String.valueOf(partnerUserId));

            return kakaoPayReadyResponseDTO;
        } catch (HttpStatusCodeException exception) {
            String errorPayload = exception.getResponseBodyAsString();
            log.error("카카오페이api 에러: {}", errorPayload, exception);
            return null;
        }
    }

    /**
     * 카카오 요구 헤더값
     */
    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + admin_Key;

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }

    /**
     * 카카오 페이로 결제 시에도 payment, payLecture에 해당 데이터 값들이 들어가야함
     */
    public void paymentSave(int memberId, KakaoPayApproveResponseDTO kakaoPayApproveResponseDTO) throws Exception {
        Member member = memberRepository.findByMemberId(memberId);
        Lecture lecture = lectureRepository.findById(Integer.parseInt(kakaoPayApproveResponseDTO.getPartner_order_id()));

        List<PayLecture> existingPayLecture = payLectureRepository.findByMemberAndLecture(memberId, Integer.parseInt(kakaoPayApproveResponseDTO.getPartner_order_id()));

        if (existingPayLecture != null && !existingPayLecture.isEmpty()) {
            throw new Exception("이미 결제 완료된 강좌입니다.");
        }

        List<KakaoPay> existingKakaoPay = kakaoPayRepository.findKakaoPayInfo(memberId);
        for (KakaoPay kakaoPay : existingKakaoPay) {
            if (kakaoPay.getPartnerOrderId().equals(kakaoPayApproveResponseDTO.getPartner_order_id())) {
                throw new Exception("이미 카카오페이로 결제 완료된 강좌입니다.");
            }
        }

        // 결제가 성공적으로 완료되었으면 KakaoPay 엔터티에 저장
        KakaoPay kakaoPay = KakaoPay.builder()
                .kakaoPaymentId(kakaoPayApproveResponseDTO.getTid())//성공하면 이거지울거임
                .partnerOrderId(kakaoPayApproveResponseDTO.getPartner_order_id())
                .partnerUserId(Integer.parseInt(kakaoPayApproveResponseDTO.getPartner_user_id()))
                .totalAmount(kakaoPayApproveResponseDTO.getAmount().getTotal())
                .createdAt(LocalDateTime.now())
                .tid(kakaoPayApproveResponseDTO.getTid())
                .state(1) //0:실패 1:성공 2:삭제
                .build();
        //kakaoPay결제 완료된 강좌에대해선 예외처리해야함


        kakaoPayRepository.save(kakaoPay); // KakaoPayRepository를 사용하여 데이터베이스에 저장
        Basket basket = basketRepository.findByMemberIdAndLectureId(memberId, Integer.parseInt(kakaoPayApproveResponseDTO.getPartner_order_id()));
        basketRepository.delete(basket);


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
        for (Section section : sections) {
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

    /**
     * 결제 완료 승인
     */
    @Transactional
    public KakaoPayApproveResponseDTO approveResponse(int memberId, String pgToken) {
        // 카카오 요청
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", kakaoPayReadyResponseDTO.getTid());
        parameters.add("partner_order_id", kakaoPayReadyResponseDTO.getPartner_order_id());
        parameters.add("partner_user_id", kakaoPayReadyResponseDTO.getPartner_user_id());
        parameters.add("pg_token", pgToken);

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayApproveResponseDTO approveResponse = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/approve",
                    requestEntity,
                    KakaoPayApproveResponseDTO.class);

            paymentSave(memberId, approveResponse);

            return approveResponse;
        } catch (HttpStatusCodeException exception) {
            String errorPayload = exception.getResponseBodyAsString();
            log.error("카카오페이 api 에러", errorPayload, exception);
            throw new RuntimeException("결제 실패: " + errorPayload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 결제 취소(환불말고)
     */

    @Transactional
    public KakaoPayCancelResponseDTO cancelPayment(int memberId, String partnerOrderId) throws Exception {
        KakaoPay kakaoPay = kakaoPayRepository.findKakaoPay(memberId, partnerOrderId);
        List<Payment> payment = paymentRepository.findMemberId(memberId);

        for(Payment payments : payment){
            paymentService.cancelPayment(memberId,payments.getId());
        }

        if (kakaoPay.getTid() == null || kakaoPay.getTid().isEmpty()) {
            throw new IllegalArgumentException("취소할 수 있는 결제목록이 없습니다.");
        }
        if (kakaoPay.getPartnerOrderId() == null || kakaoPay.getPartnerOrderId().isEmpty() || kakaoPay.getState() == 2) {
            throw new IllegalArgumentException("취소할 수 있는 결제 과목이 없습니다.");
        }
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid); // 가맹점 코드 입력
        parameters.add("tid", kakaoPay.getTid());
        parameters.add("partner_user_id", String.valueOf(kakaoPay.getPartnerUserId()));
        parameters.add("cancel_tax_free_amount", "0");
        parameters.add("cancel_amount", "1");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        ResponseEntity<KakaoPayCancelResponseDTO> response = restTemplate.exchange(
                "https://kapi.kakao.com/v1/payment/cancel",
                HttpMethod.POST,
                requestEntity,
                KakaoPayCancelResponseDTO.class
        );

        kakaoPay.setState(2);
        kakaoPayRepository.save(kakaoPay);


        return response.getBody();
    }
}


