package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.LectureStudentDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureStudentService {

    private final MemberRepository memberRepository;

    private final LectureRepository lectureRepository;

    private final LectureProgressRepository lectureProgressRepository;

    private final VideoRepository videoRepository;

    private final VideoProgressRepository videoProgressRepository;

    private final SectionRepository sectionRepository;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;


    // 학생 목록 가져오기
    public List<LectureStudentDTO.ListResponseDTO> getStudentList(int memberId, LectureStudentDTO.ListRequestDTO listRequestDTO, Pageable pageable) {

        // 받아온 memberId에 맞는 MemberEntity 가져옴
        Member member = memberRepository.findByMemberId(memberId);
        // lectureId와 member에 맞는 lectureEntity 가져옴
        Lecture lecture = lectureRepository.findByIdAndMember(listRequestDTO.getLectureId(), member);
        // 아무 것도 못 가져왔다면 로그인 에러
        if (lecture == null) {
            throw new RuntimeException("LectureStudentService.getStudentList() : 로그인된 아이디는 해당 강좌의 강사가 아닙니다.");
        }

        // 해당 강의의 모든 동영상 갯수
        long totalVideoCount = videoRepository.countByLectureId(lecture.getId());
        // progress 제외한 학생dto 리스트 가져옴
        Page<Object[]> pageObjects = lectureProgressRepository.getStudentList(lecture.getId(), pageable);
        // 학생 DTO List 생성
        List<LectureStudentDTO.ListResponseDTO> listResponseDTOList = new ArrayList<>();
        // 반복문
        for (Object[] object : pageObjects) {
            // 학생 DTO 생성
            LectureStudentDTO.ListResponseDTO listResponseDTO = new LectureStudentDTO.ListResponseDTO(object);
            // 시청 완료한 비디오 갯수 가져옴
            long videoProgressCount = videoProgressRepository.countByMemberIdAndLectureId(listResponseDTO.getMemberId(), lecture.getId(), 1);
            // 총 몇 퍼센트 들었는지 계산
            double progress = ((double) videoProgressCount / (double) totalVideoCount) * 100;
            // 소수점 첫번째 자리에서 반올림 후 학생 DTO에 progress 저장
            listResponseDTO.setProgress((int) Math.round(progress));
            // 학생 DTO List에 학생 DTO 저장
            listResponseDTOList.add(listResponseDTO);
        }

        return listResponseDTOList;

    }


    /*
    * 학생 정보 가져오기
    */
    public LectureStudentDTO.InfoResponseDTO getStudentInfo(int memberId, LectureStudentDTO.InfoRequestDTO infoRequestDTO) {

        // 등록된 강사인지 검사
        Member teacher = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(teacher, "등록된 강사가 아닙니다. (강사 ID : %s)", memberId);

        // 등록된 강의인지 검사
        Lecture lecture = lectureRepository.findById(infoRequestDTO.getLectureId());
        Preconditions.checkNotNull(lecture, "등록된 강의가 아닙니다. (강의 ID : %s)", infoRequestDTO.getLectureId());

        // 해당 강의 소유자인지 검사
        Preconditions.checkArgument(teacher == lecture.getMember(), "강의 소유자가 아닙니다. (회원 ID : %s)", memberId);

        // 등록된 학생인지 검사
        Member student = memberRepository.findByMemberId(infoRequestDTO.getMemberId());
        Preconditions.checkNotNull(student, "등록된 학생이 아닙니다. (학생 ID : %s)", infoRequestDTO.getMemberId());

        LectureProgress lectureProgress = lectureProgressRepository.findByMemberAndLecture(student, lecture);
        Preconditions.checkNotNull(lectureProgress, "해당 강의를 수강중인 학생이 아닙니다. (학생 ID : %s)", student.getId());

        LectureStudentDTO.InfoResponseDTO infoResponseDTO = new LectureStudentDTO.InfoResponseDTO();
        infoResponseDTO.setNickname(memberRepository.getNicknameByMember(student));
        infoResponseDTO.setStartDay(lectureProgress.getStartDay());
        infoResponseDTO.setEndDay(lectureProgress.getEndDay());

        // 해당 강의의 모든 동영상 갯수
        long totalVideoCount = videoRepository.countByLectureId(lecture.getId());
        // 시청 완료한 비디오 갯수 가져옴
        long videoProgressCount = videoProgressRepository.countByMemberIdAndLectureId(student.getId(), lecture.getId(), 1);
        // 총 몇 퍼센트 들었는지 계산
        double progress = ((double) videoProgressCount / (double) totalVideoCount) * 100;

        infoResponseDTO.setProgress((int) Math.round(progress));

        // 학생이 시청한 기록이 있는 영상의 섹션들만 가져오기
        List<Section> sectionList = sectionRepository.getLectureStudentSection(student);
        List<LectureStudentDTO.SectionListResponseDTO> sectionDTOList = new ArrayList<>();
        for (Section section : sectionList) {
            // section dto에 담기
            LectureStudentDTO.SectionListResponseDTO sectionDTO = new LectureStudentDTO.SectionListResponseDTO(section);

            // 해당 section에 있는 모든 Video 가져옴
            List<Video> videoList = videoRepository.findAllBySectionOrderBySequence(section);
            List<LectureStudentDTO.VideoListDTO> videoListDTOS = new ArrayList<>();
            for (Video video : videoList) {
                // video dto에 담기
                LectureStudentDTO.VideoListDTO videoDTO = new LectureStudentDTO.VideoListDTO(video);
                videoDTO.setThumb(domain + port + "/eumCodingImgs/lecture/video/thumb/" + video.getThumb());
                // videoList에 저장
                videoListDTOS.add(videoDTO);
            }
            // 섹션 dto에 비디오 리스트 담기
            sectionDTO.setVideoDTOList(videoListDTOS);
            // 섹션 dto list에 섹션 dto담기
            sectionDTOList.add(sectionDTO);
        }

        // 반환할 dto에 sectionDTOList 담기
        infoResponseDTO.setSectionListResponseDTOS(sectionDTOList);

        return infoResponseDTO;
    }

}
