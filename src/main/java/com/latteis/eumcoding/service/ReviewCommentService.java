package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.ReviewCommentDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Review;
import com.latteis.eumcoding.model.ReviewComment;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.ReviewCommentRepository;
import com.latteis.eumcoding.persistence.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;

    private final ReviewRepository reviewRepository;

    private final MemberRepository memberRepository;

    // 리뷰 댓글 작성
    public void writeReviewComment(int memberId, ReviewCommentDTO.WriteRequestDTO writeRequestDTO) {

        try {

            Review review = reviewRepository.findById(writeRequestDTO.getReviewId());
            Member member = memberRepository.findByMemberId(memberId);

            ReviewComment reviewComment = ReviewComment.builder()
                    .review(review)
                    .member(member)
                    .content(writeRequestDTO.getContent())
                    .commentDay(LocalDateTime.now())
                    .modified(0)
                    .build();
            reviewCommentRepository.save(reviewComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewCommentService.writeReviewComment() : 에러 발생");
        }

    }

    // 리뷰 댓글 수정
    public void updateReviewComment(int memberId, ReviewCommentDTO.UpdateRequestDTO updateRequestDTO) {

        try {


            ReviewComment reviewComment = reviewCommentRepository.findByIdAndMemberId(updateRequestDTO.getId(), memberId);
                    reviewComment.setContent(updateRequestDTO.getContent());
                    reviewComment.setModified(1);
            reviewCommentRepository.save(reviewComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewCommentService.updateReviewComment() : 에러 발생");
        }

    }

    // 리뷰 댓글 삭제
    public void deleteReviewComment(int memberId, ReviewCommentDTO.IdRequestDTO idRequestDTO) {

        try {


            ReviewComment reviewComment = reviewCommentRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
            reviewCommentRepository.delete(reviewComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewCommentService.deleteReviewComment() : 에러 발생");
        }

    }
}
