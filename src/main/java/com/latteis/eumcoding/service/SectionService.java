package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.SectionRepository;
import com.latteis.eumcoding.persistence.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    private final LectureRepository lectureRepository;

    private final VideoRepository videoRepository;

    private final VideoService videoService;

    private final MemberRepository memberRepository;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;

    // 섹션 추가
    public void addSection(int memberId, SectionDTO.AddRequestDTO addRequestDTO) {

        try {

            // 로그인된 사용자가 강의 작성자인지 검사
            int checkMemberId = lectureRepository.findMemberIdById(addRequestDTO.getLectureId());
            if (checkMemberId != memberId) {
                throw new RuntimeException("SectionService.addSection() : 로그인된 사용자는 강의 작성자가 아닙니다.");
            }

            Lecture lecture = lectureRepository.findById(addRequestDTO.getLectureId());

//            Section section = Section.builder()
            sectionRepository.save( Section.builder()
                            .lecture(lecture)
                            .timeTaken(addRequestDTO.getTimeTaken())
                            .name(addRequestDTO.getName())
                            .createdDay(LocalDateTime.now())
                            .sequence(addRequestDTO.getSequence())
                            .build());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SectionService.addSection() : 에러 발생");
        }

    }

    // TimeTaken 수정
    public void updateTimeTaken(int memberId, SectionDTO.TimeTakenRequestDTO timeTakenRequestDTO) {

        try {

            // 섹션 ID에 맞는 Entity 가져옴
            Section section = sectionRepository.findBySectionId(timeTakenRequestDTO.getId());
            // 로그인된 사용자가 강의 작성자인지 검사
            if (memberId != section.getLecture().getMember().getId()) {
                throw new RuntimeException("SectionService.updateTimeTaken() : 로그인된 사용자는 강의 작성자가 아닙니다.");
            }

            // 수정 후 저장
            section.setTimeTaken(timeTakenRequestDTO.getTimeTaken());
            sectionRepository.save(section);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SectionService.updateTimeTaken() : 에러 발생");
        }
    }

    // 섹션 이름 수정
    public void updateName(int memberId, SectionDTO.NameRequestDTO nameRequestDTO) {

        try {

            // 섹션 ID에 맞는 Entity 가져옴
            Section section = sectionRepository.findBySectionId(nameRequestDTO.getId());
            // 로그인된 사용자가 강의 작성자인지 검사
            if (memberId != section.getLecture().getMember().getId()) {
                throw new RuntimeException("SectionService.updateName() : 로그인된 사용자는 강의 작성자가 아닙니다.");
            }

            // 수정 후 저장
            section.setName(nameRequestDTO.getName());
            sectionRepository.save(section);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SectionService.updateName() : 에러 발생");
        }

    }



    // 섹션 리스트 가져오기
    public List<SectionDTO.ListResponseDTO> getSectionList(LectureDTO.IdRequestDTO idRequestDTO) {

        // lecture 가져오기
        Lecture lecture = lectureRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(lecture, "등록된 강의가 없습니다. (강의 ID : %s)", idRequestDTO.getId());

        // 해당 Lecture에 있는 모든 Section 가져옴
        List<Section> sectionObjects = sectionRepository.findAllByLectureOrderBySequence(lecture);
        // 섹션 DTO 리스트 생성
        List<SectionDTO.ListResponseDTO> listResponseDTOList = new ArrayList<>();
        // 섹션 반복문
        for (Section section : sectionObjects) {
            // SectionDTO에 object 저장
            SectionDTO.ListResponseDTO listResponseDTO = new SectionDTO.ListResponseDTO(section);
            // VideoDTOList 생성
            List<VideoDTO.SectionListDTO> videoDTOList = new ArrayList<>();
            // 해당 section에 있는 모든 Video 가져옴
            List<Video> videoList = videoRepository.findAllBySectionOrderBySequence(section);
            // 비디오 반복문
            for (Video video : videoList) {
                // videoDTO에 해당 object 저장
                VideoDTO.SectionListDTO videoDTO = new VideoDTO.SectionListDTO(video);
                videoDTO.setThumb(domain + port + "/eumCodingImgs/lecture/video/thumb/" + video.getThumb());
                // videoList에 저장
                videoDTOList.add(videoDTO);
            }
            // SectionDTO에 videoList 저장
            listResponseDTO.setVideoDTOList(videoDTOList);
            // SectionDTOList에 SectionDTO 저장
            listResponseDTOList.add(listResponseDTO);
        }

        // sectionDTOList 반환
        return listResponseDTOList;

    }

    // 섹션 순서 앞으로 이동
    public void updateSequenceUp(int memberId, SectionDTO.IdRequestDTO idRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // Section 가져오기
        Section section = sectionRepository.findByIdAndLectureMember(idRequestDTO.getId(), member);
        Preconditions.checkNotNull(section, "등록된 섹션이 아닙니다. (section ID : %s)", idRequestDTO.getId());

        // 강사 회원인지 검사
        Preconditions.checkArgument((member.getRole() == MemberDTO.MemberRole.TEACHER) || (member.getRole() == MemberDTO.MemberRole.ADMIN), "강사나 관리자 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 섹션의 앞 순서 섹션 가져오기
        Section frontSection = sectionRepository.findByLectureAndSequence(section.getLecture(), section.getSequence() - 1);
        Preconditions.checkNotNull(frontSection, "앞 순서 섹션이 없습니다. (section ID : %s)", idRequestDTO.getId());

        // 순서 + 1
        frontSection.setSequence(frontSection.getSequence() + 1);
        // 저장
        sectionRepository.save(frontSection);

        // 기존 섹션 순서 - 1
        section.setSequence(section.getSequence() - 1);
        // 저장
        sectionRepository.save(section);

    }

    /*
    * 섹션 순서 뒤로 이동
    */
    public void updateSequenceDown(int memberId, SectionDTO.IdRequestDTO idRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // Section 가져오기
        Section section = sectionRepository.findByIdAndLectureMember(idRequestDTO.getId(), member);
        Preconditions.checkNotNull(section, "등록된 섹션이 아닙니다. (section ID : %s)", idRequestDTO.getId());

        // 강사 회원인지 검사
        Preconditions.checkArgument((member.getRole() == MemberDTO.MemberRole.TEACHER) || (member.getRole() == MemberDTO.MemberRole.ADMIN), "강사나 관리자 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 섹션의 앞 순서 섹션 가져오기
        Section backSection = sectionRepository.findByLectureAndSequence(section.getLecture(), section.getSequence() + 1);
        Preconditions.checkNotNull(backSection, "뒷 순서 섹션이 없습니다. (section ID : %s)", idRequestDTO.getId());

        // 순서 - 1
        backSection.setSequence(backSection.getSequence() - 1);
        // 저장
        sectionRepository.save(backSection);

        // 기존 섹션 순서 - 1
        section.setSequence(section.getSequence() + 1);
        // 저장
        sectionRepository.save(section);

    }

    /*
    * 섹션 삭제
    */
    public void deleteSection(int memberId, SectionDTO.IdRequestDTO idRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // Section 가져오기
        Section section = sectionRepository.findByIdAndLectureMember(idRequestDTO.getId(), member);
        Preconditions.checkNotNull(section, "등록된 섹션이 아닙니다. (section ID : %s)", idRequestDTO.getId());

        // 강사 회원인지 검사
        Preconditions.checkArgument((member.getRole() == MemberDTO.MemberRole.TEACHER) || (member.getRole() == MemberDTO.MemberRole.ADMIN), "강사나 관리자 회원이 아닙니다. (회원 ID : %s)", memberId);

        List<Video> videoList = videoRepository.findBySectionId(section.getId());
        videoList.forEach(video -> videoService.deleteVideo(memberId, new VideoDTO.IdRequestDTO(video.getId())));

        List<Section> sectionList = sectionRepository.findAllByLectureAndSequenceGreaterThan(section.getLecture(), section.getSequence());
        sectionList.forEach(section1 -> {
            section1.setSequence(section1.getSequence() - 1);
            sectionRepository.save(section1);
        });

        sectionRepository.delete(section);

    }
}
