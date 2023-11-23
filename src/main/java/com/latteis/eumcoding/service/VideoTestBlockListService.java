package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoTestBlockListDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestAnswer;
import com.latteis.eumcoding.model.VideoTestBlockList;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.VideoTestAnswerRepository;
import com.latteis.eumcoding.persistence.VideoTestBlockListRepository;
import com.latteis.eumcoding.persistence.VideoTestRepository;
import com.latteis.eumcoding.util.blockCoding.Block;
import com.latteis.eumcoding.util.blockCoding.BlockCodeToJavaConverter;
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

    private final BlockCodeToJavaConverter blockCodeToJavaConverter;


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
    * 답안과 답변 비교해서 채점하기
    */
    public Boolean getBlockTestResult(Authentication authentication, VideoTestBlockListDTO.TestResultRequestDTO requestDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        Member tester = memberRepository.findByMemberId(requestDTO.getTestMemberId());
        VideoTest videoTest = videoTestRepository.findById(requestDTO.getVideoTestId());
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // tester가 등록된 회원인지 검사
        if (tester == null) {
            throw new ResponseMessageException(ErrorCode.TEST_MEMBER_UNREGISTERED);
        }
        // 등록된 videoTest인지 검사
        if (videoTest == null) {
            throw new ResponseMessageException(ErrorCode.VIDEO_TEST_NOT_FOUND);
        }
        // block list null 검사
        if (requestDTO.getBlockList() == null) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

        // 학생 답안을 자바 코드로 변환
        String testAnswerString = blockCodeToJavaConverter.convertToJavaCode(requestDTO.getBlockList());
        // 변환된 답안을 문제 답과 비교
        VideoTestAnswer videoTestAnswer = videoTestAnswerRepository.findByVideoTest(videoTest);

        if (testAnswerString.equals(videoTestAnswer.getAnswer())) {
            return true;
        }
        else {
            return false;
        }

    }



}
