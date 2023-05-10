package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Integer> {
    List<Section> findByLectureId(int lectureId);
    @Query("SELECT COUNT(v) FROM Video v WHERE v.section.id = :sectionId")
    long countBySectionId(int sectionId);

    Optional<Section> findById(int sectionId);



/*    @Query(value = "SELECT SUM(TIME_TO_SEC(play_time) / 60) FROM video WHERE section_id = :sectionId", nativeQuery = true)
    Integer calculateTotalPlayTime(@Param("sectionId") int sectionId);*/




}
