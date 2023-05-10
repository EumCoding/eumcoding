
package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.MyPlanListDTO;
import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;
import com.latteis.eumcoding.model.VideoProgress;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {



    private final VideoRepository videoRepository;
    private final SectionRepository sectionRepository;

    //addVideo,updateVideo,updateSectionTimeTaken
    //이 세 메서드는 sectionRepository.save(lectureSection); 부분이 반복문 내에 위치해있는데, 이는 매번 DB에 접근 ->느려짐
    //그래서 이를 해당 메서드를 통해 video테이블에 업데이트가 이루어지는 경우나(playtime이 변경) 비디오가 추가될경우에만 불러오게
    //이 외에는 그냥 평상시 정보가지고 비교할 수 있게 하기위함(매번 DB접근안하게 하기 위한 메서드라고 보면 됨)
    public Video addVideo(Video video) {
        Video savedVideo = videoRepository.save(video);
        updateSectionTimeTaken(savedVideo.getSection().getId());
        return savedVideo;
    }


    public Video updateVideo(Video oldVideo, Video newVideo) {
        if (!oldVideo.getPlayTime().equals(newVideo.getPlayTime())) {
            newVideo = videoRepository.save(newVideo);
            updateSectionTimeTaken(newVideo.getSection().getId());
        }
        return newVideo;
    }

    private void updateSectionTimeTaken(int sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section ID: " + sectionId));
        int totalPlayTime = sectionRepository.calculateTotalPlayTime(sectionId);
        section.setTimeTaken(totalPlayTime);
        sectionRepository.save(section);
    }
}
