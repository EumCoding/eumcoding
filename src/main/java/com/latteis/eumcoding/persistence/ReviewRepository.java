package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // id와 memberId로 엔티티 가져오기
    Review findByIdAndMemberId(int id, int memberId);

    boolean existsByLectureIdAndMemberId(int lectureId, int memberId);

    @Query(value = "SELECT r.id, r.content, r.rating, r.created_day, COUNT(ir.review_id) as heart " +
            "FROM review r left join interest_review ir on r.id = ir.review_id " +
            "WHERE r.member_id = :memberId " +
            "GROUP BY r.id " +
            "ORDER BY r.created_day DESC ", nativeQuery = true)
    Page<Object[]> getMyReviewList(@Param("memberId") int memberId, Pageable pageable);
}

