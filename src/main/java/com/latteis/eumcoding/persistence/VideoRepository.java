package com.latteis.eumcoding.persistence;


import com.latteis.eumcoding.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    Long countBySectionId(int sectionId);

    List<Video> findBySectionId(int sectionId);

    List<Video> findBySection(Section section);

}
