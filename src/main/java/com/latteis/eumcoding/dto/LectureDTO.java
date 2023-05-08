package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO {

    @Getter
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
        private LocalDateTime createdDay; // 강좌생성일
        private String thumb; // 강좌 썸네일
        private int state; // 0:등록대기중, 1:등록
        private String badge; // 프로필 이미지가 들어있는 경로
    }

    // 강의 생성 요청 DTO
    @Getter
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
