package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoTestBlockListDTO;
import com.latteis.eumcoding.dto.VideoTestLogDTO;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTestBlockListService {

    private final VideoTestRepository videoTestRepository;

    private final MemberRepository memberRepository;

    private final VideoTestBlockListRepository videoTestBlockListRepository;

    private final VideoTestAnswerRepository videoTestAnswerRepository;

    private final VideoTestLogRepository videoTestLogRepository;

    private final BlockCodeToJavaConverter blockCodeToJavaConverter;

    private final JavaCodeExecutor javaCodeExecutor = new JavaCodeExecutor();


    /*
    * 비디오 블록 코딩 문제 가져오기*/
    public List<VideoTestBlockListDTO.BlockResponseDTO> getBlockList(int memberId, VideoTest videoTest) {

        Preconditions.checkNotNull(videoTest, "등록된 문제가 아닙니다. (문제 ID : %s)", memberId);

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 문제의 보기 리스트 가져오기
        List<VideoTestBlockList> videoTestMultipleLists = videoTestBlockListRepository.findAllByVideoTest(videoTest);

        // 반환할 dto list 생성
        List<VideoTestBlockListDTO.BlockResponseDTO> blockResponseDTOList = videoTestMultipleLists.stream()
                .map(videoTestBlockList -> new VideoTestBlockListDTO.BlockResponseDTO(videoTestBlockList))
                .collect(Collectors.toList());

        return blockResponseDTOList;

    }


    /*
    * 답안 저장 후 답변 비교해서 채점하기
    */
    public Boolean saveAnswerAndGetScoring(Authentication authentication, VideoTestBlockListDTO.TestResultRequestDTO requestDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        VideoTest videoTest = videoTestRepository.findById(requestDTO.getVideoTestId());
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // 등록된 videoTest인지 검사
        if (videoTest == null) {
            throw new ResponseMessageException(ErrorCode.VIDEO_TEST_NOT_FOUND);
        }
        // block list null 검사
        if (requestDTO.getBlockList() == null) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

        log.info("들어온 블록리스트...");
        for(Block block : requestDTO.getBlockList()) {
            log.info(block.getBlock() + "  " + block.getValue());
        }

        // 학생 답안을 자바 코드로 변환
        String testString = blockCodeToJavaConverter.convertToJavaCode(requestDTO.getBlockList());

        log.info("변환된 자바 코드...");
        log.info(testString.toString());

        // 변환된 답안을 컴파일
        String testAnswerString = null;

        try{
            testAnswerString = javaCodeExecutor.executeJavaCode(testString);
        }catch(Exception e){
            e.printStackTrace();
            log.info("컴파일 에러");
        }

        log.info("컴파일된 답안...");
        log.info(testAnswerString.toString());

// 변환된 답안을 문제 답과 비교
        VideoTestAnswer videoTestAnswer = videoTestAnswerRepository.findByVideoTest(videoTest);
        log.info("정답...");
        log.info(videoTestAnswer.getAnswer());
        boolean scoring = true;

// 문자열의 공백을 제거하고 비교
        String formattedTestAnswer = testAnswerString.toString().trim();
        String formattedCorrectAnswer = videoTestAnswer.getAnswer().trim();

        if (formattedTestAnswer.equals(formattedCorrectAnswer)) {
            log.info("정답");
            scoring = true;
        } else {
            log.info("오답");
            scoring = false;
        }

        // 학생 답변 저장
        VideoTestLog videoTestLog = VideoTestLog.builder()
                .videoTest(videoTest)
                .member(member)
                .subAnswer(testAnswerString)
                .scoring(scoring)
                .build();
        videoTestLogRepository.save(videoTestLog);

        return scoring;

    }



}
