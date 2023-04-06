package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private int videoId; // 사용자에게 고유하게 부여되는 값

    private SectionDTO sectionDTO;

    private String name; // 비디오 이름

    private LocalTime playTime; // 총 재생시간

    private String description; // 영상 설명

    private LocalDateTime uploadDate; // 영상 업로드 날짜

    private int preview; // 0:미리보기불가, 1:미리보기허용

    private String path; // 영상이 저장된 위치

    private String thumb; // 썸네일이 저장된 위치

    private List<MultipartFile> thumbRequest;

    private String thumbResponse;

    private String sequence; // 영상 순서. 0부터 시작
}
