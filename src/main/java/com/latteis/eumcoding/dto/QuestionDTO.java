package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class QuestionDTO {
    private int questionId; // 사용자에게 고유하게 부여되는 값

    private int lectureId;

    private String lectureName;

    private int memberId;

    private String nickname;

    private String title;

    private String content;

    private LocalDateTime updatedDay; // 수정된 날짜

    private LocalDateTime createdDay; // 생성된 날짜

    private String image; // 이미지가 저장된 경로

    private int answerCount; // 답변 갯수

    private List<MultipartFile> imgRequest;

    private String imgResponse;
}
