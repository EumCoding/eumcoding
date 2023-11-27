package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainTestAnswerRepository extends JpaRepository<MainTestAnswer, String> {

    //findAllByMainTestQuestionId
    // 해당 메인 평가의 문제 모두 List로 가져오기
    List<MainTestAnswer> findByMainTestQuestionId(int mainTestQuestionId);
}
