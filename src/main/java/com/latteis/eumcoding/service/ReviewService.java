package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.model.Review;
import com.latteis.eumcoding.persistence.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // 리뷰 작성
    public void writeReview(int memberId, ReviewDTO.WriteRequestDTO writeRequestDTO) {

        try {

            int lectureId = writeRequestDTO.getLectureId();
            // 이미 작성한 리뷰가 있는지 검사
            if (reviewRepository.existsByLectureIdAndMemberId(lectureId, memberId)) {
                throw new RuntimeException("BoardCommentService.writeReview() : 에러 발생");
            } else {
                Review review = Review.builder()
                        .memberId(memberId)
                        .lectureId(lectureId)
                        .content(writeRequestDTO.getContent())
                        .rating(writeRequestDTO.getRating())
                        .heart(0)
                        .createdDay(LocalDateTime.now())
                        .build();
                reviewRepository.save(review);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.writeReview() : 에러 발생");
        }

    }

    // 리뷰 수정
    public void updateReview(int memberId, ReviewDTO.UpdateRequestDTO updateRequestDTO) {
        try {

            Review review = reviewRepository.findByIdAndMemberId(updateRequestDTO.getId(), memberId);
            review.setContent(updateRequestDTO.getContent());
            review.setRating(updateRequestDTO.getRating());
            reviewRepository.save(review);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.updateReview() : 에러 발생");
        }
    }

    // 내가 작성한 리뷰 목록 가져오기
    public List<ReviewDTO.MyListResponseDTO> getMyReviewList(int memberId, Pageable pageable) {

        try {

            // 엔티티 리스트에 담기
            Page<Object[]> pageList = reviewRepository.getMyReviewList(memberId, pageable);
            List<Object[]> objects = pageList.getContent();
            List<ReviewDTO.MyListResponseDTO> myListResponseDTOList = new ArrayList<>();
            // 반복으로 DTO에 넣기
            for (Object[] object : objects) {
                // DTO에 담기
                ReviewDTO.MyListResponseDTO myListResponseDTO = new ReviewDTO.MyListResponseDTO(object);
                // 저장
                myListResponseDTOList.add(myListResponseDTO);
            }
            return myListResponseDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.getMyReviewList() : 에러 발생");
        }

    }
}
