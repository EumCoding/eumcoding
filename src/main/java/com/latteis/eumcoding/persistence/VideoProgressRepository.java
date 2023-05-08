package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, String> {

    @Query("SELECT vp FROM VideoProgress vp WHERE vp.lectureProgress.payLecture.payment.member.id = :memberId AND vp.video.id = :videoId")
    VideoProgress findByMemberIdAndVideoId(@Param("memberId") int memberId, @Param("videoId") int videoId);


}

