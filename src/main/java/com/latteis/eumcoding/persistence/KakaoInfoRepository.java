package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.dto.TeacherProfileDTO;
import com.latteis.eumcoding.model.KakaoInfo;
import com.latteis.eumcoding.model.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KakaoInfoRepository extends JpaRepository<KakaoInfo, Integer> {


    @Query(value = "SELECT * FROM KakaoInfo WHERE kakao_email = :kakaoEmail", nativeQuery = true)
    KakaoInfo findByKakaoEmail(@Param("kakaoEmail") String kakaoEmail);

    @Query(value = "SELECT * FROM KakaoInfo WHERE kakao_user_id = :kakaoUserId", nativeQuery = true)
    KakaoInfo findByKakaoUserId(@Param("kakaoUserId") String kakaoUserId);

    @Query(value = "SELECT * FROM KakaoInfo WHERE access_token_expires = :accessTokenExpires", nativeQuery = true)
    KakaoInfo findByRefreshToken(@Param("accessTokenExpires")String accessTokenExpires);

    @Query(value = "SELECT * FROM KakaoInfo WHERE kakao_access_token = :kakaoAccessToken", nativeQuery = true)
    KakaoInfo findByKakaoAccessToken(@Param("kakaoAccessToken")String kakaoAccessToken);
}
