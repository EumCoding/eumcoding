package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Integer> {

    // Lecture를 기준으로 모든 Section을 찾는 쿼리
    @Query("SELECT s FROM Section s WHERE s.lecture = :lecture")
    List<Section> findByLecture(@Param("lecture") Lecture lecture);


    List<Section> findByLectureId(int lectureId);

    @Query("SELECT s FROM Section s WHERE s.id IN (SELECT c.section.id FROM Curriculum c WHERE c.id = :curriculumId)")
    List<Section> findByCurriculumId(@Param("curriculumId")int curriculumId);



    @Query("SELECT COUNT(v) FROM Video v WHERE v.section.id = :sectionId")
    long countBySectionId(int sectionId);

    Optional<Section> findById(int sectionId);

/*    @Query(value = "SELECT SUM(TIME_TO_SEC(play_time) / 60) FROM video WHERE section_id = :sectionId", nativeQuery = true)
    Integer calculateTotalPlayTime(@Param("sectionId") int sectionId);*/

    @Query(value = "SELECT * FROM section WHERE id = :id", nativeQuery = true)
    Section findBySectionId(@Param("id") int id);

    /*
    * SectionId와 강사에 맞는 Section 가져오기
    */
    Section findByIdAndLectureMember(int id, Member member);

    /*
     * Lecture, sequence에 맞는 Section 가져오기
     */
    Section findByLectureAndSequence(Lecture lecture, int sequence);

    /*
    *
    * */
    List<Section> findAllByLectureAndSequenceGreaterThan(Lecture lecture, int sequence);

    /*
    * Lecture로 섹션 순서대로 가져오기
    */
    List<Section> findAllByLectureOrderBySequence(Lecture lecture);


    /*
    * 학생이 시청한 기록이 있는 동영상의 섹션만 가져오기
    */
    @Query("SELECT s FROM Section s " +
            "WHERE " +
                "(SELECT MAX(vp.video.section.sequence) FROM VideoProgress vp " +
                "WHERE vp.lectureProgress.payLecture.payment.member = :member) >= s.sequence " +
            "ORDER BY s.sequence")
    List<Section> getLectureStudentSection(@Param("member") Member member);
}
