package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.BasketDTO;
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
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class BasketService {
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final BasketRepository basketRepository;
    private final PayLectureRepository payLectureRepository;



    @Value("${file.path.lecture.thumb}")
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

    @Transactional
    public BasketDTO addBasket(int memberId, BasketDTO.BasketAddDTO basketAddDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("MemberId:" + memberId));

        Lecture lecture = lectureRepository.findById(basketAddDTO.getLectureId());
        if(lecture == null){
            throw new IllegalArgumentException("강좌가 없습니다.");
        }


        Optional<Basket> existingBasket = basketRepository.findByMemberAndLecture(member, lecture);
        if(existingBasket.isPresent()){
            throw new IllegalArgumentException("장바구니에 있습니다.");
        }

        List<PayLecture> payLectures = payLectureRepository.findByMemberAndLecture(memberId, lecture.getId());
        for(PayLecture payLecture : payLectures)
        {
            if(payLecture.getLecture().getId() == lecture.getId()){
                throw new IllegalArgumentException("이미 구매한 강좌입니다.");
            }
        }


        Basket basket = Basket.builder()
                .member(member)
                .lecture(lecture)
                .build();

        Basket savedBasket = basketRepository.save(basket);
        BasketDTO basketDTO = convertToDTO(savedBasket);

        return basketDTO;
    }

    @Transactional
    public BasketDTO removeBasket(int memberId, int basketId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new IllegalArgumentException("BasketId: " + basketId));

        if(basket.getMember().getId() != memberId){
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        basketRepository.delete(basket);
        BasketDTO basketDTO = convertToDTO(basket);

        return basketDTO;
    }

    @Transactional
    public List<BasketDTO> getBasketlist(int memberId,int page) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("MemberId:" + memberId));

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<Basket> basketPage = basketRepository.findByMemberIdAndRoleAndState(member,pageable);
        List<Basket> baskets = basketPage.getContent();

        List<BasketDTO> basketDTOs = new ArrayList<>();
        for (Basket basket : baskets) {
            BasketDTO dto = convertToDTO(basket);
            basketDTOs.add(dto);
        }

        return basketDTOs;
    }


    //entity->dto변환
    private BasketDTO convertToDTO(Basket basket){
        return BasketDTO.builder()
                .basketId(basket.getId())
                .memberId(basket.getMember().getId())
                .lectureId(basket.getLecture().getId())
                .lectureName(basket.getLecture().getName())
                .price(basket.getLecture().getPrice())
                .teacherName(basket.getLecture().getMember().getName())
                .thumb(domain + port + "/eumCodingImgs/basket/" + basket.getLecture().getThumb())
                .build();
    }
}