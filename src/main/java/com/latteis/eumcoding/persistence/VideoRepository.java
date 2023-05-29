package com.latteis.eumcoding.persistence;


import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    Long countBySectionId(int sectionId);

    Video findById(int videoId);

    List<Video> findBySectionId(int sectionId);

    //Video 테이블에서 sectionId 컬럼 이용 ㅡCurriculum과 관련된 Video를 찾기
    //curriculumService에 CheckOver메서드에  video findByCurriculum 메서드에서는
    //Curriculum 엔티티를 입력받아서 해당 Curriculum과 관련된 모든 Video를 반환
    @Query("SELECT v FROM Video v WHERE v.section.id  IN (SELECT c.section.id FROM Curriculum c WHERE c = :curriculum)")
    List<Video> findByCurriculum(@Param("curriculum") Curriculum curriculum);

    // lectureId에 있는 video 개수 가져오기
    @Query(value = "SELECT COUNT(v.id) " +
            "FROM video v, section s, lecture l " +
            "WHERE v.section_id = s.id AND s.lecture_id = l.id " +
            "and l.id = 3", nativeQuery = true)
    long countByLectureId(int lectureId);

    // SectionList에 넣을 VideoList 가져오기
    @Query(value = "SELECT id, name, preview, play_time FROM video WHERE section_id = :sectionId ORDER BY sequence", nativeQuery = true)
    List<Object[]> getSectionList(@Param("sectionId") int sectionId);
}
