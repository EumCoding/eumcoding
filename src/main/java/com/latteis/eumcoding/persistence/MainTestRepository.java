package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MainTestRepository extends JpaRepository<MainTest, Integer> {

    MainTest findById(int id);


    @Query(value = "SELECT COUNT(mt) FROM MainTest mt WHERE mt.type = :type AND mt.section.lecture = :lecture")
    long countByTypeAndLecture(@Param("type") int type, @Param("lecture") Lecture lecture);

    boolean existsByTypeAndSectionLecture(int type, Lecture lecture);
    boolean existsBySection(Section section);
}
