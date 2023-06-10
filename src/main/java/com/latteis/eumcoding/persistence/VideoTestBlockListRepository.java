package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestBlockList;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoTestBlockListRepository extends JpaRepository<VideoTestBlockList, Integer> {

    /*
    * VideoTest에 맞는 엔티티들 가져오기
    */
    List<VideoTestBlockList> findAllByVideoTest(VideoTest videoTest);
}
