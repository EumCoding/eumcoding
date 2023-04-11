package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.model.Member;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private String token;

    private int id; // 사용자에게 고유하게 부여되는 값

    private String password;

    private String name;

    private String nickname;

    private String email;

    private String tel;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthDay;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime joinDay;

    private int gender;


    private String address;

    private String profile; // 프로필 이미지가 들어있는 경로

    private int role; // 0:학생, 1:선생, 2:관리자

    private int state;

    private List<MultipartFile> profileImgRequest;

    private String profileImg;

    private List<BadgeDTO> badgeDTOList;


    public MemberDTO(final Member member) {

        this.id = member.getId();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.tel = member.getTel();
        this.birthDay= member.getBirthDay();
        this.joinDay = member.getJoinDay();
        this.gender = member.getGender();
        this.address = member.getAddress();
        this.profileImg = member.getProfile();
        this.role = member.getRole();
        this.state = member.getState();

    }

    // 파일 null 체크
    public boolean checkProfileImgRequestNull() {
        return this.profileImgRequest != null;
    }
}
