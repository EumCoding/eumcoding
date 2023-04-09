package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    // id로 Member 가져오기
    @Query(value = "SELECT * FROM member WHERE id = :id", nativeQuery = true)
    Member findByMemberId(@Param("id") int id);
}
