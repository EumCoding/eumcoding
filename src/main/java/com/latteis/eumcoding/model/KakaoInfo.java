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

    @Column(name = "agree")
    private int agree;

    @Column(name = "kakao_email")
    private String kakaoEmail;

    @Column(name = "kakao_user_id")
    private String kakaoUserId;

    @Column(name = "kakao_access_token")
    private String kakaoAccessToken;

    @Column(name = "join_day")
    private LocalDateTime joinDay;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "access_token_expires")
    private LocalDateTime accessTokenExpires;


}
