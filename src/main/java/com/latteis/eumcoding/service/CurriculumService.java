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
//myplan/list, myplan/updatelectureProgress
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final VideoRepository videoRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final SectionRepository sectionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    /**
     * 오늘 들을 강의
     * checkOver 에서 return 3인것들을 호출하는 메서드
     */
    public List<MyPlanInfoDTO> getTodayPlanInfo(int memberId, Integer lectureId){
        //특정 회원이 가지고 있는 모든 커리큘럼을 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberIdAndLectureId(memberId, lectureId);

        List<MyPlanInfoDTO> myPlanInfoDTOS = new ArrayList<>();

        for(Curriculum curriculum : curriculums){
            List<SectionDTO.SectionDTOMessageList> sectionDTOMessageLists= new ArrayList<>();

            List<Section> lectureSections = sectionRepository.findByCurriculumId(curriculum.getId());

            for(Section lectureSection : lectureSections){
                //각 섹션에 포함된 전체 비디오의 수를 조회
                long totalVideos = videoRepository.countBySectionId(lectureSection.getId());
                // 전체 비디오 수와 완료된 비디오 수 계산
                int[] videoCounts = countTotalAndCompletedVideos(memberId, lectureSection);
                int totalVideoss = videoCounts[0];
                int completedVideoss = videoCounts[1];
                int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideoss * 100 / totalVideoss);
                int over = checkOver(memberId, lectureSection.getId());

                if(over == 3){
                    SectionDTO.SectionDTOMessageList sectionDTO = SectionDTO.SectionDTOMessageList.builder()
                            .sectionId(lectureSection.getId())
                            .lectureId(lectureSection.getLecture().getId())
                            .lectureName(lectureSection.getLecture().getName())
                            .sectionName(lectureSection.getName())
                            .progress(progress)
                            .over(over)
                            .timetaken(curriculum.getTimeTaken())
                            .message("현재 들어야 하는 섹션입니다.")
                            .build();

                    sectionDTOMessageLists.add(sectionDTO);
                }
            }
            if(!sectionDTOMessageLists.isEmpty()){
                MyPlanInfoDTO myPlanInfoDTO = MyPlanInfoDTO.builder()
                        .curriculumId(curriculum.getId())
                        .date(curriculum.getStartDay())
                        .editDay(curriculum.getEditDay())
                        .sectionDTOList(sectionDTOMessageLists)
                        .build();
                myPlanInfoDTOS.add(myPlanInfoDTO);
            }
        }
        return myPlanInfoDTOS;
    }


    //기존 getMyPlanList랑 다른점은 해당 커리큘럼에 있는 섹션을 기한내에 다들었는지
    //혹은 이번에 들어야하는 섹션인지 표시해주는게 들어가있음
    public List<MyPlanInfoDTO> getMyPlanInfo(int memberId, Integer lectureId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }

        //특정 회원이 가지고 있는 모든 커리큘럼을 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberIdAndMonthYear(memberId, lectureId, startDate, endDate);

        // 커리큘럼 정보가 없으면 예외를 던짐
        if (curriculums.isEmpty()) {
            throw new RuntimeException("해당 날짜에는 커리큘럼 정보가 없습니다.");
        }


        //사용자의 학습 진행 상황을 나타내는 DTO
        List<MyPlanInfoDTO> myPlanInfoList = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();

        for (Curriculum curriculum : curriculums) {
            List<SectionDTO.SectionDTOMessageList> sectionDTOMessageLists = new ArrayList<>();
            //다음 섹션 startDay 1초전까지 수강들을수있는 범위
            LocalDateTime sectionDeadline = curriculum.getStartDay().plusDays(curriculum.getTimeTaken()).minusSeconds(1);
            LocalDateTime nextSectionStart = calculateNextSectionStartDay(memberId, curriculum.getSection().getLecture().getId(), curriculum.getStartDay(), curriculums);
            LocalDateTime effectiveDeadline = (curriculum.getEditDay() != null) ? curriculum.getEditDay() : sectionDeadline;
            LocalDateTime newEditDay = LocalDateTime.of(today.toLocalDate(), LocalTime.now());


            //현재 커리큘럼의 id에 대응하는 섹션을 가져옴
            List<Section> lectureSections = sectionRepository.findByCurriculumId(curriculum.getId());


            //현재 커리큘럼에 포함된 강의의 모든 섹션을 조회
            for (Section lectureSection : lectureSections) {

                //각 섹션에 포함된 전체 비디오의 수를 조회
                long totalVideos = videoRepository.countBySectionId(lectureSection.getId());

                // 전체 비디오 수와 완료된 비디오 수 계산
                int[] videoCounts = countTotalAndCompletedVideos(memberId, lectureSection);
                int totalVideoss = videoCounts[0];
                int completedVideoss = videoCounts[1];
                int progress = totalVideos == 0 ? 0 : (int) Math.round((double) completedVideoss * 100 / totalVideoss);
                int over = checkOver(memberId, curriculum.getSection().getId());
                System.out.println(curriculum.getSection().getId() + " : sectionId");


                String message = "";

                // 현재 섹션 처리
                if (over == 1) {
                    // 현재 날짜가 editDay 이후이거나, editDay가 없는데 다음 섹션 종료일 이후라면,
                    // 현재 섹션의 editDay를 업데이트하고, 다음 섹션들의 startDay를 업데이트
                    if (today.isAfter(effectiveDeadline)) {
                        updateEditDay(curriculum.getId(), newEditDay); // 현재 섹션의 editDay 업데이트
                        //System.out.println(curriculum.getSection().getId() + ": over 가 1인경우");
                        message = "해당 섹션 수강 기한 초과" + curriculum.getEditDay().toLocalDate() + "일로 연장";
                        // 다음 섹션들의 startDay만 업데이트해야 하므로, 현재 섹션의 ID보다 큰 섹션들을 찾습니다.
                        Integer inputLectureId = lectureId != null ? lectureId : curriculum.getSection().getLecture().getId();
                        updateStartDaysSections(curriculums, lectureSection.getId(), inputLectureId, newEditDay.plusDays(curriculum.getTimeTaken()));

                    }
                } else if (over == 2) {
                    message = "해당 섹션을 들을 기간이 아닙니다.";
                } else if (over == 0 && (nextSectionStart == null || effectiveDeadline.isBefore(nextSectionStart))) {
                    //System.out.println(curriculum.getSection().getId() + ": over 가 0인경우");
                    message = "해당 섹션 수강을 기한 내 완료";
                }  else if (over == 3) {
                    message = "현재 들어야하는 섹션입니다.";
                }

                SectionDTO.SectionDTOMessageList sectionDTO = SectionDTO.SectionDTOMessageList.builder()
                        .sectionId(lectureSection.getId())
                        .lectureId(lectureSection.getLecture().getId())
                        .lectureName(lectureSection.getLecture().getName())
                        .sectionName(lectureSection.getName())
                        .progress(progress)
                        .over(over)
                        .timetaken(curriculum.getTimeTaken())
                        .message(message)
                        .build();

                sectionDTOMessageLists.add(sectionDTO);
            }


            MyPlanInfoDTO myPlanInfoDTO = MyPlanInfoDTO.builder()
                    .curriculumId(curriculum.getId())
                    .date(curriculum.getStartDay())
                    .editDay(curriculum.getEditDay())
                    .sectionDTOList(sectionDTOMessageLists)
                    .build();

            myPlanInfoList.add(myPlanInfoDTO);
        }

        return myPlanInfoList;
    }
