package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.model.Member;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @Data
    @NoArgsConstructor
    @ApiModel(value = "회원 ID 요청 DTO")
    public static class IDRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "회원 ID", example = "1")
        private int id;

    }


    public MemberDTO(int id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

    @Getter
    @NoArgsConstructor
    public static class loginDTO{
        private String email;
        private String password;

    }

    @Getter
    @NoArgsConstructor
    public static class UpdateTel{
        private String tel;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdatePw{
        private String curPw;
        private String chgPw;

    }

    @Getter
    @NoArgsConstructor
    public static class UpdateAddress{
        private String address;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateNickName{
        private String nickname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateProfile{
        private String profile; // 프로필 이미지가 들어있는 경로

        private List<MultipartFile> profileImgRequest;
        public boolean checkProfileImgRequestNull() {
            return this.profileImgRequest != null;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CheckEmail{
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class ViewConfirmEmail{
        private int id;
        private String email;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Sign{


        private String password;

        private String name;

        private String nickname;

        private String email;

        private String tel;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate birthDay;


        private int gender;

        private String address;

        private int role; // 0:학생, 1:선생, 2:관리자

        private String profile; // 프로필 이미지가 들어있는 경로


        private List<MultipartFile> profileImgRequest;

        public boolean checkProfileImgRequestNull() {
            return this.profileImgRequest != null;
        }


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentProfileDTO{
        private int memberId;
        private String nickname;
        private String profileImage;
        
        //여기서부턴강좌
        private int grade;
        //badge 배지 경로, 수료한 해당 강의
        private List<Badge> badge;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Badge {
        private String url;
        private int lectureId;
    }




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
        this.profile = member.getProfile();
        this.role = member.getRole();
        this.state = member.getState();


    }

    // 파일 null 체크
    public boolean checkProfileImgRequestNull() {
        return this.profileImgRequest != null;
    }

    public static class MemberRole {

        // 학생
        public static final int STUDENT = 0;

        // 강사
        public static final int TEACHER = 1;

        // 관리자
        public static final int ADMIN = 2;


    }

}
