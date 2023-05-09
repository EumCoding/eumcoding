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
    private final LectureProgressRepository lectureProgressRepository;
    private final SectionRepository sectionRepository;



    public List<MyPlanListDTO> getMyPlanList(int memberId) {
        List<Curriculum> curriculums = curriculumRepository.findByMemberId(memberId);
        List<MyPlanListDTO> myPlanList = new ArrayList<>();

        for (Curriculum curriculum : curriculums) {
            List<SectionDTO.SectionDTOList> sectionDTOLists = new ArrayList<>();

            List<Section> lectureSections = sectionRepository.findByLectureId(curriculum.getSection().getLecture().getId());

            for (Section lectureSection : lectureSections) {
                long totalVideos = videoRepository.countBySectionId(lectureSection.getId());
                int completedVideos = 0;

                List<Video> sectionVideos = videoRepository.findBySectionId(lectureSection.getId());

                for (Video video : sectionVideos) {
                    Optional<VideoProgress> videoProgress = videoProgressRepository.findByMemberIdAndVideoId(memberId, video.getId());

                    if (videoProgress.isPresent()) {
                        updateVideoProgressState(videoProgress.get(), video);
                        if (videoProgress.get().getState() == 3) {
                            completedVideos++;
                        }
                    }
                }

                int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideos * 100 / totalVideos);

                SectionDTO.SectionDTOList sectionDTO = SectionDTO.SectionDTOList.builder()
                        .sectionId(lectureSection.getId())
                        .lectureId(lectureSection.getLecture().getId())
                        .lectureName(lectureSection.getLecture().getName())
                        .sectionName(lectureSection.getName())
                        .progress(progress)
                        .build();

                sectionDTOLists.add(sectionDTO);
            }

            MyPlanListDTO myPlanListDTO = MyPlanListDTO.builder()
                    .curriculumId(curriculum.getId())
                    .date(curriculum.getCreateDate())
                    .videoProgress(calculateOverallProgress(sectionDTOLists))
                    .sectionDTOList(sectionDTOLists)
                    .build();

            myPlanList.add(myPlanListDTO);
        }

        return myPlanList;
    }

    private int calculateOverallProgress(List<SectionDTO.SectionDTOList> sectionDTOList) {
        int totalProgress = 0;
        for (SectionDTO.SectionDTOList section : sectionDTOList) {
            totalProgress += section.getProgress();
        }

        return sectionDTOList.size() == 0 ? 0 : Math.round((float) totalProgress / sectionDTOList.size());
    }



    private int isOvers(LocalDateTime endDay) {
        if (endDay.isBefore(LocalDateTime.now())) {
            return 1;
        } else {
            return 0;
        }
    }




    //video_progress에 state 상태를 해당 조건에 맞게 변경
    //lastView를 기준으로 영상의 10%들으면 수강시작
    //50% 수강중, 100%수강 완료 0->1->2->3 변경
    private void updateVideoProgressState(VideoProgress videoProgress, Video video) {

        if (videoProgress.getLastView() == null) {
            videoProgress.setState(0); // 수강 전
            return;
        }

        double playedPercentage = (double) videoProgress.getLastView().toMillis() / video.getPlayTime().toMillis() * 100;

        if (playedPercentage >= 100) {
            System.out.println("3");
            videoProgress.setState(3); // 수강 종료
        } else if (playedPercentage >= 50) {
            System.out.println("2");
            videoProgress.setState(2); // 수강 중
        } else if (playedPercentage >= 10) {
            System.out.println("1");
            videoProgress.setState(1); // 수강 시작
        } else {
            videoProgress.setState(0); // 수강 전
        }
        //videoProgressRepository.save(videoProgress);
    }

}