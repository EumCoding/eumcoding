package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.service.CurriculumService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {


    @Query("SELECT c FROM Curriculum c JOIN c.member m JOIN c.section s where m.id = :memberId ")
    List<Curriculum> findByMemberId(@Param("memberId") int memberId);

    
    //커리큘럼 날짜 입력해서 결과 보이게
    @Query(value = "SELECT c.* FROM curriculum c " +
            "JOIN member m ON c.member_id = m.id " +
            "JOIN section s ON c.section_id = s.id " +
            "JOIN lecture l ON s.lecture_id = l.id " +
            "WHERE m.id =:memberId " +
            "AND (:lectureId IS NULL OR l.id = :lectureId) " +
            "AND ( (c.edit_day IS NOT NULL AND c.edit_day BETWEEN :startDate AND :endDate) OR " +
            "      (c.edit_day IS NULL AND c.start_day BETWEEN :startDate AND :endDate) ) ",
            nativeQuery = true)
    List<Curriculum> findByMemberIdAndMonthYear(@Param("memberId") int memberId,
                                                @Param("lectureId") Integer lectureId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate
                                                );





    @Query("SELECT c FROM Curriculum c JOIN c.section s WHERE c.member.id = :memberId " +
            "AND (:lectureId IS NULL OR s.lecture.id = :lectureId)")
    List<Curriculum> findByMemberIdAndLectureId(@Param("memberId") int memberId, @Param("lectureId")Integer lectureId);


    @Query("SELECT c FROM Curriculum c JOIN Member m ON c.member.id = m.id WHERE c.id = :curriculumId AND c.member.id = :memberId AND c.member.role = 0")
    Optional<Curriculum> findByCurriculumId(@Param("memberId")int memberId,@Param("curriculumId")int curriculumId);


    /**
     *해당 커리큘럼에 lecture에 해당하는 section들의 합친후 평균을 구함( 각 회원마다)
     * 그리고 회원마다 구해진 과목에 평균 점수를 다 더하고 거기서 또 평균점수를 더한 회원 수로 나눔
     */
    @Query(value = "SELECT AVG(member_average_score) AS average_score_per_section " +
            "FROM (SELECT c.member_id, AVG(c.score) AS member_average_score " +
            "      FROM Curriculum c " +
            "      JOIN Section s ON c.section_id = s.id " +
            "      JOIN Lecture l ON s.lecture_id = l.id " +
            "      WHERE s.lecture_id =:lectureId " +
            "      GROUP BY c.member_id) As sub", nativeQuery = true)
    Optional<Float> findByAVGLectureScore(@Param("lectureId")int lectureId);


    @Query("SELECT c FROM Curriculum c WHERE c.edit = :edit")
    CurriculumService findByEditId(@Param("edit")int edit);

    @Query(value = "SELECT * " +
            "FROM curriculum c " +
            "JOIN section s ON c.section_id = s.id " +
            "JOIN lecture l ON s.lecture_id = l.id " +
            "JOIN pay_lecture pl ON pl.lecture_id = l.id " +
            "JOIN payment p ON pl.payment_id = p.id " +
            "JOIN member m ON c.member_id = m.id " +
            "WHERE p.state IN(0,2) AND m.id =:memberId", nativeQuery = true)
    List<Curriculum> findByDeleteMemberId(@Param("memberId") int memberId);


    // Lecture ID를 기반으로 최대 Section ID 조회
    @Query("SELECT MAX(c.section.id) FROM Curriculum c WHERE c.section.lecture.id = :lectureId")
    Integer findMaxSectionIdByLectureId(@Param("lectureId") int lectureId);

    @Query(value = "SELECT * FROM curriculum c JOIN member m ON c.member_id = m.id JOIN section s ON c.section_id = s.id JOIN lecture l ON s.lecture_id = l.id WHERE m.id = :memberId AND s.id = :sectionId", nativeQuery = true)
    List<Curriculum> findByMemberSectionId(@Param("memberId") int memberId,@Param("sectionId") int sectionId);

    @Query(value = "SELECT distinct c.* FROM curriculum c " +
            "JOIN section s ON c.section_id = s.id " +
            "JOIN lecture l ON s.lecture_id = l.id " +
            "JOIN video v ON s.id = v.section_id " +
             "LEFT JOIN video_progress vp ON v.id = vp.video_id "+
            "WHERE l.id =:lectureId ",nativeQuery = true)
    List<Curriculum> findLatestVideoProgressByLecture(@Param("lectureId") int lectureId);




}
