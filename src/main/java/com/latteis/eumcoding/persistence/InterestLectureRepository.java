package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.InterestLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestLectureRepository extends JpaRepository<InterestLecture, Integer> {

    @Query(value = "SELECT * FROM interest_lecture WHERE lecture_id = :lectureId AND member_id = :memberId", nativeQuery = true)
    InterestLecture findByLectureIdAndMemberId(@Param("lectureId") int lectureId, @Param("memberId") int memberId);
}
