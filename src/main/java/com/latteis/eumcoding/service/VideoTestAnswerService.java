package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoTestAnswerDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestAnswer;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.VideoTestAnswerRepository;
import com.latteis.eumcoding.persistence.VideoTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTestAnswerService {

    private final VideoTestAnswerRepository videoTestAnswerRepository;

    private final VideoTestRepository videoTestRepository;

    private final MemberRepository memberRepository;


    // 문제 답안 저장
    public void addTestAnswer(int memberId, VideoTestAnswerDTO.AddDTO addDTO) {

        // 문제 정보 가져오기
        VideoTest videoTest = videoTestRepository.findById(addDTO.getVideoTestId());
        Preconditions.checkNotNull(videoTest, "등록된 동영상 문제가 없습니다. (동영상 문제 ID : %s)", addDTO.getVideoTestId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTest.getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTest.getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        VideoTestAnswer videoTestAnswer = VideoTestAnswer.builder()
                .videoTest(videoTest)
                .answer(addDTO.getAnswer())
                .build();
        videoTestAnswerRepository.save(videoTestAnswer);

    }

    // 문제 답안 수정
    public void updateTestAnswer(int memberId, VideoTestAnswerDTO.UpdateRequestDTO updateRequestDTO) {

        // 문제 답변 정보 가져오기
        VideoTestAnswer videoTestAnswer = videoTestAnswerRepository.findById(updateRequestDTO.getId());
        Preconditions.checkNotNull(videoTestAnswer, "등록된 동영상 문제 답변이 없습니다. (동영상 문제 답변 ID : %s)", updateRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTestAnswer.getVideoTest().getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTestAnswer.getVideoTest().getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        videoTestAnswer.setAnswer(updateRequestDTO.getAnswer());
        videoTestAnswerRepository.save(videoTestAnswer);

    }

    // 답변 응답
    public VideoTestAnswerDTO.ResponseDTO getAnswer(int memberId, VideoTest videoTest) {

        Preconditions.checkNotNull(videoTest, "등록된 문제가 아닙니다. (문제 ID : %s)", memberId);

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 문제 답변 정보 가져오기
        VideoTestAnswer videoTestAnswer = videoTestAnswerRepository.findByVideoTest(videoTest);

        VideoTestAnswerDTO.ResponseDTO responseDTO = (videoTestAnswer == null) ? null : new VideoTestAnswerDTO.ResponseDTO(videoTestAnswer);
        return responseDTO;

    }
}
