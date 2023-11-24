package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.dto.TeacherProfileDTO;
import com.latteis.eumcoding.model.KakaoInfo;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.ReplationParent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KakaoInfoRepository extends JpaRepository<KakaoInfo, Integer> {


    @Query(value = "SELECT * FROM KakaoInfo WHERE kakao_email = :kakaoEmail and agree = 1", nativeQuery = true)
    KakaoInfo findByKakaoEmail(@Param("kakaoEmail") String kakaoEmail);



    @Query(value = "SELECT * FROM KakaoInfo WHERE kakao_user_id = :kakaoUserId", nativeQuery = true)
    KakaoInfo findByKakaoUserId(@Param("kakaoUserId") String kakaoUserId);

    @Query(value = "SELECT * FROM KakaoInfo WHERE refresh_token = :refreshToken", nativeQuery = true)
    KakaoInfo findByRefreshToken(@Param("refreshToken") String refreshToken);

    @Query(value = "SELECT * FROM KakaoInfo WHERE kakao_access_token = :kakaoAccessToken", nativeQuery = true)
    KakaoInfo findByKakaoAccessToken(@Param("kakaoAccessToken")String kakaoAccessToken);

    @Query(value = "SELECT * FROM KakaoInfo k where k.member_id = :memberId", nativeQuery = true)
    Optional<KakaoInfo> findByMemberId(@Param("memberId") int memberId);


}
