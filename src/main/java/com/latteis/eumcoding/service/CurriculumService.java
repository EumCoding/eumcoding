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
//myplan/list, myplan/update
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final VideoRepository videoRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final SectionRepository sectionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;



    //특정 회원의 학습 계획 리스트를 반환 각 커리큘럼의 섹션별로 비디오 진행 상황을 확인하고, 그에 따른 진행률을 계산하여 DTO에 담아 반환
    public List<MyPlanListDTO> getMyPlanList(int memberId) {
        //특정 회원이 가지고 있는 모든 커리큘럼을 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberId(memberId);

        //사용자의 학습 진행 상황을 나타내는 DTO
        List<MyPlanListDTO> myPlanList = new ArrayList<>();

        for (Curriculum curriculum : curriculums) {
            List<SectionDTO.SectionDTOList> sectionDTOLists = new ArrayList<>();

            //현재 커리큘럼의 id에 대응하는 섹션을 가져옴
            List<Section> lectureSections = sectionRepository.findByCurriculumId(curriculum.getId());

            //현재 커리큘럼에 포함된 강의의 모든 섹션을 조회
            for (Section lectureSection : lectureSections) {
                //각 섹션에 포함된 전체 비디오의 수를 조회
                long totalVideos = videoRepository.countBySectionId(lectureSection.getId());
                //완료된 전체 비디오 수 처음엔 0
                int completedVideos = 0;

                //현재 섹션에 포함된 모든 비디오를 조회
                List<Video> sectionVideos = videoRepository.findBySectionId(lectureSection.getId());

                List<VideoProgress> videoProgresses = videoProgressRepository.findByMemberId(memberId);
                int over = CheckOver(lectureSection.getId(), memberId,videoProgresses);

                for (Video video : sectionVideos) {
                    //해당 회원이 해당 비디오 진행상황 조회
                    Optional<VideoProgress> videoProgress = videoProgressRepository.findByMemberIdAndVideoId(memberId, video.getId());
                    boolean isLectureCompleted = true; // 강의를 모두 들었는지 표시하는 변수, 처음에는 true로 설정

                    if (videoProgress.isPresent()) {
                        updateVideoProgressState(videoProgress.get(), video);
                        if (videoProgress.get().getState() == 1) { //0:수강중 1:수강종료
                            completedVideos++; //완강한 비디오 갯수
                        }else{
                            isLectureCompleted = false; // videoProgress의 state가 1이 아니면, 강의를 모두 들지 않았다는 뜻
                        }
                    }
                }

                int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideos * 100 / totalVideos);
                // 현재 섹션의 모든 비디오 체크 후 해당 섹션에 연결된 강의의 진행 상태 업데이트
                updateLectureProgressState(memberId, lectureSection.getLecture().getId());

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


            MyPlanListDTO myPlanListDTO = MyPlanListDTO.builder()
                    .curriculumId(curriculum.getId())
                    .date(curriculum.getCreateDate())
                    //.videoProgress(calculateOverallProgress(sectionDTOLists))
                    .sectionDTOList(sectionDTOLists)
                    .build();

            myPlanList.add(myPlanListDTO);
        }

        return myPlanList;
    }

    //Curriculum에 timeTaken에 설정한 시간안에 VideoProgress에 state가 1이안되면 over는 1
    //videoProgress에 state는 last_View를 가지고 Video에 playTime이랑 일치할 경우 state는 1로 바뀌도록
    //밑에 메서드에 표시해놨음  updateVideoProgressState
    private int CheckOver(int sectionId,int memberId, List<VideoProgress> videoProgresses) {
        //section_id로 curriculum찾기
        Curriculum curriculum = curriculumRepository.findBySectionId(sectionId,memberId);
        //curriculum에 section_id에 설정되지 않은 섹션 아이디는 일단은 over 0으로 할거임
        //즉 설정하지 않은 것에 대해선 다 0으로 기간지나지않음으로 처리
        if (curriculum == null) {
            return 0; // Curriculum이 없는 경우 over는 0
        }

        // Curriculum에 연결된 모든 비디오를 가져옴
        List<Video> videos = videoRepository.findByCurriculum(curriculum);

        int totalViewTime = 0; //전체 시청 시간 저장
        boolean allVideosCompleted = true; //섹션에 있는 비디오가 완료되었는지 확인

        for (Video video : videos) {
            boolean videoCompleted = false; //각 비디오가 완료되엇는지 확인
            for (VideoProgress vp : videoProgresses) {
                if (vp.getVideo().getId() == video.getId() && vp.getState() == 1) {
                    totalViewTime += video.getPlayTime().toSecondOfDay() / 60; //전체 시청시간에 비디오 재생 시간더하기
                    videoCompleted = true; //비디오 완료
                    break;
                }
            }
            if (!videoCompleted) {
                allVideosCompleted = false;
            }
        }

        // Curriculum의 timeTaken을 분으로 변환 (1일 = 24시간 = 1440분)
        int curriculumTimeTakenInMinutes = curriculum.getTimeTaken() * 24 * 60;

        //섹션에 포함된 모든 비디오가 다 state가 1이아니면 over 1
        //1:실패 0:성공
        if(!allVideosCompleted)
        {
            return 1;
        }
        // 모든 비디오가 완전히 시청되었지만, 전체 시청 시간이 Curriculum의 timeTaken을 초과하면 over는 1
        if (totalViewTime > curriculumTimeTakenInMinutes) {
            return 1;
        }
        // 그 외의 경우에는 over는 0
        else {
            return 0;
        }
    }


    //video_progress에 state 상태를 해당 조건에 맞게 변경
    //lastView를 기준으로 영상의 10%들으면 수강시작
    //50% 수강중, 100%수강 완료 0->1변경
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

    //내 커리큘럼 timetaken 수정하는 메서드
    public Curriculum updateTimeTaken(int memberId, int curriculumId, int newTimeTaken) {

        //커리큘럼 id로 커리큘럼 찾기
        Curriculum curriculum = curriculumRepository.findByCurriculumId(memberId,curriculumId)
                .orElseThrow(() -> new RuntimeException("회원님의 커리큘럼이 없거나, 타 계정 커리큘럼에 접근해 권한이없습니다.."));

        if (curriculum.getEdit() == 1) {
            //timetaken업데이트
            curriculum.setTimeTaken(newTimeTaken);
            //변경사항 저장하고 업데이트된 커리큘럼 반환
            return curriculumRepository.save(curriculum);
        } else{
            throw new RuntimeException("커리큘럼을 수정할 수 없습니다.");
        }
    }


    public void updateLectureProgressState(int memberId, int lectureId) {
        // Member와 Lecture 객체를 가져옵니다.
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("없는 회원입니다."));
        Lecture lecture = lectureRepository.findById(lectureId);

        // 해당 회원의 해당 Lecture의 LectureProgress를 가져옴
        LectureProgress lectureProgress = lectureProgressRepository.findByMemberAndLecture(member, lecture);

        // 해당 Lecture에 포함된 모든 Section의 Video들의 진행 상황을 체크
        List<Section> sections = sectionRepository.findByLectureId(lectureId);
        boolean allVideosCompleted = true;
        for (Section section : sections) {
            List<Video> videos = videoRepository.findBySectionId(section.getId());
            for (Video video : videos) {
                Optional<VideoProgress> videoProgress = videoProgressRepository.findByMemberIdAndVideoId(memberId, video.getId());
                if (videoProgress.get().getState() != 1) {
                    allVideosCompleted = false;
                    break;
                }
            }
            if (!allVideosCompleted) break;
        }

        // 모든 Video들이 완료되면 LectureProgress state를 1로 업데이트
        if (allVideosCompleted) {
            lectureProgress.setState(1);
            lectureProgressRepository.save(lectureProgress);
        }
    }
}