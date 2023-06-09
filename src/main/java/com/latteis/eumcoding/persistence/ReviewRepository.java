package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Review findById(int id);

    // id와 memberId로 엔티티 가져오기
    Review findByIdAndMemberId(int id, int memberId);

    // lectureId와 memberId에 맞는 엔티티 있는지 확인
    boolean existsByLectureIdAndMemberId(int lectureId, int memberId);

    // 내가 작성한 리스트
    @Query(value = "SELECT r.id, r.member_id, m.nickname, r.content, r.rating, r.created_day, COUNT(ir.review_id) as heart " +
            "FROM review r left join interest_review ir on r.id = ir.review_id, member m " +
            "WHERE m.id = r.member_id AND r.lecture_id = :lectureId " +
            "GROUP BY r.id " +
            "ORDER BY r.created_day DESC ", nativeQuery = true)
    Page<Object[]> getReviewList(Pageable pageable, @Param("lectureId") int lectureId);

    // 내가 작성한 리스트
    @Query(value = "SELECT r.id, r.content, r.rating, r.created_day, COUNT(ir.review_id) as heart " +
            "FROM review r left join interest_review ir on r.id = ir.review_id " +
            "WHERE r.member_id = :memberId " +
            "GROUP BY r.id " +
            "ORDER BY r.created_day DESC ", nativeQuery = true)
    Page<Object[]> getMyReviewList(@Param("memberId") int memberId, Pageable pageable);

    /*
     * Lecture에 맞는 리뷰 갯수 가져오기
     */
    long countByLecture(Lecture lecture);

    /*
     * Lecture에 맞는 리뷰 갯수 가져오기
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lecture = :lecture")
    String avgRating(@Param("lecture") Lecture lecture);

}

