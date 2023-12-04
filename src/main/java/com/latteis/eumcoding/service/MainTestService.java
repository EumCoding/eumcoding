package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MainTestDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import javafx.scene.canvas.GraphicsContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainTestService {

    private final MainTestRepository mainTestRepository;

    private final MemberRepository memberRepository;

    private final SectionRepository sectionRepository;

    private final MainTestQuestionRepository mainTestQuestionRepository;

    private final MainTestMultipleChoiceViewRepository mainTestMultipleChoiceViewRepository;

    private final MainTestBlockRepository mainTestBlockRepository;

    private final VideoRepository videoRepository;

    private final VideoProgressRepository videoProgressRepository;

    //lectureRepository
    private final LectureRepository lectureRepository;

    //mainTestAnswerRepository
    private final MainTestAnswerRepository mainTestAnswerRepository;

    private final MainTestLogRepository mainTestLogRepository;


    // main test 문제 리스트 가져오기 getMainTestQuestion
    public List<MainTestDTO.MainTestQuestionInfoRequestDTO> getMainTestQuestion(int mainTestId) {
        try{
            // mainTestId로 mainTest 가져오기
            MainTest mainTest = mainTestRepository.findById(mainTestId);
            // 해당 메인 평가의 문제 리스트 가져오기
            List<MainTestQuestion> mainTestQuestionList = mainTestQuestionRepository.findAllByMainTest(mainTest);
            // MainTestDTO.MainTestQuestionInfoRequestDTO로 변환
            List<MainTestDTO.MainTestQuestionInfoRequestDTO> responseDTO = mainTestQuestionList.stream().map(MainTestDTO.MainTestQuestionInfoRequestDTO::new).collect(java.util.stream.Collectors.toList());
            for(MainTestDTO.MainTestQuestionInfoRequestDTO tempResponseDTO : responseDTO){
                // answerList 가져와서 삽입하기
                List<MainTestAnswer> mainTestAnswerList = mainTestAnswerRepository.findByMainTestQuestionId(tempResponseDTO.getMainTestQuestionId());
                List<String> answerList = new ArrayList<>();
                for(MainTestAnswer tempMainTestAnswer : mainTestAnswerList){
                    answerList.add(tempMainTestAnswer.getAnswer());
                }
                // tempResponseDTO에 저장
                tempResponseDTO.setAnswer(answerList);
                // type이 0인경우(객관식) MainTestQuestionId로 multipleChoiceList 가져오기
                if(tempResponseDTO.getType() == 0){
                    List<MainTestMultipleChoiceView> mainTestMultipleChoiceViewList = mainTestMultipleChoiceViewRepository.findAllByMainTestQuestionIdOrderBySequenceDesc(tempResponseDTO.getMainTestQuestionId());
                    // mainTestMultipleChoiceViewList의 content만 가져와서 List<String> 형태로 변환
                    List<String> contentList = new ArrayList<>();
                    for(MainTestMultipleChoiceView tempMainTestMultipleChoiceView : mainTestMultipleChoiceViewList){
                        contentList.add(tempMainTestMultipleChoiceView.getContent());
                    }
                    // tempResponseDTO에 저장
                    tempResponseDTO.setChoices(contentList);
                }else if(tempResponseDTO.getType() == 1){
                    // 블록코딩인 경우 BlockList 가져와서 저장
                    List<MainTestBlock> mainTestBlockList = mainTestBlockRepository.findAllByMainTestQuestionId(tempResponseDTO.getMainTestQuestionId());
                    List<MainTestDTO.BlockDTO> mainTestBlockDTOList = mainTestBlockList.stream()
                            .map(block -> new MainTestDTO.BlockDTO(block.getBlock(), block.getValue(), block.getId()))
                            .collect(Collectors.toList());
                    // tempResponseDTO에 저장
                    tempResponseDTO.setBlockList(mainTestBlockDTOList);
                }
            }
            // 리턴
            return responseDTO;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //updateSection
    public void updateSection(int memberId, MainTestDTO.UpdateSectionRequestDTO updateSectionRequestDTO) {
        try{
            // lecture id로 section list 가져오기
            List<Section> sectionList = sectionRepository.findByLectureId(updateSectionRequestDTO.getLectureId());
            // sectionList를 for문으로 돌면서 해당 section으로 mainTest를 조회해 type이 일치하는 mainTest가 있는지 찾기
            if(sectionList.size() == 0){
                // 예외처리
                Preconditions.checkArgument(false, "Section이 없습니다.", updateSectionRequestDTO.getLectureId());
            }
            for(Section tempSection : sectionList){
                List<MainTest> mainTestList = mainTestRepository.findAllBySectionIdAndType(tempSection.getId(), updateSectionRequestDTO.getType());
                // 있으면 해당 mainTest의 section을 updateSectionRequestDTO의 sectionId로 수정
                if(mainTestList.size() > 0){
                    MainTest mainTest = mainTestList.get(0);
                    mainTest.setSection(sectionRepository.findBySectionId(updateSectionRequestDTO.getSectionId()));
                    mainTestRepository.save(mainTest);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // 메인 평가 정보 리스트 가져오기 getMainTest
    public List<MainTestDTO.MainTestInfoRequestDTO> getMainTest(int lectureId) {
        // 해당 강의의 Section List 가져오기
        List<Section> sectionList = sectionRepository.findByLectureId(lectureId);

        List<MainTestDTO.MainTestInfoRequestDTO> responseDTO = new ArrayList<>();

        for(Section tempSection : sectionList){
            // section 정보를 이용해 MainTest List 가져오기
            List<MainTest> mainTestList = mainTestRepository.findAllBySection(tempSection);
            log.info("mainTestList.size() = " + mainTestList.size());
            if(mainTestList.size() > 0){
                for(MainTest tempMainTest : mainTestList){
                    // MainTestDTO.MainTestInfoRequestDTO로 변환
                    MainTestDTO.MainTestInfoRequestDTO tempResponseDTO = new MainTestDTO.MainTestInfoRequestDTO(tempMainTest);
                    responseDTO.add(tempResponseDTO);
                }
            }
        }
        // 리턴
        return responseDTO;
    }


    // 메인 평가 등록
    public void addMainTest(int memberId, MainTestDTO.AddRequestDTO addRequestDTO) {
        try {
//            // 섹션 정보 가져오기
//            Section section = sectionRepository.findBySectionId(addRequestDTO.getSectionId());
//            Preconditions.checkNotNull(section, "등록된 섹션이 없습니다. (섹션 ID : %s)", addRequestDTO.getSectionId());
//
//            // 등록된 회원인지 검사
//            Member member = memberRepository.findByMemberId(memberId);
//            Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);
//
//            // 본인 체크
//            int lectureUploader = section.getLecture().getMember().getId();
//            Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", section.getLecture().getId(), lectureUploader, memberId);
//
//            // 해당 유형의 평가가 이미 등록되어 있는지 검사
////        long cnt = mainTestRepository.countByTypeAndLecture(addRequestDTO.getType(), section.getLecture());
////        Preconditions.checkArgument(cnt == 0, "해당 유형의 평가가 이미 등록되어 있습니다. (type : %s)", addRequestDTO.getType());
//            Preconditions.checkArgument(!mainTestRepository.existsByTypeAndSectionLecture(addRequestDTO.getType(), section.getLecture()), "해당 유형의 평가가 이미 등록되어 있습니다. (type : %s)", addRequestDTO.getType());
//
//            // 해당 섹션에 평가가 이미 등록되어 있는지 검사
//            Preconditions.checkArgument(!mainTestRepository.existsBySection(section), "해당 섹션에 평가가 이미 등록되어 있습니다. (섹션 ID : %s)", section.getId());

            log.info("MainTestType" + addRequestDTO.getMainTestType());

            // 해당 강의에 mainTest가 있는지 체크(없으면 생성해서 해당하는 mainTest의 sectionId를 가져옵니다.)
            List<MainTest> mainTestList = mainTestRepository.findAllBySectionIdAndType(addRequestDTO.getSectionId(), addRequestDTO.getMainTestType());
            int sectionId = 0;
            // 없으면 LectureId로도 찾기
            if (mainTestList.size() == 0) {
                List<Section> tempSectionList = sectionRepository.findByLectureId(addRequestDTO.getLectureId());
                if (tempSectionList.size() > 0) {
                    for (Section tempSection : tempSectionList) {
                        mainTestList = mainTestRepository.findAllBySectionIdAndType(tempSection.getId(), addRequestDTO.getMainTestType());
                        if (mainTestList.size() > 0) {
                            sectionId = mainTestList.get(0).getSection().getId();
                            break;
                        }
                    }
                }
            }
            // mainTest 객체를 저장할 곳
            MainTest mainTest = null;
            log.info("mainTestList.size() = " + mainTestList.size());
            log.info("sectionId = " + sectionId);

            if (mainTestList.size() == 0 && sectionId == 0) { // 들어온 Section도 없고 찾은 Section도 없을때 mainTest 만들기
                log.info("들어온 Section도 없고 찾은 Section도 없을때 mainTest 만들기");
                // 들어온 section이 0이 아니면 section을 가져옴
                Section section = null;
                if (addRequestDTO.getSectionId() != 0) { // 들어온 section이 있을때
                    section = sectionRepository.findBySectionId(addRequestDTO.getSectionId()); // section아이디로 section 찾아서 넣음
                } else {
                    // 들어온 sectionId도 없고 현재 생성된 section이 하나도 없으면 section 생성 후 해당 section 가져옴
                    if (sectionRepository.countByLectureId(addRequestDTO.getLectureId()) == 0) {
                        log.info("들어온 lectureId를 이용해 section을 생성합니다... lectureId = " + addRequestDTO.getLectureId());
                        Section tempSection = Section.builder()
                                .lecture(lectureRepository.findById(addRequestDTO.getLectureId()))
                                .build();
                        sectionRepository.save(tempSection);
                        section = tempSection;
                    } else {
                        // 생성된 section이 있으면 0번 section을 가져옴
                        section = sectionRepository.findAllByLectureId(addRequestDTO.getLectureId()).get(0);
                    }
                }
                // mainTest가 없으면 생성
                MainTest tempMainTest = MainTest.builder()
                        .section(section)
                        .type(addRequestDTO.getMainTestType())
                        .description(addRequestDTO.getDescription())
                        .build();

                // id를 변수로 받아오면서 저장 db에 저장
                mainTestRepository.save(tempMainTest).getId();
                mainTest = tempMainTest;
            }else if(mainTestList.size() == 0 && sectionId != 0){ // manTestList는 없는데 sectionId는 찾았을 때 해당 SectionId로 조회해서 mainTest 가져옵니다.
                log.info("manTestList는 없는데 sectionId는 찾았을 때 해당 SectionId로 조회해서 mainTest 가져옵니다.");
                mainTestList = mainTestRepository.findAllBySectionIdAndType(sectionId, addRequestDTO.getMainTestType());
                if(mainTestList.size() == 0){
                    // 예외처리
                    Preconditions.checkArgument(false, "Section 오류. 다시시도해주세요.", sectionId);
                }else{
                    mainTest = mainTestList.get(0);
                }
            }else{
                log.info("mainTestList가 있을 때");
                // mainTest가 있으면 해당 객체를 가져옴
                mainTest = mainTestList.get(0);
            }

            log.info("해당 main test에 해당하는 mainTestQuestion이 몇개 있는지 조회");
            // 해당 main test에 해당하는 mainTestQuestion이 몇개 있는지 조회
            long cnt = mainTestQuestionRepository.countByMainTest(mainTest);

            log.info("mainTestQuestion을 먼저 DB에 저장");
            // mainTestQuestion을 먼저 DB에 저장
            MainTestQuestion mainTestQuestion = MainTestQuestion.builder()
                    .mainTest(mainTest)
                    .title(addRequestDTO.getDescription())
                    .type(addRequestDTO.getType())
                    .score(addRequestDTO.getScore())
                    .sequence((int)cnt)
                    .build();

            // DB에 저장
            mainTestQuestionRepository.save(mainTestQuestion);

            log.info("답변리스트를 DB에 저장");
            // 답변리스트를 DB에 저장
            for (String answer : addRequestDTO.getAnswer()) {
                MainTestAnswer mainTestAnswer = MainTestAnswer.builder()
                        .mainTestQuestion(mainTestQuestion)
                        .answer(answer)
                        .build();
                // db에 저장
                mainTestAnswerRepository.save(mainTestAnswer);
            }

            // 객관식인 경우 multipleList에 보기 저장
            if(addRequestDTO.getType() == 0){
                // 보기 리스트를 DB에 저장
                for (String choice : addRequestDTO.getChoices()) {
                    // 현재 보기 리스트가 몇개 있는지 조회
                    long tempCnt = mainTestMultipleChoiceViewRepository.countByMainTestQuestion(mainTestQuestion);
                    MainTestMultipleChoiceView mainTestMultipleChoice = MainTestMultipleChoiceView.builder()
                            .mainTestQuestion(mainTestQuestion)
                            .content(choice)
                            .sequence((int)tempCnt)
                            .build();
                    // DB에 저장
                    mainTestMultipleChoiceViewRepository.save(mainTestMultipleChoice);
                }
            }

            // 블록코딩인 경우 blockList에 블록 저장
            if(addRequestDTO.getType() == 1){
                // 블록 리스트를 DB에 저장
                for (MainTestDTO.BlockDTO blockDTO : addRequestDTO.getBlockList()) {
                    MainTestBlock mainTestBlock = MainTestBlock.builder()
                            .mainTestQuestion(mainTestQuestion)
                            .block(blockDTO.getBlock())
                            .value(blockDTO.getValue())
                            .build();
                    // DB에 저장
                    mainTestBlockRepository.save(mainTestBlock);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    // 문제 삭제하기
    public void deleteMainTestQuestion(int memberId, int mainTestQuestionId) {
        try{
            // mainTestQuestionId로 mainTestQuestion 가져오기
            MainTestQuestion mainTestQuestion = mainTestQuestionRepository.findById(mainTestQuestionId).get();
            // mainTestQuestionId로 mainTestAnswer 가져오기
            List<MainTestAnswer> mainTestAnswerList = mainTestAnswerRepository.findByMainTestQuestionId(mainTestQuestionId);
            // mainTestQuestionId로 mainTestMultipleChoiceView 가져오기
            List<MainTestMultipleChoiceView> mainTestMultipleChoiceViewList = mainTestMultipleChoiceViewRepository.findAllByMainTestQuestionId(mainTestQuestionId);
            // mainTestQuestionId로 mainTestBlock 가져오기
            List<MainTestBlock> mainTestBlockList = mainTestBlockRepository.findAllByMainTestQuestionId(mainTestQuestionId);
            // mainTestQuestionId로 mainTest 가져오기
            MainTest mainTest = mainTestRepository.findById(mainTestQuestion.getMainTest().getId());
            // mainTestQuestionId로 mainTestQuestion 삭제
            mainTestQuestionRepository.delete(mainTestQuestion);
            // mainTestAnswer 삭제
            for(MainTestAnswer tempMainTestAnswer : mainTestAnswerList){
                mainTestAnswerRepository.delete(tempMainTestAnswer);
            }
            // mainTestMultipleChoiceView 삭제
            for(MainTestMultipleChoiceView tempMainTestMultipleChoiceView : mainTestMultipleChoiceViewList){
                mainTestMultipleChoiceViewRepository.delete(tempMainTestMultipleChoiceView);
            }
            // mainTestBlock 삭제
            for(MainTestBlock tempMainTestBlock : mainTestBlockList){
                mainTestBlockRepository.delete(tempMainTestBlock);
            }
            // mainTestQuestionId로 mainTestQuestion 가져오기
            List<MainTestQuestion> mainTestQuestionList = mainTestQuestionRepository.findAllByMainTest(mainTest);
            // mainTestQuestionList를 for문으로 돌면서 sequence를 1씩 줄여서 저장
            for(MainTestQuestion tempMainTestQuestion : mainTestQuestionList){
                if(tempMainTestQuestion.getSequence() > mainTestQuestion.getSequence()){
                    tempMainTestQuestion.setSequence(tempMainTestQuestion.getSequence() - 1);
                    mainTestQuestionRepository.save(tempMainTestQuestion);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param authentication 로그인 정보
     * @param idDTO MainTest ID DTO
     * @return 자격 충족 : 0 / 불충족 : 1 / 응시함 : 2
     */
    public Integer confirmationOfEligibility(Authentication authentication, MainTestDTO.IdDTO idDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        MainTest mainTest = mainTestRepository.findById(idDTO.getMainTestId());

        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 등록된 MainTest인지 검사
        if (mainTest == null) {
            throw new ResponseMessageException(ErrorCode.MAIN_TEST_NOT_FOUND);
        }

        // mainTestQuestion 수와 mainTestLog 수가 같다면 
        long mainTestQuestionCnt = mainTestQuestionRepository.countByMainTest(mainTest);
        long mainTestLog = mainTestLogRepository.countByMemberAndMainTestQuestionMainTest(member, mainTest);
        if (mainTestQuestionCnt == mainTestLog) {
            return 2; // 응시함
        }

        Video video = videoRepository.findTopBySectionIdOrderBySequenceDesc(mainTest.getSection().getId());
        boolean satisfy = videoProgressRepository.existsByVideoAndStateAndLectureProgressPayLecturePaymentMember(video, 1, member);

        if (satisfy) {
            return 0; // 자격 충족
        }
        else {
            return 1; // 자격 불충족
        }

    }

    /**
     * 해당 강의에 해당하는 MainTest 리스트
     * @param authentication 로그인 정보
     * @param lectureIdDTO 강의 ID
     * @return MainTestId List
     */
    public List<Integer> getIDs(Authentication authentication, LectureDTO.IdRequestDTO lectureIdDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        Lecture lecture = lectureRepository.findById(lectureIdDTO.getId());

        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 등록된 Lecture인지 검사
        if (lecture == null) {
            throw new ResponseMessageException(ErrorCode.LECTURE_NOT_FOUND);
        }

        List<MainTest> mainTestList = mainTestRepository.findBySectionLecture(lecture);
        List<Integer> mainTestIds = new ArrayList<>();
        for (MainTest mainTest : mainTestList) {
            mainTestIds.add(mainTest.getId());
        }

        return mainTestIds;

    }

}
