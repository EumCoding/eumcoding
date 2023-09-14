package com.latteis.eumcoding.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class KakaoInfoDTO {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresIn;
}
