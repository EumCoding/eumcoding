package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.SectionRepository;
import com.latteis.eumcoding.persistence.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        try {

            // 해당 Lecture에 있는 모든 Section 가져옴
            List<Object[]> sectionObjects = sectionRepository.findListByLecture(idRequestDTO.getId());
            // 섹션 DTO 리스트 생성
            List<SectionDTO.ListResponseDTO> listResponseDTOList = new ArrayList<>();
            // 섹션 반복문
            for (Object[] object : sectionObjects) {
                // SectionDTO에 object 저장
                SectionDTO.ListResponseDTO listResponseDTO = new SectionDTO.ListResponseDTO(object);
                // VideoDTOList 생성
                List<VideoDTO.SectionListDTO> videoDTOList = new ArrayList<>();
                // 해당 section에 있는 모든 Video 가져옴
                List<Object[]> videoObjects = videoRepository.getSectionList((int)object[0]);
                // 비디오 반복문
                for (Object[] videoObject : videoObjects) {
                    // videoDTO에 해당 object 저장
                    VideoDTO.SectionListDTO videoDTO = new VideoDTO.SectionListDTO(videoObject);
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

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SectionService.getSectionList() : 에러 발생");
        }

    }

}
