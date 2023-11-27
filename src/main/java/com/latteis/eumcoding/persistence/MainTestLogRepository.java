package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.MainTestLog;
import com.latteis.eumcoding.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MainTestLogRepository extends JpaRepository<MainTestLog, String> {

    /**
     * @param member Member Entity
     * @param mainTest MainTest Entity
     * @return count
     */
    @Query(value = "SELECT COUNT(mtl) FROM MainTestLog mtl " +
            "WHERE mtl.member = :member " +
            "AND mtl.mainTestQuestion.mainTest = :mainTest ")
    long countByMemberAndMainTestQuestionMainTest(@Param("member") Member member, @Param("mainTest") MainTest mainTest);


}
