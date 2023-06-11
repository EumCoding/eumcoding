package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoTestBlockListDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestBlockList;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.VideoTestBlockListRepository;
import com.latteis.eumcoding.persistence.VideoTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
