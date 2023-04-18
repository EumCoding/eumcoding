package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO {

    @Getter
    @NoArgsConstructor
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int id;
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

    private LocalDateTime createDay; // 강좌생성일

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
