package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, Integer> {
    @Query("SELECT lp FROM LectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m WHERE m.id = :memberId")
    List<LectureProgress> findByMemberId(@Param("memberId") int memberId);

}
