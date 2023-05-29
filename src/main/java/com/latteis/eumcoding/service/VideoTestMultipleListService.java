package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.dto.VideoTestMultipleListDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Video;
import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestMultipleList;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.VideoTestMultipleListRepository;
import com.latteis.eumcoding.persistence.VideoTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTestMultipleListService {

    private final VideoTestMultipleListRepository videoTestMultipleListRepository;

    private final VideoTestRepository videoTestRepository;

    private final MemberRepository memberRepository;

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

        // 해당 문제의 보기 리스트 가져오기
        List<VideoTestMultipleList> videoTestMultipleLists = videoTestMultipleListRepository.findAllByVideoTestOrderBySequence(videoTest);
        Preconditions.checkNotNull(videoTestMultipleLists, "해당 문제에 등록된 보기가 없습니다. (동영상 문제 ID : %s)", videoTest.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = videoTest.getVideo().getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", videoTest.getVideo().getSection().getLecture().getId(), lectureUploader, memberId);

        // 반환할 dto list 생성
         List<VideoTestMultipleListDTO.ListResponseDTO> listResponseDTOs = new ArrayList<>();
         // entity를 dto에 담고 dtoList에 담기
         for (VideoTestMultipleList videoTestMultipleList : videoTestMultipleLists) {
             VideoTestMultipleListDTO.ListResponseDTO listResponseDTO = new VideoTestMultipleListDTO.ListResponseDTO(videoTestMultipleList);
             listResponseDTOs.add(listResponseDTO);
         }
         return listResponseDTOs;

    }

}
