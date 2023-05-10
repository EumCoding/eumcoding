package com.latteis.eumcoding.persistence;


import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    Long countBySectionId(int sectionId);

    List<Video> findBySectionId(int sectionId);



    //Video 테이블에서 sectionId 컬럼 이용 ㅡCurriculum과 관련된 Video를 찾기
    //curriculumService에 CheckOver메서드에  video findByCurriculum 메서드에서는
    //Curriculum 엔티티를 입력받아서 해당 Curriculum과 관련된 모든 Video를 반환
    @Query("SELECT v FROM Video v WHERE v.section.id  IN (SELECT c.section.id FROM Curriculum c WHERE c = :curriculum)")
    List<Video> findByCurriculum(@Param("curriculum") Curriculum curriculum);

}
