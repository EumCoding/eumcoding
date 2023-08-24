package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kakaoinfo")
public class KakaoInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @Column(name = "email")
    private String email;

    @Column(name = "kakao_email")
    private String kakaoEmail;

    @Column(name = "kakao_user_id")
    private int kakaoUserId;

    @Column(name = "kakao_access_token")
    private String kakaoAccessToken;

    @Column(name = "join_day")
    private LocalDateTime joinDay;


}
