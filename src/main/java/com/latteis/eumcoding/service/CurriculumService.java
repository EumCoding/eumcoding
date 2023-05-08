package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.*;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final VideoRepository videoRepository;



    public List<MyPlanListDTO> getMyPlanList(int memberId) {
        List<Curriculum> curriculums = curriculumRepository.findByMemeberId(memberId);
        List<MyPlanListDTO> myPlanList = new ArrayList<>();

        for (Curriculum curriculum : curriculums) {
            List<SectionDTO> sectionDTOList = new ArrayList<>(); //plan

            Section section = curriculum.getSection();
            List<Video> videos = videoRepository.findBySectionId(section.getId());
            int totalVideos = videos.size();
            int completedVideos = 0;

            for (Video video : videos) {
                VideoProgress videoProgress = videoProgressRepository.findByMemberIdAndVideoId(memberId, video.getId());

                if (videoProgress != null) {
                    updateVideoProgressState(videoProgress, video);

                    if (videoProgress.getState() == 3) {
                        completedVideos++;
                    }
                }
            }

            int videoProgress = totalVideos == 0 ? 0 : (completedVideos * 100) / totalVideos;
            //int sectionProgress = totalVideos == 0 ? 0 : (completedVideos * 100) / totalVideos;

            SectionDTO sectionDTO = SectionDTO.builder()
                    .sectionId(section.getId())
                    .lectureId(section.getLecture().getId())
                    .lectureName(section.getLecture().getName())
                    .name(section.getName())
                    // .mainTestId, .videoDTOList
                    .progress(videoProgress)
                    .build();

            sectionDTOList.add(sectionDTO);

            MyPlanListDTO myPlanListDTO = MyPlanListDTO.builder()
                    .curriculumId(curriculum.getId())
                    .date(curriculum.getCreateDate())
                    .over(isOver(curriculum))
                    .videoProgress(videoProgress)
                    //.sectionProgress(sectionProgress)
                    .sectionDTOList(sectionDTOList)
                    .build();

            myPlanList.add(myPlanListDTO);
        }

        return myPlanList;
    }

    private int isOver(Curriculum curriculum) {
        // 강의 해당 일까지 다 수강했는지 확인하는 로직 구현
        // time_taken이 40 이상이면 1, 그렇지 않으면 0 반환
        if (curriculum.getTimeTaken() >= 40) {
            return 1;
        } else {
            return 0;
        }
    }


    //video_progress에 state 상태를 해당 조건에 맞게 변경
    //lastView를 기준으로 영상의 10%들으면 수강시작
    //50% 수강중, 100%수강 완료 0->1->2->3 변경
    private void updateVideoProgressState(VideoProgress videoProgress, Video video) {
        //toSecondOfDay() 메소드는 LocalTime 타입의 객체를 초 단위로 변환
        //video.getPlayTime().toSecondOfDay()는 해당 비디오의 총 재생 시간을 초 단위로 변환
        //재생한 위치의 초 단위 값과 총 재생 시간의 초 단위 값을 나눈 비율
        double playedPercentage = (double) videoProgress.getLastView().toSecondOfDay() / video.getPlayTime().toSecondOfDay() * 100;

        if (playedPercentage >= 100) {
            videoProgress.setState(3); // 수강 종료
        } else if (playedPercentage >= 50) {
            videoProgress.setState(2); // 수강 중
        } else if (playedPercentage >= 10) {
            videoProgress.setState(1); // 수강 시작
        } else {
            videoProgress.setState(0); // 수강 전
        }
        // lsatView 값이 null인 경우 state는 0으로 초기화
        if (videoProgress.getLastView() == null) {
            videoProgress.setState(0);
        }
    }

}