/*
    private boolean isLastSection(int lecutreId) {
        Integer maxSectionId = curriculumRepository.findMaxSectionIdByLectureId(lecutreId);
        return maxSectionId != null && maxSectionId.equals(lecutreId);
    }*/

    @Transactional
    public int checkOver(int memberId, int sectionId) {
        List<VideoProgress> lastProgress = videoProgressRepository.findVideoProgressEndDay(memberId, sectionId);
        int isAnyVideoNotStarted = 0;
        LocalDateTime today = LocalDateTime.now();
        List<Curriculum> curriculumList = curriculumRepository.findByMemberSectionId(memberId, sectionId);

        //해당 lectureId에 속한 section들의 startDay를 가져와서
        //다음 섹션의 startDay.isBefore(LocalDateTime.now() 이런식으로 progress.state() = 0 일때,
        //이러면 over가 3이라는 새로운 조건 생성 가능할듯
        for (Curriculum curriculum : curriculumList) {
            LocalDateTime sectionDeadline = curriculum.getStartDay().plusDays(curriculum.getTimeTaken()).minusSeconds(1);
            LocalDateTime effectiveDeadline = (curriculum.getEditDay() != null) ? curriculum.getEditDay() : sectionDeadline;
            LocalDateTime nextSectionStartDay = calculateNextSectionStartDay(memberId, curriculum.getSection().getLecture().getId(), sectionDeadline, curriculumList);

            /**
             * videoProgress테이블 자체에 해당 video에 대한 컬럼이 없을경우 ->0
             * videoPRogress테이블에 video에대한 정보는 있는데 state가 0일경우 ->1
             */
            System.out.println("Checking progress for curriculum: " + curriculum.getId());
            for (VideoProgress progress : lastProgress) {
                System.out.println("Progress: " + progress);
                //강의를 듣지않은상태(구매후 아예 안들었거나 or 섹션 1를 다안들어서 섹션 2의 대한 vp정보가 없을경우
                // 비디오 진행 정보가 없거나 state가 NULL인 경우
                if (progress == null) {
                    /**
                     * 12.03 추가, 만약 현재 섹션을 다듣고, 날짜도 그렇고 해당 섹션을 들어야하는 순서임에도 불구하고, progress가 null일경우
                     * 를 고려해서  이럴 경우에는 over가 3인것처럼 현재 들어야하는 섹션입니다가 표시되게 해야함.
                     */
                 /*   if (today.isAfter(curriculum.getStartDay()) && (nextSectionStartDay == null || today.isBefore(nextSectionStartDay))) {
                        return 3; // 현재 들어야 하는 섹션
                    }*/

                } else if (progress.getState() == 1) {
                    // 비디오가 완료된 상태
                    //log.info("Section " + progress.getVideo().getSection().getId() + " over 0");
                    isAnyVideoNotStarted = 2;
                } else if (progress.getState() == 0) {
                    if (today.isBefore(effectiveDeadline)) {
                        isAnyVideoNotStarted = 3; // 진행 중이지만 아직 기한 내
                    } else {
                        isAnyVideoNotStarted = 1; // 기한 오버
                    }
                }
            }
        }

        if (isAnyVideoNotStarted == 0) {
            //log.info("아직 섹션를 들을 순서가 아닙니다.");
            // 아직 시작하지 않은 비디오
            return 2;
        } else if (isAnyVideoNotStarted == 1) {
            // 기한 오버
            return 1;
        } else if (isAnyVideoNotStarted == 3) {
            return 3;
        } else {
            // 기한 내 완료
            return 0;
        }


    }


    //다음 섹션 startDay찾는 메서드
    private LocalDateTime calculateNextSectionStartDay(int memberId, int lectureId, LocalDateTime currentSectionEndDay, List<Curriculum> curriculums) {
        // 현재 섹션의 마감일 이후에 시작하는 섹션 중 가장 빠른 시작일을 가진 섹션을 찾아서 반환
        LocalDateTime nextSectionStartDay = null;

        for (Curriculum curriculum : curriculums) {
            if (curriculum.getMember().getId() == memberId && curriculum.getSection().getLecture().getId() == lectureId) {
                LocalDateTime startDay = curriculum.getStartDay();
                // 현재 섹션의 종료일 이후에 시작하는 섹션 중에서 가장 빠른 시작일
                // nextSectionStartDay가 null이거나 현재 섹션의 startDay가 더 이른 날짜인 경우에만 갱신
                if (startDay.isAfter(currentSectionEndDay) && (nextSectionStartDay == null || startDay.isBefore(nextSectionStartDay))) {
                    nextSectionStartDay = startDay;
                }
            }
        }
        return nextSectionStartDay; // 다음 섹션이 있으면 그 섹션의 시작 날짜, 없으면 null 반환
    }


    @Transactional
    public void updateEditDay(int curriculumId, LocalDateTime localDateTime) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId).orElse(null);
        if (curriculum != null) {
            // 현재 날짜와 시간을 구합니다.
            localDateTime = LocalDateTime.now();
            // 마감 기한을 계산합니다.
            LocalDateTime sectionDeadline = curriculum.getStartDay().plusDays(curriculum.getTimeTaken()).minusSeconds(1);
            LocalDateTime effectiveDeadline = (curriculum.getEditDay() != null) ? curriculum.getEditDay() : sectionDeadline;

            // 현재 날짜가 마감 기한을 넘었는지 확인합니다.
            if (localDateTime.isAfter(effectiveDeadline)) {
                // 넘었다면, editDay를 그 날의 끝 시간으로 설정
                LocalDateTime endOfDay = localDateTime.toLocalDate().atTime(23, 59, 59);
                curriculum.setEditDay(endOfDay);
                log.info("editDay업데이트", curriculumId, endOfDay);
            } else {
                // 현재 날짜가 마감 기한을 넘지 않았다면, editDay를 업데이트하지 않습니다.
                log.info("마감기한안넘음", curriculumId);
                return;
            }
            curriculumRepository.save(curriculum);
        } else {
            log.error("커리큘럼없음", curriculumId);
        }
    }


    //섹션별 startDay 업데이트
    @Transactional
    public void updateStartDaysSections(List<Curriculum> curriculums, int currentSectionId, Integer lectureId, LocalDateTime newStartDayForNextSections) {
        for (Curriculum curriculum : curriculums) {
            if (lectureId == null || curriculum.getSection().getLecture().getId() == lectureId) {
                if (curriculum.getSection().getId() > currentSectionId) {
                    LocalDateTime currentStartDay = curriculum.getStartDay();
                    if (newStartDayForNextSections.isAfter(currentStartDay)) {
                        curriculum.setStartDay(newStartDayForNextSections);
                        curriculumRepository.save(curriculum);
                        newStartDayForNextSections = newStartDayForNextSections.plusDays(curriculum.getTimeTaken());
                    }
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