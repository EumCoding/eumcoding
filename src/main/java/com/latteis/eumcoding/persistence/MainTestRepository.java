package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MainTestRepository extends JpaRepository<MainTest, Integer> {

    List<MainTest> findAllBySectionIdAndType(int sectionId, int type);

    MainTest findById(int id);


    @Query(value = "SELECT COUNT(mt) FROM MainTest mt WHERE mt.type = :type AND mt.section.lecture = :lecture")
    long countByTypeAndLecture(@Param("type") int type, @Param("lecture") Lecture lecture);

    boolean existsByTypeAndSectionLecture(int type, Lecture lecture);
    boolean existsBySection(Section section);

    //findAllBySectionLectureId
    // 해당 강의의 메인 평가 정보 가져오기
    List<MainTest> findAllBySection(Section section);

    //findAllByLectureIdAndType
    // 해당 강의의 메인 평가 정보 가져오기
    List<MainTest> findAllBySectionLectureIdAndType(int lectureId, int type);

    /**
     * @param lecture Lecture Entity
     * @return MainTest List
     */
    List<MainTest> findBySectionLecture(Lecture lecture);
}
