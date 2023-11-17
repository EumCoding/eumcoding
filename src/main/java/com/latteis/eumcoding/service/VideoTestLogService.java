package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.dto.VideoTestLogDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.VideoProgressRepository;
import com.latteis.eumcoding.persistence.VideoTestLogRepository;
import com.latteis.eumcoding.persistence.VideoTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTestLogService {

    private final VideoTestLogRepository videoTestLogRepository;

    private final MemberRepository memberRepository;

    private final VideoTestRepository videoTestRepository;

    private final VideoProgressRepository videoProgressRepository;

    // 동영상 테스트 로그 추가
    public void addTestLog(int memberId, VideoTestLogDTO.AddRequestDTO addRequestDTO) {
        try{
            // 동영상 문제 정보 가져오기
            VideoTest videoTest = videoTestRepository.findById(addRequestDTO.getVideoTestId());
            Preconditions.checkNotNull(videoTest, "등록된 문제가 없습니다. (문제 ID : %s)", addRequestDTO.getVideoTestId());

            // 등록된 회원인지 검사
            Member member = memberRepository.findByMemberId(memberId);
            Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

            // 해당 동영상을 시청 중인지 검사
            VideoProgress videoProgress = videoProgressRepository.findByMemberAndVideo(member, videoTest.getVideo());
            Preconditions.checkNotNull(videoProgress, "해당 학생이 해당 동영상을 시청하고 있지 않습니다. (학생 ID: %s, 동영상 ID: %s)", memberId, videoTest.getVideo().getId());

            // 이미 해당 문제에 대한 로그가 존재하는지 검사
            Preconditions.checkArgument(!videoTestLogRepository.existsByVideoTestAndMember(videoTest, member), "이미 해당 문제에 대한 답변을 제출했습니다. (동영상 문제 ID : %s)", videoTest.getId());

            VideoTestLog videoTestLog = VideoTestLog.builder()
                    .videoTest(videoTest)
                    .member(member)
                    .subAnswer(addRequestDTO.getSubAnswer())
                    .build();
            videoTestLogRepository.save(videoTestLog);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 동영상 테스트 로그 가져오기
    public VideoTestLogDTO.ResponseDTO getTestLog(int memberId, VideoTestLogDTO.InfoRequestDTO infoRequestDTO) {

        // 동영상 테스트 정보 가져오기
        VideoTest videoTest = videoTestRepository.findById(infoRequestDTO.getVideoTestId());
        log.info("ff");
//        Preconditions.checkNotNull(videoTest, "등록된 동영상 문제가 없습니다. (동영상 문제 ID : %s)", infoRequestDTO.getVideoTestId());

        // 동영상 테스트 로그 정보 가져오기
        VideoTestLog videoTestLog = videoTestLogRepository.findByVideoTestAndMemberId(videoTest ,infoRequestDTO.getMemberId());
//        Preconditions.checkNotNull(videoTestLog, "등록된 테스트 로그가 없습니다. (동영상 문제 ID : %s, 문제 답변 작성자 ID : %s)", videoTest.getId(), infoRequestDTO.getMemberId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
//        int lectureUploader = videoTest.getVideo().getSection().getLecture().getMember().getId();
//        Preconditions.checkArgument(memberId == lectureUploader || memberId == infoRequestDTO.getMemberId(), "해당 정보에 접근할 권한이 없습니다. (현재 회원 ID: %s)", memberId);

        if (videoTestLog == null) return new VideoTestLogDTO.ResponseDTO();
        return new VideoTestLogDTO.ResponseDTO(videoTestLog);

    }
}
