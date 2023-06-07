package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.ReviewCommentDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Review;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.ReviewCommentRepository;
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

    private final ReviewCommentRepository reviewCommentRepository;

    private final MemberRepository memberRepository;

    private final LectureRepository lectureRepository;

    // 리뷰 작성
    public void writeReview(int memberId, ReviewDTO.WriteRequestDTO writeRequestDTO) {

        try {

            int lectureId = writeRequestDTO.getLectureId();
            // 이미 작성한 리뷰가 있는지 검사
            if (reviewRepository.existsByLectureIdAndMemberId(lectureId, memberId)) {
                throw new RuntimeException("BoardCommentService.writeReview() : 에러 발생");
            } else {

                // Member Entity 가져오기
                Member member = memberRepository.findByMemberId(memberId);
                // Lecture Entity 가져오기
                Lecture lecture = lectureRepository.findById(lectureId);

                Review review = Review.builder()
                        .member(member)
                        .lecture(lecture)
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

    // 리뷰 삭제
    public void deleteReview(int memberId, ReviewDTO.IdRequestDTO idRequestDTO) {
        try {

            Review review = reviewRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
            reviewRepository.delete(review);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.deleteReview() : 에러 발생");
        }
    }

    // 리뷰 목록 가져오기
    public List<ReviewDTO.ListResponseDTO> getReviewList(Pageable pageable, LectureDTO.IdRequestDTO idRequestDTO) {

        // 강의 정보 가져오기
        Lecture lecture = lectureRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(lecture, "등록된 강의가 없습니다. (강의 ID : %s)", idRequestDTO.getId());

        // 오브젝트에 리스트 담기
        Page<Object[]> pageList = reviewRepository.getReviewList(pageable, idRequestDTO.getId());
        List<Object[]> objects = pageList.getContent();
        List<ReviewDTO.ListResponseDTO> listResponseDTOList = new ArrayList<>();
        // 반복으로 DTO에 넣기
        for (Object[] object : objects) {
            // ReviewDTO에 담기
            ReviewDTO.ListResponseDTO listResponseDTO = new ReviewDTO.ListResponseDTO(object);
            // 오브젝트에 댓글 담기
            Object commentObject = reviewCommentRepository.getCommentList(listResponseDTO.getId());
            // 오브젝트가 null이 아니라면 리뷰DTO에 댓글 DTO 추가
            if (commentObject != null) {
                ReviewCommentDTO.ListCommentResponseDTO listCommentResponseDTO = new ReviewCommentDTO.ListCommentResponseDTO((Object[]) commentObject);
                listResponseDTO.setListCommentResponseDTO(listCommentResponseDTO);
            }
            // 리뷰DTO 리스트에 저장
            listResponseDTOList.add(listResponseDTO);
        }
        return listResponseDTOList;

    }

    // 내가 작성한 리뷰 목록 가져오기
    public List<ReviewDTO.MyListResponseDTO> getMyReviewList(int memberId, Pageable pageable) {

        try {

            // 오브젝트에 리스트 담기
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
