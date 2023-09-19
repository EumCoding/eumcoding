package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherMyPageDTO {
    private int memberId;
    private String profileImgRequest; //백->프론트 파일 타입
    private String profileImgResponse;// 프톤르-> 백 스트링 타입
    private String nickname;
    private String address;
    private String tel;
    private LocalDateTime joinDay;
    private LocalDate birthDay;
    private String name;
    private String email;

    private String resume; //이력서 경로
    private String portfolioPath; //포트폴리오 경로
    //강좌별 학생들의평균진도율 + 평균성적 + 강좌별 판매 정렬 + 판매률(횟수)+평점
    private List<LectureDTO.coursesDTO> courses;
    //내 강좌 질문 현황
    //private List<QuestionDTO.countQuestionDTO> questions;

}
