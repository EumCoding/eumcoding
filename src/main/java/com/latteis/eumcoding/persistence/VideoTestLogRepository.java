package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoTestLogRepository extends JpaRepository<VideoTestLog, Integer> {

    // videotest와 member에 맞는 log가 있는지 검사
    boolean existsByVideoTestAndMember(VideoTest videoTest, Member member);

    VideoTestLog findByVideoTestAndMemberId(VideoTest videoTest, int memberId);

    List<VideoTestLog> findAllByVideoTestAndMember(VideoTest videoTest, Member member);

}
