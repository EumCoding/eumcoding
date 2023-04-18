package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.model.InterestReview;
import com.latteis.eumcoding.persistence.InterestReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestReviewService {

    private final InterestReviewRepository interestReviewRepository;

    // 좋아요 추가
    public void addHeart(int memberId, ReviewDTO.IdRequestDTO idRequestDTO) {

        try {

            InterestReview interestReview = InterestReview.builder()
                    .memberId(memberId)
                    .reviewId(idRequestDTO.getId())
                    .build();
            interestReviewRepository.save(interestReview);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("InterestReviewService.addHeart() : 에러 발생");
        }

    }

    // 좋아요 삭제
    public void deleteHeart(int memberId, ReviewDTO.IdRequestDTO idRequestDTO) {

        try {

            InterestReview interestReview = interestReviewRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
            interestReviewRepository.delete(interestReview);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("InterestReviewService.deleteHeart() : 에러 발생");
        }

    }
}
