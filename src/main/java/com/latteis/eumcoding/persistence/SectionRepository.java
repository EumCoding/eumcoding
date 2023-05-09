package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, String> {
    List<Section> findByLectureId(int lectureId);
    @Query("SELECT COUNT(v) FROM Video v WHERE v.section.id = :sectionId")
    long countBySectionId(int sectionId);

}
