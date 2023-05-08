package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.PayLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayLectureRepository extends JpaRepository<PayLecture, Integer> {
    
    //해당 강의를 결제한 학생들을 파악하기 위해 사용
    //totalStudent구하기위해서, state 가 0이면 성공, 1이면 실패
    @Query("SELECT p1 FROM PayLecture p1 JOIN p1.payment p JOIN p1.lecture l WHERE l.id = :lectureId AND p.state = :state")
    List<PayLecture> findByLectureIdAndState(@Param("lectureId")int lectureId, @Param("state") int state);

}
