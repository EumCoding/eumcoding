package com.latteis.eumcoding.service;


import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.PayLecture;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.PayLectureRepository;
import com.latteis.eumcoding.util.MultipartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    private final PayLectureRepository payLectureRepository;

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @Value("${file.path.lecture.image}")
    private String imageFilePath;

    @Value("${file.path.lecture.thumb}")
    private String thumbFilePath;

    @Value("${file.path.lecture.badge}")
    private String badgeFilePath;

    public File getImageDirectoryPath() {
        File file = new File(imageFilePath);
        file.mkdirs();

        return file;
    }

    public File getThumbDirectoryPath() {
        File file = new File(thumbFilePath);
        file.mkdirs();

        return file;
    }

    public File getBadgeDirectoryPath() {
        File file = new File(badgeFilePath);
        file.mkdirs();

        return file;
    }

    // 강의 생성
    public void createLecture(int memberId,
                              LectureDTO.CreateRequestDTO createRequestDTO,
                              List<MultipartFile> image,
                              List<MultipartFile> thumb,
                              List<MultipartFile> badge) {

        try{

            Member member = memberRepository.findByMemberId(memberId);

            Lecture lecture = Lecture.builder()
                    .member(member)
                    .name(createRequestDTO.getName())
                    .description(createRequestDTO.getDescription())
                    .price(createRequestDTO.getPrice())
                    .grade(createRequestDTO.getGrade())
                    .createdDay(LocalDateTime.now())
                    .state(createRequestDTO.getState())
                    .build();
            lectureRepository.save(lecture);

            // 강의 설명 이미지가 있을 경우
            if (image != null && !image.isEmpty()) {

                // 이미지 한개만 저장
                MultipartFile multipartFile = image.get(0);

                // 이미지 저장
                File newFile = saveLectureImage(lecture.getImage() ,lecture.getId(), getImageDirectoryPath(), multipartFile);

                // 이미지 파일명 DB에 저장
                lecture.setImage(newFile.getName());

            }

            // 강의 썸네일 이미지가 있을 경우
            if (thumb != null && !thumb.isEmpty()) {

                // 이미지 한개만 저장
                MultipartFile multipartFile = thumb.get(0);

                // 이미지 저장
                File newFile = saveLectureImage(lecture.getThumb(), lecture.getId(), getThumbDirectoryPath(), multipartFile);

                // 이미지 파일명 DB에 저장
                lecture.setThumb(newFile.getName());

            }

            // 강의 뱃지 이미지가 있을 경우
            if (badge != null && !badge.isEmpty()) {

                // 이미지 한개만 저장
                MultipartFile multipartFile = badge.get(0);

                // 이미지 저장
                File newFile = saveLectureImage(lecture.getBadge(), lecture.getId(), getBadgeDirectoryPath(), multipartFile);

                // 이미지 파일명 DB에 저장
                lecture.setBadge(newFile.getName());

            }

            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.createLecture() : 에러 발생");
        }

    }

    //강의를 결제한 학생 수 구하기
    public int getTotalStudentsByLectureId(int lectureId) {
        List<PayLecture> paymentLectures = payLectureRepository.findByLectureIdAndState(lectureId, 0);
        return paymentLectures.size();
    }

    // 강의 상태 수정
    public void updateState(int memberId, LectureDTO.StateRequestDTO stateRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(stateRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setState(stateRequestDTO.getState());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateState() : 에러 발생");
        }
    }

    // 강의명 수정
    public void updateName(int memberId, LectureDTO.NameRequestDTO nameRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(nameRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setName(nameRequestDTO.getName());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateName() : 에러 발생");
        }
    }

    // 강의 설명 수정
    public void updateDescription(int memberId, LectureDTO.DescriptionRequestDTO descriptionRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(descriptionRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setDescription(descriptionRequestDTO.getDescription());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateDescription() : 에러 발생");
        }
    }

    // 강의 학년 수정
    public void updateGrade(int memberId, LectureDTO.GradeRequestDTO gradeRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(gradeRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setGrade(gradeRequestDTO.getGrade());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateGrade() : 에러 발생");
        }
    }

    // 강의 가격 수정
    public void updatePrice(int memberId, LectureDTO.PriceRequestDTO priceRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(priceRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setPrice(priceRequestDTO.getPrice());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updatePrice() : 에러 발생");
        }
    }

    // 강의 설명 이미지 수정
    public void updateImage(int memberId, LectureDTO.IdRequestDTO idRequestDTO, List<MultipartFile> image) {

        Lecture lecture = lectureRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
        memberService.chkIfEntityIsEmpty(lecture);

        // 강의 설명 이미지가 있을 경우
        if (image != null && !image.isEmpty()) {

            // 이미지 한개만 저장
            MultipartFile multipartFile = image.get(0);

            // 이미지 저장
            File newFile = saveLectureImage(lecture.getImage() ,lecture.getId(), getImageDirectoryPath(), multipartFile);

            // 이미지 파일명 DB에 저장
            lecture.setImage(newFile.getName());

            lectureRepository.save(lecture);

        } else {
            Preconditions.checkNotNull(image, "받아온 이미지가 없습니다.");
        }

    }

    // 강의 썸네일 이미지 수정
    public void updateThumb(int memberId, LectureDTO.IdRequestDTO idRequestDTO, List<MultipartFile> thumb) {

        Lecture lecture = lectureRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
        memberService.chkIfEntityIsEmpty(lecture);

        // 강의 설명 이미지가 있을 경우
        if (thumb != null && !thumb.isEmpty()) {

            // 이미지 한개만 저장
            MultipartFile multipartFile = thumb.get(0);

            // 이미지 저장
            File newFile = saveLectureImage(lecture.getThumb() ,lecture.getId(), getThumbDirectoryPath(), multipartFile);

            // 이미지 파일명 DB에 저장
            lecture.setThumb(newFile.getName());

            lectureRepository.save(lecture);

        } else {
            Preconditions.checkNotNull(thumb, "받아온 이미지가 없습니다.");
        }

    }

    // 강의 썸네일 이미지 수정
    public void updateBadge(int memberId, LectureDTO.IdRequestDTO idRequestDTO, List<MultipartFile> badge) {

        Lecture lecture = lectureRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
        memberService.chkIfEntityIsEmpty(lecture);

        // 강의 설명 이미지가 있을 경우
        if (badge != null && !badge.isEmpty()) {

            // 이미지 한개만 저장
            MultipartFile multipartFile = badge.get(0);

            // 이미지 저장
            File newFile = saveLectureImage(lecture.getBadge() ,lecture.getId(), getBadgeDirectoryPath(), multipartFile);

            // 이미지 파일명 DB에 저장
            lecture.setBadge(newFile.getName());

            lectureRepository.save(lecture);

        } else {
            Preconditions.checkNotNull(badge, "받아온 이미지가 없습니다.");
        }

    }

    // 내가 등록한 강의 리스트 가져오기
    public List<LectureDTO.MyListResponseDTO> getMyUploadList(int memberId, Pageable pageable) {

        try {

            // 내가 등록한 강의 리스트 가져오기
            Page<Object[]> pageObjects = lectureRepository.getUploadListByMemberId(memberId, pageable);
            // 리스트 DTO 생성
            List<LectureDTO.MyListResponseDTO> myListResponseDTOList = new ArrayList<>();
            for (Object[] object : pageObjects) {
                LectureDTO.MyListResponseDTO myListResponseDTO = new LectureDTO.MyListResponseDTO(object);
                myListResponseDTOList.add(myListResponseDTO);
            }
            return myListResponseDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.getMyUploadList() : 에러 발생");
        }

    }

    // 강의 설명 이미지 저장
    private File saveLectureImage(String fileName, int lectureId, File directoryPath/* Lecture lecture*/, MultipartFile multipartFile) {

        // 기존 이미지가 있다면 삭제
        if (fileName != null) {
            deleteLectureImage(fileName, directoryPath);
        }

        // 이미지 저장 (파일명 : "강의 ID.확장자")
        File newFile = MultipartUtils.saveImage(multipartFile, directoryPath, String.valueOf(lectureId));

        return newFile;

    }

    // 강의 설명 이미지 삭제
    private void deleteLectureImage(String fileName, File directoryPath) {

        // 이미지 삭제
        String imagePath = fileName;
        if(imagePath != null) {
            File oldImageFile = new File(directoryPath, imagePath);
            oldImageFile.delete();
        }

    }


}
