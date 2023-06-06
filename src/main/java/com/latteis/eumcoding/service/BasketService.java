package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.BasketDTO;
import com.latteis.eumcoding.model.Basket;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.BasketRepository;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class BasketService {
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final BasketRepository basketRepository;


    @Transactional
    public BasketDTO addBasket(int memberId, BasketDTO.BasketAddDTO basketAddDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("MemberId:" + memberId));

        Lecture lecture = lectureRepository.findById(basketAddDTO.getLectureId());
        if(lecture == null){
            new IllegalArgumentException("강좌가 없습니다.");
        }


        Optional<Basket> existingBasket = basketRepository.findByMemberAndLecture(member, lecture);
        if(existingBasket.isPresent()){
            throw new IllegalArgumentException("장바구니에 있습니다.");
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

        BasketDTO basketDTO = convertToDTO(basket);
        basketRepository.delete(basket);

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


    private BasketDTO convertToDTO(Basket basket){
        return BasketDTO.builder()
                .basketId(basket.getId())
                .memberId(basket.getMember().getId())
                .lectureId(basket.getLecture().getId())
                .lectureName(basket.getLecture().getName())
                .price(basket.getLecture().getPrice())
                .teacherName(basket.getLecture().getMember().getName())
                .thumb(basket.getLecture().getThumb())
                .build();
    }
}