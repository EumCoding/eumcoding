package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.MainTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MainTestQuestionRepository extends JpaRepository<MainTestQuestion, Integer> {

    //countByMainTest
    // 해당 메인 평가의 문제 수 가져오기
    long countByMainTest(MainTest mainTest);

    //findAllByMainTestId
    // 해당 메인 평가의 문제 모두 List로 가져오기
    List<MainTestQuestion> findAllByMainTest(MainTest mainTest);

    @Query(value = "SELECT mtq FROM MainTestQuestion mtq WHERE mtq.id = :id")
    MainTestQuestion findByMainTestQuestionId(@Param("id") int id);


}
