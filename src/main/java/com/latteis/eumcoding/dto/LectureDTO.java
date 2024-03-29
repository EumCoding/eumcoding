package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.model.Lecture;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class LectureDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "강의 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;
    }

    // 프로필 불러오기
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString //안붙이면 lectureDTO.profileDTO 내용이 패키지명으로만 출력됨 com.latteis.eumcoding.dto.LectureDTO$profileDTO@234e6413 이렇게
    public static class profileDTO {
        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;
        private int memberId;
        private String name;
        private String description;
        private String image; // 강좌 설명에 들어가는 이미지
        private int price;
        private int grade; // 학년

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdDay; // 강좌생성일
        private String thumb; // 강좌 썸네일
        private int state; // 0:등록대기중, 1:등록
        private String badge; // 뱃지 경로
    }

    // 강의 생성 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "강의 생성 요청 DTO")
    public static class CreateRequestDTO {

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "강의명", example = "강의명입니다")
        private String name;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "강의 설명", example = "강의 설명입니다")
        private String description;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "가격", example = "10000")
        private int price;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "추천 학년", example = "1")
        private int grade;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "0 : 등록 대기, 1 : 등록", example = "1")
        private int state;

    }

    // 강의 상태 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "강의 상태 수정 요청 DTO")
    public static class StateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "0 : 등록 대기, 1 : 등록", example = "1")
        private int state;

    }

    // 강의명 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "강의명 수정 요청 DTO")
    public static class NameRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "강의명", example = "강의명입니다")
        private String name;

    }

    // 강의 설명 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "강의 설명 수정 요청 DTO")
    public static class DescriptionRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "강의 설명", example = "강의 설명입니다")
        private String description;

    }

    // 강의 학년 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "강의 학년 수정 요청 DTO")
    public static class GradeRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "추천 학년", example = "1")
        private int grade;

    }

    // 강의 가격 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "강의 가격 수정 요청 DTO")
    public static class PriceRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "가격", example = "10000")
        private int price;

    }

    // 내가 등록한 강의 리스트 응답 DTO
    @Data
    @NoArgsConstructor
    @ApiModel(value = "내가 등록한 강의 리스트 응답 DTO")
    public static class MyListResponseDTO {

        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "강의명", example = "강의명입니다")
        private String name;

        @ApiModelProperty(value = "상태", example = "1")
        private int state;

        @ApiModelProperty(value = "등록일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

        @ApiModelProperty(value = "썸네일", example = "~.png")
        private String thumb;

        public MyListResponseDTO(Lecture lecture) {
            this.id = lecture.getId();
            this.name = lecture.getName();
            this.state = lecture.getState();
            this.createdDay = lecture.getCreatedDay();
        }
    }

    // 강의 정보 응답
    @Data
    @NoArgsConstructor
    @ApiModel(value = "강의 정보 응답 DTO")
    public static class ViewResponseDTO {

        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "강사 ID", example = "1")
        private int memberId;

        @ApiModelProperty(value = "강의명", example = "강의명입니다")
        private String name;

        @ApiModelProperty(value = "설명", example = "강의 설명입니다")
        private String description;

        @ApiModelProperty(value = "가격", example = "1")
        private int price;

        @ApiModelProperty(value = "학년", example = "1")
        private int grade;

        @ApiModelProperty(value = "등록일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

        @ApiModelProperty(value = "강의 상태", example = "1")
        private int state;

        @ApiModelProperty(value = "썸네일", example = "~.png")
        private String thumb;

        @ApiModelProperty(value = "강좌설명에 들어가는 이미지", example = "~.png")
        private String image;

        @ApiModelProperty(value = "뱃지", example = "~.png")
        private String badge;

        @ApiModelProperty(value = "강의 평점", example = "4.1")
        private String score;

        @ApiModelProperty(value = "수강 중인 학생 수", example = "1")
        private int totalStudent;

        @ApiModelProperty(value = "리뷰 갯수", example = "1")
        private int totalReview;

        public ViewResponseDTO(Lecture lecture) {
            this.id = lecture.getId();
            this.memberId = lecture.getMember().getId();
            this.name = lecture.getName();
            this.description = lecture.getDescription();
            this.price = lecture.getPrice();
            this.grade = lecture.getGrade();
            this.createdDay = lecture.getCreatedDay();
            this.state = lecture.getState();
        }

    }

    // 통계 메인에 들어가는 강의 리스트 응답 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "통계 메인에 들어가는 강의 리스트 응답 DTO")
    public static class StatsListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "강의명", example = "강의명입니다")
        private String name;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "가격", example = "10000")
        private int price;

        @ApiModelProperty(value = "등록일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime createdDay;

        @ApiModelProperty(value = "강의 썸네일", example = ".jpg")
        private String thumb;

        private int salesVolume;

        public StatsListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.name = (String) objects[1];
            this.price = (int) objects[2];
            this.createdDay = timestampToLocalDateTime((Timestamp) objects[3]);
            this.thumb = objects[4] != null ? (String) objects[4] : null;
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }


    // 내결제 목록 강좌
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "내가 결제한 강의")
    public static class PayLectureIdNameDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;

        @Positive(message = "강의제목작성")
        @ApiModelProperty(value = "강의이름", example = "코딩강의")
        private String name;

        @Positive(message = "강사이름")
        @ApiModelProperty(value = "강사이름", example = "강사이름")
        private String teacherName;

        @Positive(message = "강의이미지")
        @ApiModelProperty(value = "강의이미지", example = "강의이미지")
        private String lectureImg;

        @PositiveOrZero(message = "가격")
        @ApiModelProperty(value = "과목당가격", example = "과목당가격")
        private int price;

        @PositiveOrZero(message = "리뷰작성여부")
        @ApiModelProperty(value = "리뷰작성여부", example = "리뷰작성여부")
        private String reviewStatus;

    }

    // 강사가 올린 강좌 정보모음
    // 강좌별 학생들의평균진도율 + 평균성적 + 강좌별 판매 정렬 + 판매률(횟수)+평점
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "강사가 올린 강좌 정보모음")
    public static class coursesDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        private String lectureName;

/*        private String lectureDescription;

        private int lecturePrice;*/

        private LocalDateTime createdDay;

        private float avgScore;// 평균성적

        private int reviewCount;

        private float avgProgress;

        private int saleCount;//판매율(횟수)

        private float avgReviewScore; // 평균 평점

    }


    private int id; // 사용자에게 고유하게 부여되는 값

    private int memberId;

    private String nickname; // 강사명

    private String name;

    private String description;

    private String image; // 강좌 설명에 들어가는 이미지

    private List<MultipartFile> imageRequest;

    private String imageResponse;

    private int price;

    private int grade; // 학년

    private LocalDateTime createdDay; // 강좌생성일

    private String thumb; // 강좌 썸네일

    private List<MultipartFile> thumbRequest;

    private String thumbResponse;

    private int state; // 0:등록대기중, 1:등록

    private String badge; // 프로필 이미지가 들어있는 경로

    private int sectionCount; // 섹션 갯수

    private int videoCount; // 비디오 갯수
    
    private float score; // 리뷰 평점

    private int reviewer; // 리뷰갯수

    private int progress; // 진행률

    private List<MultipartFile> badgeRequest;

    private String badgeResponse;

    private List<SectionDTO> sectionDTOList;
}
