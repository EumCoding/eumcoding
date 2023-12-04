package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.MainTestDTO;
import com.latteis.eumcoding.dto.MainTestLogDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import com.latteis.eumcoding.util.blockCoding.Block;
import com.latteis.eumcoding.util.blockCoding.BlockCodeToJavaConverter;
import com.latteis.eumcoding.util.blockCoding.JavaCodeExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainTestLogService {

    private final MemberRepository memberRepository;

    private final MainTestRepository mainTestRepository;

    private final MainTestQuestionRepository mainTestQuestionRepository;

    private final MainTestAnswerRepository mainTestAnswerRepository;

    private final MainTestLogRepository mainTestLogRepository;

    private final BlockCodeToJavaConverter blockCodeToJavaConverter;

    private final JavaCodeExecutor javaCodeExecutor = new JavaCodeExecutor();


    /**
     * 해당 MainTest 채점해서 점수 반환
     * @param authentication 로그인 정보
     * @param scoringDTO 채점 요청 DTO
     * @return 점수
     */
    public int saveAnswerAndGetScore(Authentication authentication, MainTestLogDTO.ScoringDTO scoringDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        MainTest mainTest = mainTestRepository.findById(scoringDTO.getMainTestId());

        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 등록된 MainTest인지 검사
        if (mainTest == null) {
            throw new ResponseMessageException(ErrorCode.MAIN_TEST_NOT_FOUND);
        }

        // 반환할 시험 점수
        int score = 0;
        // 받아온 학생 답안 반복 처리
        for (MainTestLogDTO.LogDTO logDTO : scoringDTO.getLogDTOList()) {

            // MainTestQuestion Entity생성
            MainTestQuestion mainTestQuestion = mainTestQuestionRepository.findByMainTestQuestionId(logDTO.getMainTestQuestionId());
            // 정답인지 체크
            boolean scoring = true;

            // 객관식 문제라면
            if (mainTestQuestion.getType() == 0) {

                List<String> multipleChoiceList = logDTO.getMultipleChoiceList();
                if (multipleChoiceList.isEmpty()) {
                    throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
                }

                // 해당 문제 답안 전부 가져오기
                List<MainTestAnswer> mainTestAnswerList = mainTestAnswerRepository.findAllByMainTestQuestion(mainTestQuestion);

                // 리스트 크기가 다르면 flase
                if (multipleChoiceList.size() != mainTestAnswerList.size()) {
                    scoring = false;
                }

                // 답안으로 리스트 정렬
                Collections.sort(multipleChoiceList);
                Collections.sort(mainTestAnswerList , Comparator.comparing(MainTestAnswer::getAnswer, Comparator.naturalOrder()));

                // 리스트 비교해서 다르면 false 리턴
                for (int i = 0; i < mainTestAnswerList.size(); i++) {
                    if (!mainTestAnswerList.get(i).getAnswer().equals(multipleChoiceList.get(i))) {
                        scoring = false;
                    }
                }

                // 답안 저장 DTO 생성 후 저장
                for (String log : multipleChoiceList) {
                    // 학생 답변 저장
                    MainTestLog mainTestLog = MainTestLog.builder()
                            .mainTestQuestion(mainTestQuestion)
                            .member(member)
                            .subAnswer(log)
                            .scoring(scoring)
                            .build();
                    mainTestLogRepository.save(mainTestLog);
                }

            }
            // 블록코딩 문제라면
            else if (mainTestQuestion.getType() == 1) {

                // block list가 비었는지 검사
                if (logDTO.getBlockList() == null) {
                    throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
                }

                // 학생 답안을 자바 코드로 변환
                String testString = blockCodeToJavaConverter.convertToJavaCode(logDTO.getBlockList());

                // 변환된 답안을 컴파일
                String testAnswerString = null;

                try{
                    testAnswerString = javaCodeExecutor.executeJavaCode(testString);
                }catch(Exception e){
                    e.printStackTrace();
                    log.info("컴파일 에러");
                }

                // 변환된 답안을 문제 답과 비교
                MainTestAnswer mainTestAnswer = mainTestAnswerRepository.findByMainTestQuestion(mainTestQuestion);

                // 문자열의 공백을 제거하고 비교
                String formattedTestAnswer = testAnswerString.toString().trim();
                String formattedCorrectAnswer = mainTestAnswer.getAnswer().trim();

                if (!formattedTestAnswer.equals(formattedCorrectAnswer)) {
                    scoring = false;
                }

                // 학생 답변 저장
                MainTestLog mainTestLog = MainTestLog.builder()
                        .mainTestQuestion(mainTestQuestion)
                        .member(member)
                        .subAnswer(formattedTestAnswer)
                        .scoring(scoring)
                        .build();
                mainTestLogRepository.save(mainTestLog);

            }

            // 정답이면 해당 문제 점수를 더함
            if (scoring) {
                score += mainTestQuestion.getScore();
            }

        }

        // 점수 반환
        return score;

    }

    /**
     * 채점 결과 가져오기
     * @param authentication 로그인 정보
     * @param idDTO 메인 테스트 ID
     * @return 채점 결과 DTO
     */
    public MainTestLogDTO.ScoringResponseDTO getScore(Authentication authentication, MainTestDTO.IdDTO idDTO) {

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

        List<MainTestLog> mainTestLogs = mainTestLogRepository.findAllByMemberAndMainTestQuestionMainTest(member, mainTest);

        // 점수 저장할 변수
        int score = 0;
        // 만점 저장
        int perfectScore = 0;
        // 로그 반복하면서 정답이면 점수 추가
        for (MainTestLog mainTestLog : mainTestLogs) {
            // 정답인 답변이라면 점수 추가
            if (mainTestLog.isScoring()) {
                score += mainTestLog.getMainTestQuestion().getScore();
            }
            //만점 점수 저장
            perfectScore += mainTestLog.getMainTestQuestion().getScore();
        }

        return new MainTestLogDTO.ScoringResponseDTO(score, perfectScore);

    }
}
