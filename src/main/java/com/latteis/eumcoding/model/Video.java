package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinTable(
            name = "section_video",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Section section;

    @Column(name = "name")
    private String name; // 비디오 이름

    @Column(name = "play_time")
    private LocalTime playTime; // 총 재생시간

    @Column(name = "description")
    private String description; // 영상 설명

    @Column(name = "upload_date")
    private LocalDateTime uploadDate; // 영상 업로드 날짜

    @Column(name = "preview")
    private int preview; // 0:미리보기불가, 1:미리보기허용

    @Column(name = "path")
    private String path; // 영상이 저장된 위치

    @Column(name = "thumb")
    private String thumb; // 썸네일이 저장된 위치

    @Column(name = "sequence")
    private String sequence; // 영상 순서. 0부터 시작
}
