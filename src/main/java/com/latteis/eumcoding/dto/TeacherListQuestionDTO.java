package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class TeacherListQuestionDTO {
    private long count;
    private List<TeacherListQuestionDTO.StudentQuestionListDTO> teacherMyReviewList;


    //강사입장에서, 자신한테 달린 질문들 모음
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentQuestionListDTO{
        private String nickname;
        private int qnaId;
        private String title;
        private int answer;// 0:안달림, 1:달림
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime date;
        private int lectureId;
        private String lectureName;
        private String lectureThumb;
    }

}
