package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTestBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainTestBlockRepository extends JpaRepository<MainTestBlock, Integer> {

    //findAllByMainTestQuestionId
    // 해당 메인 평가의 문제 모두 List로 가져오기
    List<MainTestBlock> findAllByMainTestQuestionId(int mainTestQuestionId);
}
