package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Integer> {

    @Query("SELECT vp FROM VideoProgress vp JOIN vp.lectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m WHERE m.id = :memberId AND vp.video.id = :videoId AND p.state = 1")
    Optional<VideoProgress> findByMemberIdAndVideoId(@Param("memberId") int memberId, @Param("videoId") int videoId);


    // video 데이터포함, videoPrgoress에 video에대한 vp정보가 없으면 null
/*    @Query("SELECT vp " +
            "FROM Video v " +
            "LEFT JOIN VideoProgress vp ON v.id = vp.video.id " +
            "JOIN v.section s " +
            "JOIN s.lecture l " +
            "WHERE s.id = :sectionId " +
            "AND (vp.lectureProgress.payLecture.payment.member.id = :memberId)")*/
    @Query(value = "SELECT vp.* " +
            "FROM video v " +
            "LEFT JOIN video_progress vp ON v.id = vp.video_id " +
            "JOIN section s ON v.section_id = s.id " +
            "JOIN lecture l ON s.lecture_id = l.id " +
            "JOIN lecture_progress lp ON vp.lecture_progress_id = lp.id " +
            "JOIN pay_lecture pl ON lp.pay_lecture_id = pl.id " +
            "JOIN payment p ON pl.payment_id = p.id " +
            "JOIN member m ON p.member_id = m.id " +
            "WHERE s.id = :sectionId AND m.id = :memberId", nativeQuery = true)
    List<VideoProgress> findVideoByLectureIdAndMemberId(@Param("sectionId") int sectionId, @Param("memberId") Integer memberId);



    @Query("SELECT vp FROM VideoProgress vp " +
            "JOIN vp.lectureProgress lp " +
            "JOIN lp.payLecture pl " +
            "JOIN pl.payment p " +
            "JOIN p.member m " +
            "WHERE m.id = :memberId ")
    List<VideoProgress> findByMemberId(@Param("memberId") int memberId);

    // memeberId와 lectureId에 맞는 videoProgress 개수 가져오기
    @Query(value = "SELECT COUNT(vp.id) " +
            "FROM video_progress vp, lecture_progress lp, pay_lecture pl, payment p " +
            "WHERE vp.lecture_progress_id = lp.id AND lp.pay_lecture_id = pl.id " +
            "And pl.payment_id = p.id AND p.member_id = :memberId And pl.lecture_id = :lectureId And vp.state = :state", nativeQuery = true)
    long countByMemberIdAndLectureId(@Param("memberId") int memberId, @Param("lectureId") int lectureId, @Param("state") int state);


    @Query(value =
            "SELECT vp.* FROM video_progress vp " +
            "RIGHT JOIN video v ON vp.video_id = v.id " +
            "JOIN section s ON v.section_id = s.id " +
            "JOIN curriculum c ON c.section_id = s.id " +
            "WHERE s.id =:sectionId", nativeQuery = true)
    List<VideoProgress> findBySectionsId(@Param("sectionId") int sectionId);


    /*
    * Member, Video에 맞는 Entity 가져오기
    */
    @Query("SELECT vp FROM VideoProgress vp WHERE vp.video = :video AND vp.lectureProgress.payLecture.payment.member = :member")
    VideoProgress findByMemberAndVideo(@Param("member") Member member, @Param("video") Video video);

    /*
    * Video, Member에 맞는 videoProgress 가져오기
    */
    VideoProgress findByVideoAndLectureProgress(Video video, LectureProgress lectureProgress);

    @Query(value = "SELECT * " +
            "FROM video_progress vp " +
            "JOIN lecture_progress lp on vp.lecture_progress_id = lp.id " +
            "JOIN pay_lecture pl ON lp.pay_lecture_id = pl.id " +
            "JOIN payment p ON pl.payment_id = p.id " +
            "JOIN member m on p.member_id = m.id " +
            "WHERE p.state IN(0,2) AND m.id =:memberId", nativeQuery = true)
    List<VideoProgress> findByDeleteVideoProgressId(@Param("memberId") int memberId);


}

