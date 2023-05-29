package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestMultipleList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoTestMultipleListRepository extends JpaRepository<VideoTestMultipleList, Integer> {

    Long countByVideoTestId(int videoTestId);

    VideoTestMultipleList findById(int id);

    List<VideoTestMultipleList> findAllByVideoTestOrderBySequence(VideoTest videoTest);

}
