package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.service.CurriculumService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {
    @Query("SELECT c FROM Curriculum c JOIN c.member m JOIN c.section s where m.id = :memberId ")
    List<Curriculum> findByMemberId(@Param("memberId") int memberId);

    @Query("SELECT c FROM Curriculum c WHERE c.section.id = :sectionId AND c.member.id = :memberId")
    Curriculum findBySectionId(@Param("sectionId")int sectionId ,@Param("memberId") int memberId);

    @Query("SELECT c FROM Curriculum c JOIN Member m ON c.member.id = m.id WHERE c.id = :curriculumId AND c.member.id = :memberId AND c.member.role = 0")
    Optional<Curriculum> findByCurriculumId(@Param("memberId")int memberId,@Param("curriculumId")int curriculumId);


    //과목별 학생 점수
    //이를 바탕으로 해당 과목을 듣는 학생들의 평균 점수를 구해서 
    //선생님 마이페이지에 표시되도록 하기 위함
    @Query(value = "SELECT AVG(member_score) " +
            "FROM (SELECT SUM(c.score) as member_score " +
            "      FROM Curriculum c " +
            "      JOIN Section s ON c.section_id = s.id " +
            "      JOIN Lecture l ON s.lecture_id = l.id " +
            "      WHERE l.id =:lectureId " +
            "      GROUP BY c.member_id) As subQuery", nativeQuery = true)
    Optional<Float> findByAVGLectureScore(@Param("lectureId")int lectureId);


    @Query("SELECT c FROM Curriculum c WHERE c.edit = :edit")
    CurriculumService findByEditId(@Param("edit")int edit);
}
