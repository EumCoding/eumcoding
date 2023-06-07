package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.dto.VideoTestAnswerDTO;
import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.dto.VideoTestMultipleListDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Video;
import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestAnswer;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.VideoRepository;
import com.latteis.eumcoding.persistence.VideoTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTestService {

    private final VideoTestRepository videoTestRepository;

    private final VideoRepository videoRepository;

    private final MemberRepository memberRepository;

    private final VideoTestMultipleListService videoTestMultipleListService;

    private final VideoTestAnswerService videoTestAnswerService;

    // 동영상 문제 추가
    public void addTest(int memberId, VideoTestDTO.AddRequestDTO addRequestDTO) {

        // 동영상 정보 가져오기
        Video video = videoRepository.findById(addRequestDTO.getVideoId());
        Preconditions.checkNotNull(video, "등록된 동영상이 없습니다. (동영상 ID : %s)", addRequestDTO.getVideoId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = video.getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", video.getSection().getLecture().getId(), lectureUploader, memberId);

        // 받아온 시간이 유효하거나 겹치지 않는지 검사
        LocalTime testTime = LocalTime.parse(addRequestDTO.getTestTime());

        Preconditions.checkArgument(testTime != null && testTime.getSecond() > 0,"알맞은 문제 시간을 입력하세요.");
        Preconditions.checkArgument(!videoTestRepository.existsByVideoIdAndTestTime(video.getId(), testTime),"입력한 시간에 존재하는 문제가 있습니다.");

        // videoTest 저장
        VideoTest videoTest = VideoTest.builder()
                .video(video)
                .testTime(testTime)
                .type(addRequestDTO.getType())
                .title(addRequestDTO.getTitle())
                .score(addRequestDTO.getScore())
                .build();
        videoTestRepository.save(videoTest);

        // 객관식 문제라면 실행
        if (videoTest.getType() == VideoTestDTO.VideoTestType.MULTIPLE_CHOICE) {

            // 객관식 문제 보기 저장
            for (VideoTestMultipleListDTO.AddRequestDTO videoTestMultipleListDTO : addRequestDTO.getVideoTestMultipleList()) {

                VideoTestMultipleListDTO.AddDTO addDTO = new VideoTestMultipleListDTO.AddDTO();
                addDTO.setVideoTestId(videoTest.getId());
                addDTO.setContent(videoTestMultipleListDTO.getContent());
                videoTestMultipleListService.add(memberId, addDTO);

            }

        }
        // 코드 블럭 문제라면 실행
        else if (videoTest.getType() == VideoTestDTO.VideoTestType.CODE_BLOCK) {

            log.info("코드 블럭 저장");

        }

        // 문제 답안 저장
        VideoTestAnswerDTO.AddDTO addAnswerDTO = new VideoTestAnswerDTO.AddDTO();
        addAnswerDTO.setVideoTestId(videoTest.getId());
        addAnswerDTO.setAnswer(addRequestDTO.getTestAnswerDTO().getAnswer());
        videoTestAnswerService.addTestAnswer(memberId, addAnswerDTO);

    }

    // 동영상 문제 수정
    public void updateTest(int memberId, VideoTestDTO.UpdateRequestDTO updateRequestDTO) {

        // 동영상 문제 정보 가져오기
        VideoTest videoTest = videoTestRepository.findById(updateRequestDTO.getId());
        Preconditions.checkNotNull(videoTest, "등록된 문제가 없습니다. (문제 ID : %s)", updateRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTest.getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTest.getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        // 받아온 시간이 유효하거나 겹치지 않는지 검사
        LocalTime testTime = LocalTime.parse(updateRequestDTO.getTestTime());

        Preconditions.checkArgument(testTime != null && testTime.getSecond() > 0,"알맞은 문제 시간을 입력하세요.");
        Preconditions.checkArgument(!videoTestRepository.existsByVideoIdAndTestTime(videoTest.getVideo().getId(), testTime),"입력한 시간에 존재하는 문제가 있습니다.");

        videoTest.setTestTime(testTime);
        videoTest.setTitle(updateRequestDTO.getTitle());
        videoTest.setScore(updateRequestDTO.getScore());

        videoTestRepository.save(videoTest);

    }

    // 동영상 문제 삭제
    public void deleteTest(int memberId, VideoTestDTO.IdRequestDTO idRequestDTO) {

        // 동영상 문제 가져오기
        VideoTest videoTest = videoTestRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(videoTest, "등록된 문제가 없습니다. (문제 ID : %s)", idRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTest.getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTest.getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        videoTestRepository.delete(videoTest);

    }

    // 동영상 문제 리스트
    public List<VideoTestDTO.ListResponseDTO> getTestList(int memberId, VideoDTO.IdRequestDTO idRequestDTO) {

        // 동영상 정보 가져오기
        Video video = videoRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(video, "등록된 동영상이 없습니다. (동영상 ID : %s)", idRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 비디오에 있는 문제 리스트 가져오기
        List<VideoTest> videoTestList = videoTestRepository.findAllByVideoOrderByTestTime(video);
        List<VideoTestDTO.ListResponseDTO> listResponseDTOList = new ArrayList<>();
        // 반복 작업
        for (VideoTest videoTest : videoTestList) {

            // dto에 담기
            VideoTestDTO.ListResponseDTO listResponseDTO = new VideoTestDTO.ListResponseDTO(videoTest);

            // 테스트 타입이 객관식이라면
            if (videoTest.getType() == VideoTestDTO.VideoTestType.MULTIPLE_CHOICE) {
                // 동영상 객관식 문제 보기 리스트 가져와서 videoTestDTO에 담기
                List<VideoTestMultipleListDTO.ListResponseDTO> multipleList = videoTestMultipleListService.getList(memberId, videoTest);
                listResponseDTO.setVideoTestMultipleListDTOs(multipleList);
            }

            // 문제 답안 가져와서 videoTestDTO에 담기
            VideoTestAnswerDTO.ResponseDTO videoTestAnswerDTO = videoTestAnswerService.getAnswer(memberId, videoTest);
            listResponseDTO.setTestAnswerDTO(videoTestAnswerDTO);
            // 반환할 video test dto list에 추가
            listResponseDTOList.add(listResponseDTO);

        }

        return listResponseDTOList;
    }

}
