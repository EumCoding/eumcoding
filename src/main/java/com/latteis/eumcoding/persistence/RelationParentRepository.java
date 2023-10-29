package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.ReplationParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelationParentRepository extends JpaRepository<ReplationParent, Integer> {

    @Query(value = "SELECT * FROM replation_parent WHERE parents_id = :parentsId and member_id = :memberId", nativeQuery = true)
    Optional<ReplationParent> findByParentIdChildId(@Param("parentsId") int parentsId,@Param("memberId") int memberId);

    //자녀 Id member_id = child_Id라고보면됨
    @Query(value = "SELECT * FROM replation_parent WHERE member_id = :memberId", nativeQuery = true)
    Optional<ReplationParent> findByMemberId(@Param("memberId") int memberId);

    @Query(value = "SELECT * FROM replation_parent r JOIN member m ON r.parents_id = m.id WHERE r.parents_id = :parentsId AND  m.role = 3", nativeQuery = true)
    Optional<ReplationParent> findByParentId(@Param("parentsId") int parentsId);

    //이미 엔티티에서 외래키 설정표시를 해놔서 여기선 딱히 조인안써도 괜찮음
    @Query("SELECT r FROM ReplationParent r WHERE r.child.id = :childId")
    Optional<ReplationParent> findByChildId(@Param("childId") int childId);

    @Query(value = "SELECT rp FROM ReplationParent rp WHERE rp.parent.id = :parentsId")
    List<ReplationParent> findChildId(@Param("parentsId") int parentsId);


  /*  @Query(value = "SELECT count(*) FROM replation_parent WHERE parents_id = :parentsId", nativeQuery = true)
    Integer findCountChildId(@Param("parentsId") int parentsId);*/


}
