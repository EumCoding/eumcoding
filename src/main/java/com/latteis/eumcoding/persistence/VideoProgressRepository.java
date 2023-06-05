package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Payment;
import com.latteis.eumcoding.model.VideoProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Integer> {

/*    @Query("SELECT vp FROM VideoProgress vp WHERE vp.lectureProgress.payLecture.payment.member.id = :memberId AND vp.video.id = :videoId")
    VideoProgress findByMemberIdAndVideoId(@Param("memberId") int memberId, @Param("videoId") int videoId);*/

    @Query("SELECT vp FROM VideoProgress vp JOIN vp.lectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m WHERE m.id = :memberId AND vp.video.id = :videoId AND p.state = 1")
    Optional<VideoProgress> findByMemberIdAndVideoId(@Param("memberId") int memberId, @Param("videoId") int videoId);

    @Query("SELECT vp FROM VideoProgress vp JOIN vp.lectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m WHERE m.id = :memberId")
    List<VideoProgress> findByMemberId(@Param("memberId") int memberId);

    // memeberId와 lectureId에 맞는 videoProgress 개수 가져오기
    @Query(value = "SELECT COUNT(vp.id) " +
            "FROM video_progress vp, lecture_progress lp, pay_lecture pl, payment p " +
            "WHERE vp.lecture_progress_id = lp.id AND lp.pay_lecture_id = pl.id " +
            "And pl.payment_id = p.id AND p.member_id = :memberId And pl.lecture_id = :lectureId And vp.state = :state", nativeQuery = true)
    long countByMemberIdAndLectureId(@Param("memberId") int memberId, @Param("lectureId") int lectureId, @Param("state") int state);


    @Query("SELECT vp FROM VideoProgress vp WHERE vp.lectureProgress.id = :lectureProgressId")
    List<VideoProgress> findByLectureProgressId(@Param("lectureProgressId") int lectureProgressId);

    @Query("SELECT p FROM Payment p " +
            "JOIN Member m ON p.member.id = m.id " +
            "JOIN PayLecture pl ON p.id = pl.payment.id " +
            "JOIN LectureProgress lp ON pl.id = lp.payLecture.id " +
            "JOIN VideoProgress vp ON lp.id = vp.lectureProgress.id " +
            "WHERE m.id = :memberId AND m.state = 1 " +
            "ORDER BY vp.lastView ASC")
    Page<Payment> findAllByMemberIdAndStateOrderByLastView(@Param("memberId") int memberId, Pageable pageable);


}

