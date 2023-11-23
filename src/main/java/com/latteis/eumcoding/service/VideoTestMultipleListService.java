package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.dto.VideoTestMultipleListDTO;
import com.latteis.eumcoding.dto.stats.StatsDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTestMultipleListService {

    private final VideoTestMultipleListRepository videoTestMultipleListRepository;

    private final VideoTestRepository videoTestRepository;

    private final MemberRepository memberRepository;

    private final VideoTestLogRepository videoTestLogRepository;

    private final VideoTestAnswerRepository videoTestAnswerRepository;

    // 객관식 문제 보기 추가
    public void add(int memberId, VideoTestMultipleListDTO.AddDTO addDTO) {

        // 문제 정보 가져오기
        VideoTest videoTest = videoTestRepository.findById(addDTO.getVideoTestId());
        Preconditions.checkNotNull(videoTest, "등록된 동영상 문제가 없습니다. (동영상 문제 ID : %s)", addDTO.getVideoTestId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTest.getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTest.getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        // 해당 문제의 보기 개수 가져오기
        long count = videoTestMultipleListRepository.countByVideoTestId(videoTest.getId());

        VideoTestMultipleList videoTestMultipleList = VideoTestMultipleList.builder()
                .videoTest(videoTest)
                .content(addDTO.getContent())
                .sequence((int) count)
                .build();
        videoTestMultipleListRepository.save(videoTestMultipleList);

    }

    // 동영상 객관식 문제 보기 수정
    public void update(int memberId, VideoTestMultipleListDTO.UpdateRequestDTO updateRequestDTO) {

        // 문제 정보 가져오기
        VideoTestMultipleList videoTestMultipleList = videoTestMultipleListRepository.findById(updateRequestDTO.getId());
        Preconditions.checkNotNull(videoTestMultipleList, "등록된 동영상 문제 보기가 없습니다. (동영상 문제 보기 ID : %s)", updateRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTestMultipleList.getVideoTest().getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTestMultipleList.getVideoTest().getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        videoTestMultipleList.setContent(updateRequestDTO.getContent());
        videoTestMultipleListRepository.save(videoTestMultipleList);

    }

    // 객관식 문제 보기 삭제
    public void delete(int memberId, VideoTestMultipleListDTO.IdRequestDTO idRequestDTO) {

        // 문제 정보 가져오기
        VideoTestMultipleList videoTestMultipleList = videoTestMultipleListRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(videoTestMultipleList, "등록된 동영상 문제 보기가 없습니다. (동영상 문제 보기 ID : %s)", idRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTestMultipleList.getVideoTest().getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTestMultipleList.getVideoTest().getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        // 삭제
        videoTestMultipleListRepository.delete(videoTestMultipleList);

        // 해당 문제의 보기 리스트 가져오기
        List<VideoTestMultipleList> videoTestMultipleLists = videoTestMultipleListRepository.findAllByVideoTestOrderBySequence(videoTestMultipleList.getVideoTest());

        // 순서 재정리
        int sequence = 0;
        for (VideoTestMultipleList entity : videoTestMultipleLists) {
            entity.setSequence(sequence);
            videoTestMultipleListRepository.save(entity);
            sequence++;
        }

    }

    // 객관식 문제 보기 리스트 가져오기
    public List<VideoTestMultipleListDTO.ListResponseDTO> getList(int memberId, VideoTest videoTest) {

        Preconditions.checkNotNull(videoTest, "등록된 문제가 아닙니다. (문제 ID : %s)", memberId);

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 문제의 보기 리스트 가져오기
        List<VideoTestMultipleList> videoTestMultipleLists = videoTestMultipleListRepository.findAllByVideoTestOrderBySequence(videoTest);

        // 반환할 dto list 생성
         List<VideoTestMultipleListDTO.ListResponseDTO> listResponseDTOs = new ArrayList<>();
         // entity를 dto에 담고 dtoList에 담기
         for (VideoTestMultipleList videoTestMultipleList : videoTestMultipleLists) {
             VideoTestMultipleListDTO.ListResponseDTO listResponseDTO = new VideoTestMultipleListDTO.ListResponseDTO(videoTestMultipleList);
             listResponseDTOs.add(listResponseDTO);
         }
         return listResponseDTOs;

    }

    /*
     * 객관식 테스트 결과 가져오기
     * 정답이면 true 아니면 false
     */
    public Boolean getTestResult(Authentication authentication, VideoTestMultipleListDTO.TestResultRequestDTO requestDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);
        Member tester = memberRepository.findByMemberId(requestDTO.getTestMemberId());
        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }
        // tester가 등록된 회원인지 검사
        if (tester == null) {
            throw new ResponseMessageException(ErrorCode.TEST_MEMBER_UNREGISTERED);
        }
        VideoTest videoTest = videoTestRepository.findById(requestDTO.getVideoTestId());
        // 등록된 videoTest인지 검사
        if (videoTest == null) {
            throw new ResponseMessageException(ErrorCode.VIDEO_TEST_NOT_FOUND);
        }

        // 해당 문제에 답한 기록 전부 가져오기
        List<VideoTestLog> videoTestLogList = videoTestLogRepository.findAllByVideoTestAndMember(videoTest, tester);
        // 해당 문제 답변 전부 가져오기
        List<VideoTestAnswer> videoTestAnswerList = videoTestAnswerRepository.findAllByVideoTest(videoTest);

        // 리스트 크기가 다르면 flase
        if (videoTestLogList.size() != videoTestAnswerList.size()) {
            return false;
        }

        // 답안으로 리스트 정렬
        Collections.sort(videoTestLogList ,Comparator.comparing(VideoTestLog::getSubAnswer, Comparator.naturalOrder()));
        Collections.sort(videoTestAnswerList ,Comparator.comparing(VideoTestAnswer::getAnswer, Comparator.naturalOrder()));

        // 리스트 비교해서 다르면 false 리턴
        for (int i = 0; i < videoTestAnswerList.size(); i++) {
            if (!videoTestAnswerList.get(i).getAnswer().equals(videoTestLogList.get(i).getSubAnswer())) {
                return false;
            }
        }

        // 반복문 통과하면 true
        return true;

    }
}
