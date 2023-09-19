package com.latteis.eumcoding.persistence;


import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Integer> {





    Long countBySectionId(int sectionId);

    Video findById(int videoId);



    //DB에서 각 멤버의 완료된 강좌 갯수 구함
    @Query(value = "SELECT " +
            "count(DISTINCT v.id) AS total, " +
            "count(DISTINCT CASE WHEN p.member_id = :memberId THEN vp.id ELSE NULL END) AS completed " +
            "FROM video v " +
            "LEFT JOIN video_progress vp ON vp.video_id = v.id " +
            "LEFT JOIN lecture_progress lp ON vp.lecture_progress_id = lp.id " +
            "LEFT JOIN pay_lecture pl ON lp.pay_lecture_id = pl.id " +
            "LEFT JOIN payment p ON pl.payment_id = p.id AND p.member_id = :memberId " +
            "WHERE v.section_id = :sectionId",
            nativeQuery = true)
    List<Object[]> findTotalAndCompletedVideosForSection(@Param("memberId") int memberId, @Param("sectionId") int sectionId);



    List<Video> findBySectionId(int sectionId);

    //Video 테이블에서 sectionId 컬럼 이용 ㅡCurriculum과 관련된 Video를 찾기
    //curriculumService에 CheckOver메서드에  video findByCurriculum 메서드에서는
    //Curriculum 엔티티를 입력받아서 해당 Curriculum과 관련된 모든 Video를 반환
    //즉 커리큘럼의 section_id에 해당하는 video를 가져오는것
    @Query("SELECT v FROM Video v WHERE v.section.id  IN (SELECT c.section.id FROM Curriculum c WHERE c = :curriculum)")
    List<Video> findByCurriculum(@Param("curriculum") Curriculum curriculum);

    // lectureId에 있는 video 개수 가져오기
    @Query(value = "SELECT COUNT(v.id) " +
            "FROM video v, section s, lecture l " +
            "WHERE v.section_id = s.id AND s.lecture_id = l.id " +
            "and l.id = 3", nativeQuery = true)
    long countByLectureId(int lectureId);

    // SectionList에 넣을 VideoList 가져오기
    List<Video> findAllBySectionOrderBySequence(Section section);

    /*
     * Video id, Member에 맞는 엔티티 가져오기
     */
    Video findByIdAndSectionLectureMember(int id, Member member);

    /*
     * Section, sequence에 맞는 엔티티 가져오기
     */
    Video findBySectionAndSequence(Section section, int sequence);

    /*
     * 받아온 sequence보다 큰 sequence, Section에 맞는 리스트 가져오기
     */
    List<Video> findAllBySectionAndSequenceGreaterThan(Section section, int sequence);


    /*
     * 학생이 시청한 기록이 있는 동영상만 가져오기
     */
    @Query("SELECT v FROM Video v " +
            "WHERE v.section = :section " +
            "AND " +
            "(SELECT MAX(vp.video.sequence) FROM VideoProgress vp " +
            "WHERE vp.lectureProgress.payLecture.payment.member = :member) >= v.sequence " +
            "ORDER BY v.sequence")
    List<Video> getLectureStudentVideo(@Param("member") Member member, @Param("section") Section section);
}
