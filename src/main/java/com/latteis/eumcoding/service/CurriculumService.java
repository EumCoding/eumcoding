package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.*;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
    public List<MyPlanInfoDTO> getMyPlanInfo(int memberId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }

        //특정 회원이 가지고 있는 모든 커리큘럼을 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberIdAndMonthYear(memberId, startDate, endDate);

        // 커리큘럼 정보가 없으면 예외를 던짐
        if (curriculums.isEmpty()) {
            throw new RuntimeException("해당 날짜에는 커리큘럼 정보가 없습니다.");
        }


        //사용자의 학습 진행 상황을 나타내는 DTO
        List<MyPlanInfoDTO> myPlanInfoList = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();

        for (Curriculum curriculum : curriculums) {
            List<SectionDTO.SectionDTOMessageList> sectionDTOMessageLists = new ArrayList<>();

            //현재 커리큘럼의 id에 대응하는 섹션을 가져옴
            List<Section> lectureSections = sectionRepository.findByCurriculumId(curriculum.getId());


            //현재 커리큘럼에 포함된 강의의 모든 섹션을 조회
            for (Section lectureSection : lectureSections) {

                //각 섹션에 포함된 전체 비디오의 수를 조회
                long totalVideos = videoRepository.countBySectionId(lectureSection.getId());

                List<VideoProgress> videoProgresses = videoProgressRepository.findByMemberId(memberId);

                LocalDateTime sectionStartDay = curriculum.getStartDay();

                // 전체 비디오 수와 완료된 비디오 수 계산
                int[] videoCounts = countTotalAndCompletedVideos(memberId, lectureSection);
                int totalVideoss = videoCounts[0];
                int completedVideoss = videoCounts[1];
                int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideoss * 100 / totalVideoss);
                int over = checkOver(memberId, lectureSection.getLecture().getId());

/*

                Curriculum previousSectionCurriculum = findPreviousSection(curriculums, curriculum, memberId, lectureSection.getLecture().getId());
                boolean isPreviousSectionComplete = previousSectionCurriculum == null || checkIfSectionIsComplete(previousSectionCurriculum, memberId);
*/

                String overMessage = "";
                String checkMessage = "";
                String finishMessage = "";

                // 각 섹션별로 메시지 설정 로직
                if (over == 0 && curriculum.getEditDay() != null) {
                    // 기한이 연장되었을 때
                    overMessage = "기한 내에 못들었습니다. " + curriculum.getEditDay().toLocalDate() + "로 연장되었습니다.";
                } else if (over == 0 && progress == 100) {
                    // 기한 내에 섹션을 완료했을 때
                    overMessage = "기한 내에 다 들었습니다.";
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
                    .date(curriculum.getStartDay())
                    .sectionDTOList(sectionDTOMessageLists)
                    .build();

            myPlanInfoList.add(myPlanInfoDTO);
        }

        return myPlanInfoList;
    }

    @Transactional
    public int checkOver(int memberId, int lectureId) {
        // 현재 날짜를 구함
        LocalDate today = LocalDate.now();
        // 회원 ID와 강의 ID에 해당하는 모든 커리큘럼을 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberIdAndLectureId(memberId, lectureId);

        // 각 커리큘럼에 대해 처리
        for (Curriculum curriculum : curriculums) {
            // 해당 섹션의 마감일 계산
            LocalDate sectionEndDay = curriculum.getStartDay().toLocalDate().plusDays(curriculum.getTimeTaken());
            // 해당 섹션의 비디오 진행 상태를 확인
            VideoProgress progress = videoProgressRepository.findVideoProgressEndDay(memberId, curriculum.getSection().getId());
            // 다음 섹션의 시작일 계산
            LocalDate nextSectionStart = calculateNextSectionStartDay(memberId, lectureId, curriculum.getStartDay(), curriculums);
            // 연장된 기한이 있으면 그것을 사용하고, 그렇지 않으면 원래 마감일을 사용
            LocalDate deadline = curriculum.getEditDay() != null ? curriculum.getEditDay().toLocalDate() : sectionEndDay;

            // 기한 내에 완료되었는지 확인
            if (progress.getState() == 1 && (nextSectionStart == null || today.isBefore(nextSectionStart))) {
                // 기한 내에 완료됨
                return 0;
            }
            // 기한을 넘겼는지 확인
            else if (progress.getState() == 0 && (nextSectionStart != null && today.isAfter(nextSectionStart))) {
                // 기한을 넘겼으면 editDay와 뒤따르는 섹션들의 startDay 업데이트
                LocalDateTime newEditDay = LocalDateTime.of(today, LocalTime.now());
                updateEditDay(curriculum.getId(), newEditDay);
                updateStartDaysForFollowingSections(curriculums, curriculum.getSection().getId(), today.plusDays(curriculum.getTimeTaken()).atStartOfDay());
                // 기한 초과
                return 1;
            }
            // 연장된 기한을 넘겼는지 확인
            else if (progress == null || progress.getState() == 0) {
                if (today.isAfter(deadline)) {
                    // 연장된 기한까지 완료하지 못했으면 editDay와 뒤따르는 섹션들의 startDay 업데이트
                    LocalDateTime newEditDay = LocalDateTime.of(today, LocalTime.now());
                    updateEditDay(curriculum.getId(), newEditDay);
                    updateStartDaysForFollowingSections(curriculums, curriculum.getSection().getId(), newEditDay.plusDays(curriculum.getTimeTaken()));
                    // 기한 초과
                    return 1;
                }
            }
            // 비디오를 한 번도 시청하지 않은 경우 처리
            if (progress == null) {
                // 아직 섹션 비디오를 시청하지 않았으므로 진행 중으로 처리
                return 0;
            }
        }
        // 섹션의 강의를 시작하지 않았거나, 기한 내에 완료한 경우
        return 0;
    }

    //다음 섹션 startDay찾는 메서드
    private LocalDate calculateNextSectionStartDay(int memberId, int lectureId, LocalDateTime currentSectionEndDay, List<Curriculum> curriculums) {
        // 현재 섹션의 마감일 이후에 시작하는 섹션 중 가장 빠른 시작일을 가진 섹션을 찾아서 반환
        LocalDate nextSectionStartDay = null;

        for (Curriculum curriculum : curriculums) {
            if (curriculum.getMember().getId() == memberId && curriculum.getSection().getLecture().getId() == lectureId) {
                LocalDate startDay = curriculum.getStartDay().toLocalDate();
                // 현재 섹션의 종료일 이후에 시작하는 섹션 중에서 가장 빠른 시작일
                // nextSectionStartDay가 null이거나 현재 섹션의 startDay가 더 이른 날짜인 경우에만 갱신
                if (startDay.isAfter(currentSectionEndDay.toLocalDate()) && (nextSectionStartDay == null || startDay.isBefore(nextSectionStartDay))) {
                    nextSectionStartDay = startDay;
                }
            }
        }
        return nextSectionStartDay; // 다음 섹션이 있으면 그 섹션의 시작 날짜, 없으면 null 반환
    }


    @Transactional
    public void updateEditDay(int curriculumId, LocalDateTime newEditDateTime) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId).orElse(null);
        if (curriculum != null) {
            curriculum.setEditDay(newEditDateTime);
            curriculumRepository.save(curriculum);
        }
    }

    //섹션별 startDay 업데이트
    @Transactional
    public void updateStartDaysForFollowingSections(List<Curriculum> curriculums, int currentSectionId, LocalDateTime newStartDayForNextSections) {
        // 모든 커리큘럼을 순회하면서 각 섹션의 시작일을 업데이트
        for (Curriculum curriculum : curriculums) {
            if (curriculum.getSection().getId() > currentSectionId) {
                LocalDateTime currentStartDay = curriculum.getStartDay();
                if (newStartDayForNextSections.isAfter(currentStartDay)) {
                    curriculum.setStartDay(newStartDayForNextSections);
                    curriculumRepository.save(curriculum);
                    // 다음 섹션의 시작일을 준비
                    newStartDayForNextSections = newStartDayForNextSections.plusDays(curriculum.getTimeTaken());
                }
            }
        }
    }






    //내 커리큘럼 timetaken 수정하는 메서드
    public Curriculum updateTimeTaken(int memberId, int curriculumId, int newTimeTaken) {

        //커리큘럼 id로 커리큘럼 찾기
        Curriculum curriculum = curriculumRepository.findByCurriculumId(memberId, curriculumId)
                .orElseThrow(() -> new RuntimeException("회원님의 커리큘럼이 없거나, 타 계정 커리큘럼에 접근해 권한이없습니다.."));

        if (curriculum.getEdit() == 1) {
            //timetaken업데이트
            curriculum.setTimeTaken(newTimeTaken);
            //변경사항 저장하고 업데이트된 커리큘럼 반환
            return curriculumRepository.save(curriculum);
        } else {
            throw new RuntimeException("커리큘럼을 수정할 수 없습니다.");
        }
    }


    //강좌에 해당하는 모든 비디오 다들으면 lecture_progress 에 state 가 0-> 1로 업데이트 아니면 0
    public void updateLectureProgressState(int memberId, int lectureId) {
        // Member와 Lecture 객체를 가져옵니다.
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("없는 회원입니다."));
        Lecture lecture = lectureRepository.findById(lectureId);
        if (lecture == null) {
            throw new RuntimeException("없는 강의입니다.");
        }

        // 해당 회원의 수강중인 Lecture 에대한  LectureProgress를 가져옴
        List<LectureProgress> lectureProgress = lectureProgressRepository.findByMemberLecture(member, lecture);
        if (lectureProgress.isEmpty()) {
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
            for (LectureProgress lp : lectureProgress) {
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
            List<Object[]> videoCounts = videoRepository.findTotalAndCompletedVideosForSection(memberId, section.getId());
            if (!videoCounts.isEmpty()) {
                Object[] counts = videoCounts.get(0);
                totalVideos += toInt(counts[0]);
                completedVideos += toInt(counts[1]);
            }
        }
        return new int[]{totalVideos, completedVideos};
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