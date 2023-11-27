package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.MainTestAnswer;
import com.latteis.eumcoding.model.MainTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MainTestAnswerRepository extends JpaRepository<MainTestAnswer, String> {

    //findAllByMainTestQuestionId
    // 해당 메인 평가의 문제 모두 List로 가져오기
    List<MainTestAnswer> findByMainTestQuestionId(int mainTestQuestionId);

    /**
     * @param mainTest MainTest Entity
     * @return List<MainTestAnswer>
     */
    List<MainTestAnswer> findAllByMainTestQuestionMainTest(@Param("mainTest") MainTest mainTest);
    List<MainTestAnswer> findAllByMainTestQuestion(MainTestQuestion mainTestQuestion);

    MainTestAnswer findByMainTestQuestion(MainTestQuestion mainTestQuestion);
}
