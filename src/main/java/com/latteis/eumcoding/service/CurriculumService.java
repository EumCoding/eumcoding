package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.*;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final VideoRepository videoRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final SectionRepository sectionRepository;



    //특정 회원의 학습 계획 리스트를 반환 각 커리큘럼의 섹션별로 비디오 진행 상황을 확인하고, 그에 따른 진행률을 계산하여 DTO에 담아 반환

    public List<MyPlanListDTO> getMyPlanList(int memberId) {
        //특정 회원이 가지고 있는 모든 커리큘럼을 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberId(memberId);

        //사용자의 학습 진행 상황을 나타내는 DTO
        List<MyPlanListDTO> myPlanList = new ArrayList<>();

        for (Curriculum curriculum : curriculums) {
            List<SectionDTO.SectionDTOList> sectionDTOLists = new ArrayList<>();

            //curriculum.getSection().getLecture().getId() 를 사용해 현재 순환하고 있는 커리큘럼 객체의 섹션에 연결된 강의 ID를 findByLectureId 메서드 인자로 제공
            //현재 커리큘럼의 강의에 해당하는 모든 섹션을 가져옴
            List<Section> lectureSections = sectionRepository.findByLectureId(curriculum.getSection().getLecture().getId());

            //현재 커리큘럼에 포함된 강의의 모든 섹션을 조회
            for (Section lectureSection : lectureSections) {
                //각 섹션에 포함된 전체 비디오의 수를 조회
                long totalVideos = videoRepository.countBySectionId(lectureSection.getId());
                //완료된 전체 비디오 수 처음엔 0
                int completedVideos = 0;

                //현재 섹션에 포함된 모든 비디오를 조회
                List<Video> sectionVideos = videoRepository.findBySectionId(lectureSection.getId());

                // 섹션의 totalPlayTime을 계산
                int totalPlayTime = sectionRepository.calculateTotalPlayTime(lectureSection.getId());
                // timeTaken을 업데이트
                lectureSection.setTimeTaken(totalPlayTime);

                //VideoService 만든 이유가 반복문 안에 쓰면 매번 DB에 접근하게 돼서 섹션 수가 많아지면 느려짐
                //VideoService에 add,update안쓰면 밑에 .save써야함
                //sectionRepository.save(lectureSection);

                List<VideoProgress> videoProgresses = videoProgressRepository.findByMemberId(memberId);
                int over = CheckOver(curriculum, lectureSection, videoProgresses);


                for (Video video : sectionVideos) {
                    //해당 회원이 해당 비디오 진행상황 조회
                    Optional<VideoProgress> videoProgress = videoProgressRepository.findByMemberIdAndVideoId(memberId, video.getId());

                    if (videoProgress.isPresent()) {
                        updateVideoProgressState(videoProgress.get(), video);
                        if (videoProgress.get().getState() == 1) { //0:수강중 1:수강종료
                            completedVideos++; //완강한 비디오 갯수
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
                        .over(over)
                        .build();

                sectionDTOLists.add(sectionDTO);
            }

            int timeTaken = curriculum.getTimeTaken();

            List<VideoProgress> videoProgresses = videoProgressRepository.findByMemberId(memberId);
            MyPlanListDTO myPlanListDTO = MyPlanListDTO.builder()
                    .curriculumId(curriculum.getId())
                    .date(curriculum.getCreateDate())
                    //.over(isOver(curriculum, curriculum.getSection(), videoProgresses))
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
            // 섹션에 해당하는 비디오들 진행과정 위에서 progress에 집어넣었음
            //DB에서 분류가 되어잇음 sectionId 3번 video 1,2존재 1번만 들었으면 진행률 50%
            totalProgress += section.getProgress();
        }

        return sectionDTOList.size() == 0 ? 0 : Math.round((float) totalProgress / sectionDTOList.size());
    }



    //Curriculum에 timeTaken에 설정한 시간안에 VideoProgress에 state가 1이안되면 over는 1
    //videoProgress에 state는 last_View를 가지고 Video에 playTime이랑 일치할 경우 state는 1로 바뀌도록
    //밑에 메서드에 표시해놨음  updateVideoProgressState
    private int CheckOver(Curriculum curriculum, Section section, List<VideoProgress> videoProgresses) {
        //섹션의 모든 동영상 가져오기
        List<Video> videos = videoRepository.findBySection(section);

        int totalViewTime = 0;
        boolean allVideosCompleted = true;

        for (Video video : videos) {
            boolean videoCompleted = false;

            for (VideoProgress vp : videoProgresses) {
                //videoProgress가 현재 비디오와 일치하고 상태 1인지 확인
                if (vp.getVideo().getId() == video.getId() && vp.getState() == 1) {
                    // 총 시청 시간에 동영상 재생 시간을 더합니다(분으로 환산).
                    //type 이 LocalTime이라 toMinute못씀
                    totalViewTime += video.getPlayTime().toSecondOfDay() / 60;  //playTime을 분으로 변환
                    videoCompleted = true;
                    break;
                }
            }
            if(!videoCompleted){
                allVideosCompleted = false;
                //section.setTimeTaken(section.getTimeTaken() + (int) Math.ceil((double) video.getPlayTime().toSecondOfDay() / 60)); // 섹션 시간에 비디오 시간을 더함
            }
        }
        //총 시청 시간이 커리큘럼에 설정된 소요 시간보다 큰지 확인 && state는 1이어야함
        if (totalViewTime <= curriculum.getTimeTaken() && allVideosCompleted) {
            return 0;
        } else {
            return 1;
        }
    }

    //video_progress에 state 상태를 해당 조건에 맞게 변경
    //lastView를 기준으로 영상의 10%들으면 수강시작
    //50% 수강중, 100%수강 완료 0->1->2변경
    private void updateVideoProgressState(VideoProgress videoProgress, Video video) {
        //비디오의 전체 재생 시간(playTime)과 마지막으로 본 시간(lastView)을 초 단위로 변환 한다.
        //비디오가 얼마나 재생되었는지 백분율로 계산 한다.
        //계산된 비율(playedPercentage)에 따라 VideoProgress의 상태(state)를 업데이트 한다.
        long playTimeSeconds = ChronoUnit.SECONDS.between(LocalTime.MIDNIGHT, video.getPlayTime());
        long lastViewSeconds = ChronoUnit.SECONDS.between(LocalTime.MIDNIGHT, videoProgress.getLastView());
        double playedPercentage = ((double) lastViewSeconds / playTimeSeconds) * 100;

        //
        if (playedPercentage >= 100) {
            videoProgress.setState(1); // 수강 종료
        } else {
            videoProgress.setState(0); // 수강 전
        }

        videoProgressRepository.save(videoProgress);
    }

}