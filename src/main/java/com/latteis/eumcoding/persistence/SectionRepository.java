package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Integer> {


    @Query("SELECT s FROM Section s WHERE s.id IN (SELECT c.section.id FROM Curriculum c WHERE c.id = :curriculumId)")
    List<Section> findByCurriculumId(@Param("curriculumId")int curriculumId);



    @Query("SELECT COUNT(v) FROM Video v WHERE v.section.id = :sectionId")
    long countBySectionId(int sectionId);

    Optional<Section> findById(int sectionId);

/*    @Query(value = "SELECT SUM(TIME_TO_SEC(play_time) / 60) FROM video WHERE section_id = :sectionId", nativeQuery = true)
    Integer calculateTotalPlayTime(@Param("sectionId") int sectionId);*/

    @Query(value = "SELECT * FROM section WHERE id = :id", nativeQuery = true)
    Section findBySectionId(@Param("id") int id);

    // lecture의 모든 section 가져오기
    @Query(value = "SELECT id, time_taken, name FROM section WHERE lecture_id = :lectureId ORDER BY sequence", nativeQuery = true)
    List<Object[]> findListByLecture(@Param("lectureId") int lectureId);

}
