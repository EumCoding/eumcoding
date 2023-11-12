package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.*;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


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
/*
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
                int over = checkOver(lectureSection.getId(), memberId,videoProgresses);

                int[] videoCounts = countTotalAndCompletedVideos(memberId,lectureSection);
                int totalVideoss = videoCounts[0];
                int completedVideoss = videoCounts[1];
                int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideoss * 100 / totalVideoss);
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
*/


    //기존 getMyPlanList랑 다른점은 해당 커리큘럼에 있는 섹션을 기한내에 다들었는지
    //혹은 이번에 들어야하는 섹션인지 표시해주는게 들어가있음
    public List<MyPlanInfoDTO> getMyPlanInfo(int memberId) {
        boolean finish = true;  // 이전 섹션이 완료되었는지 표시
        boolean message = false;  // 메시지가 설정되었는지 확인

        //특정 회원이 가지고 있는 모든 커리큘럼을 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberId(memberId);
        //사용자의 학습 진행 상황을 나타내는 DTO
        List<MyPlanInfoDTO> myPlanInfoList = new ArrayList<>();

        for (Curriculum curriculum : curriculums) {
            List<SectionDTO.SectionDTOMessageList> sectionDTOMessageLists = new ArrayList<>();

            //현재 커리큘럼의 id에 대응하는 섹션을 가져옴
            List<Section> lectureSections = sectionRepository.findByCurriculumId(curriculum.getId());

            //커리큘럼에 statDay + timeTaken 값이 필요

            //현재 커리큘럼에 포함된 강의의 모든 섹션을 조회
            for (Section lectureSection : lectureSections) {

                //각 섹션에 포함된 전체 비디오의 수를 조회
                long totalVideos = videoRepository.countBySectionId(lectureSection.getId());

                List<VideoProgress> videoProgresses = videoProgressRepository.findByMemberId(memberId);
                int over = checkOver(lectureSection.getId(), memberId,videoProgresses);

                // 전체 비디오 수와 완료된 비디오 수 계산
                int[] videoCounts = countTotalAndCompletedVideos(memberId,lectureSection);
                int totalVideoss = videoCounts[0];
                int completedVideoss = videoCounts[1];
                int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideoss * 100 / totalVideoss);

                String overMessage = "";
                String checkMessage = "";
                String finishMessage = "";

                //over가 1일경우
                //해당 커리큘럼에있는 섹션을 기한내에 다 듣지 못함 -> 기한을 넘겼습니다. 표시
                //이번에 들어야할 섹션입니다.는 주차별로 표시를 할것
                if(progress == 100 && over == 1){
                    overMessage = lectureSection.getName() + " 기한을 넘겼습니다.";

                    // 현재 섹션의 모든 비디오 체크 후 해당 섹션에 연결된 강의의 진행 상태 업데이트
                    updateLectureProgressState(memberId, lectureSection.getLecture().getId());
                    finish = false;
                }else if(over == 0 && progress != 100 && !message){
                    //전 섹션을 다듣지못하면 그다음 섹션의 영상은 볼 수 없으니
                    //전 섹션을 다들었을 경우 그다음 섹션에는 해당 메시지가 표시될수 있게 하면됨
                    checkMessage = lectureSection.getName() + " 이번에 들어야할 섹션입니다.";
                    finish = false;
                    message = true;

                }else if(progress == 100 && over == 0) {
                    finishMessage = lectureSection.getName() + " 해당 섹션의 비디오들을 다 수강하셨습니다.";
                    // 현재 섹션의 모든 비디오 체크 후 해당 섹션에 연결된 강의의 진행 상태 업데이트
                    updateLectureProgressState(memberId, lectureSection.getLecture().getId());
                    finish = true;
                }

                SectionDTO.SectionDTOMessageList sectionDTO = SectionDTO.SectionDTOMessageList.builder()
                        .sectionId(lectureSection.getId())
                        .lectureId(lectureSection.getLecture().getId())
                        .lectureName(lectureSection.getLecture().getName())
                        .sectionName(lectureSection.getName())
                        .progress(progress)
                        .over(over)
                        .overMessage(overMessage)
                        .checkMessage(checkMessage)
                        .finishMessage(finishMessage)
                        .build();

                sectionDTOMessageLists.add(sectionDTO);
            }


            MyPlanInfoDTO myPlanInfoDTO = MyPlanInfoDTO.builder()
                    .curriculumId(curriculum.getId())
                    .date(curriculum.getCreateDate())
                    .sectionDTOList(sectionDTOMessageLists)
                    .build();

            myPlanInfoList.add(myPlanInfoDTO);
        }

        return myPlanInfoList;
    }


    /**
     * vpr.get(0).getEndDay로 비교하는 이유는, 쿼리보면 알겠지만, desc해서 가장 마지막에 들을 비디오의 endDay를 가져오기때문에 get(0)씀.
     * 어차피 비디오 수강은 순차적으로 1->2->3..들어야하기 때문에 (건너뛰기 불가능 1->3 x) 그래서 이렇게함
     */
    //Curriculum에 timeTaken에 설정한 시간안에 VideoProgress에 state가 1이안되면 over는 1:기한넘김
    //videoProgress에 state는 last_View를 가지고 Video에 playTime이랑 일치할 경우 state는 1로 바뀌도록
    //밑에 메서드에 표시해놨음  updateVideoProgressState <- 이부분 주석처리함, 어차피 여기서 할 행위가아니기떄문
    private int checkOver(int sectionId,int memberId, List<VideoProgress> videoProgresses) {

        //section_id로 curriculum찾기
        Curriculum curriculum = curriculumRepository.findBySectionId(sectionId,memberId);
        LocalDateTime newDate = curriculum.getStartDay().plusDays(curriculum.getTimeTaken());

        //curriculum에 section_id에 설정되지 않은 섹션 아이디는 일단은 over 0으로 할거임
        //즉 설정하지 않은 것에 대해선 다 0으로 기간지나지않음으로 처리
        if (curriculum == null) {
            return 0; // Curriculum이 없는 경우 over는 0

        }

        List<VideoProgress> vpr = videoProgressRepository.findVideoProgressEndDay(sectionId,memberId);
        //curriculum timetaken에 설정된 일을 StartDay에 더해서 endDay구할수있음 -> startDay + timeTaken = endDay
        //videoProgress에 해당 section에 속한 video들의 videoProgress에 endDay가 curriculum에 startDay + timetaken > endDay 이면 over 0 반대면 1
        //여기서 vpr에 endDay는 lastView 즉 해당 강의를 다들었을 경우 endDay

        //videoProgresses테이블에 video에대한 정보가없을경우(아직 안들었을경우)
        //단, newDate의 기한을 넘기지 않아야함
        if (!vpr.isEmpty()) {
            if (newDate.isAfter(vpr.get(0).getEndDay())) {
                return 0; // 마지막 비디오의 진행 상태가 기한 내에 완료됨
            } else if (newDate.isBefore(vpr.get(0).getEndDay())) {
                return 1; // 마지막 비디오의 진행 상태가 기한을 넘김
            }
        } else {
            // vpr 리스트가 비어 있으므로, 아직 어떤 비디오도 완료되지 않음
            // 이 경우에 newDate와 현재 날짜를 비교
            if (newDate.isAfter(LocalDateTime.now())) {
                return 0; // 아직 기한이 남음
            } else {
                return 1; // 기한을 넘김
            }
        }

        // Curriculum에 연결된 모든 비디오를 가져옴
        List<Video> videos = videoRepository.findByCurriculum(curriculum);

        boolean allVideosCompleted = true; //섹션에 있는 비디오가 완료되었는지 확인

        for (Video video : videos) {
            boolean videoCompleted = false; //각 비디오가 완료되엇는지 확인
            for (VideoProgress vp : vpr) {
                if (vp.getVideo().getId() == video.getId() && vp.getState() == 1) {
                    videoCompleted = true; //비디오 완료
                    allVideosCompleted = true;
                    break;
                }
                else if (!videoCompleted) {
                    allVideosCompleted = false;
                }
            }
        }

        //섹션에 포함된 모든 비디오가 다 state가 1이아니면 over 1
        //1:실패 0:성공
        if(!allVideosCompleted || newDate.isBefore(vpr.get(0).getEndDay()))
        {
            return 1;
        }
        // 그 외의 경우에는 over는 0
        else {
            return 0;
        }

    }

    //video_progress에 state 상태를 해당 조건에 맞게 변경
    //50% 수강중, 100%수강 완료 0->1변경
  /*  private void updateVideoProgressState(VideoProgress videoProgress, Video video) {
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
    }*/

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


    //강좌에 해당하는 모든 비디오 다들으면 lecture_progress 에 state 가 0-> 1로 업데이트 아니면 0
    public void updateLectureProgressState(int memberId, int lectureId) {
        // Member와 Lecture 객체를 가져옵니다.
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("없는 회원입니다."));
        Lecture lecture = lectureRepository.findById(lectureId);
        if(lecture == null){
            throw new RuntimeException("없는 강의입니다.");
        }

        // 해당 회원의 수강중인 Lecture 에대한  LectureProgress를 가져옴
        List<LectureProgress> lectureProgress = lectureProgressRepository.findByMemberLecture(member, lecture);
        if(lectureProgress.isEmpty()){
            throw new RuntimeException("해당 강의의 진행 상황을 찾을 수 없습니다.");
        }

        // 해당 Lecture에 포함된 모든 Section의 Video들의 진행 상황을 체크
        List<Section> sections = sectionRepository.findByLectureId(lectureId);
        boolean allVideosCompleted = true;
        for (Section section : sections) {
            List<Video> videos = videoRepository.findBySectionId(section.getId());

            // VideoProgress 리스트를 가져옴
            List<VideoProgress> videoProgresses = videoProgressRepository.findVideoByLectureIdAndMemberId(memberId);

            for (Video video : videos) {
                boolean currentVideoCompleted = false;

                for (VideoProgress vp : videoProgresses) {
                    if (vp.getVideo().getId() == video.getId() && vp.getState() == 1) {
                        currentVideoCompleted = true;
                        break;
                    }
                }
                if (!currentVideoCompleted) {
                    allVideosCompleted = false;
                    break;
                }
            }
            if (!allVideosCompleted) break;
        }

        // 모든 Video들이 완료되면 LectureProgress state를 1로 업데이트
        if (allVideosCompleted) {
            for(LectureProgress lp : lectureProgress){
                lp.setState(1);
                lectureProgressRepository.save(lp);
            }
        }
    }


    //DB에서 해당 과목에 속하는 섹션들의 비디오들의 총 갯수(total), 다들은 비디오 갯수(completed)를 구함
    //만약 나중에 이거 문제생기면 위에 코드상에서 계산한걸로 사용
    private int[] countTotalAndCompletedVideos(int memberId, Section s) {
        int totalVideos = 0;
        int completedVideos = 0;

        List<Section> sections = sectionRepository.findBySectionsId(s.getId());

        for (Section section : sections) {
            List<Object[]> videoCounts  = videoRepository.findTotalAndCompletedVideosForSection(memberId, section.getId());
            if (!videoCounts.isEmpty()) {
                Object[] counts = videoCounts.get(0);
                totalVideos += toInt(counts[0]);
                completedVideos += toInt(counts[1]);
            }
        }
        return new int[] { totalVideos, completedVideos };
    }

    //형변환 에러방지
    private int toInt(Object obj) {
        if (obj instanceof BigInteger) {
            return ((BigInteger) obj).intValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + obj.getClass().getName());
        }
    }


}