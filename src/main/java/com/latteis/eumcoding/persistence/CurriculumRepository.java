package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {
    @Query("SELECT c FROM Curriculum c JOIN c.member m JOIN c.section s where m.id = :memberId ")
    List<Curriculum> findByMemberId(@Param("memberId") int memberId);

    Curriculum findBySectionId(int sectionId);

    Optional<Curriculum> findById(int curriculumId);


}
