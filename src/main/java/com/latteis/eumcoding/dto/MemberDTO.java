package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private int id; // 사용자에게 고유하게 부여되는 값

    private String password;

    private String name;

    private String nickname;

    private String tel;

    private LocalDate birthDay;

    private LocalDateTime joinDay;

    private int gender;

    private String address;

    private String profile; // 프로필 이미지가 들어있는 경로

    private int role; // 0:학생, 1:선생, 2:관리자

    private List<MultipartFile> profileImgRequest;

    private String profileImg;

    private List<BadgeDTO> badgeDTOList;
}
