package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTestMultipleChoiceView;
import com.latteis.eumcoding.model.MainTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainTestMultipleChoiceViewRepository extends JpaRepository<MainTestMultipleChoiceView, Integer> {

    //countByMainTestQuestion
    // 해당 메인 평가의 문제 수 가져오기
    long countByMainTestQuestion(MainTestQuestion mainTestQuestion);

    //findAllByMainTestQuestionId
    // 해당 메인 평가의 문제 모두 List로 가져오기
    List<MainTestMultipleChoiceView> findAllByMainTestQuestionIdOrderBySequenceDesc(int mainTestQuestionId);

    //findAllByMainTestQuestionId
    // 해당 메인 평가의 문제 모두 List로 가져오기
    List<MainTestMultipleChoiceView> findAllByMainTestQuestionId(int mainTestQuestionId);
}
