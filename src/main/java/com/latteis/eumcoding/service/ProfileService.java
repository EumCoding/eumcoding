package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.TeacherProfileDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.LectureProgress;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.PayLecture;
import com.latteis.eumcoding.persistence.LectureProgressRepository;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.PayLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;

    private final LectureRepository lectureRepository;

    private final PayLectureRepository payLectureRepository;

    private final LectureProgressRepository lectureProgressRepository;

    //application.properties
    //server.domain=http://localhost
    private final Environment env;

    @Value("${file.path.lecture.badge}")
    private String badgePath;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.path.lecture.image}")
    private String lecturePath;



    public TeacherProfileDTO getTeacherProfile(int memberId) {

        Member member = memberRepository.findByIdAndRole(memberId, 1);

        // 메소드 내부에서 사용
        String domain = env.getProperty("server.domain");
        String port = env.getProperty("server.port");

        String filePathMember = domain + ":" + port + "/" + filePath.replace("\\", "/") + "/";
        String filePathLecture = domain + ":" + port + "/" + lecturePath.replace("\\", "/");

        if (member == null) {
            throw new NoSuchElementException("해당 선생님 프로필이 없습니다.");
        }
        //memberId 여기서 널이들어같다.
        List<Lecture> lectureList = lectureRepository.findByMemberId(memberId);
        List<LectureDTO.profileDTO> lectureDTOList = new ArrayList<>();

        int totalStudent = 0;

        for (Lecture lecture : lectureList) {
            LectureDTO.profileDTO lectureDTO = LectureDTO.profileDTO.builder()
                    .id(lecture.getId())
                    .memberId(memberId)
                    .name(lecture.getName())
                    .description(lecture.getDescription())
                    .createdDay(lecture.getCreatedDay())
                    .grade(lecture.getGrade())
                    .thumb(filePathLecture + lecture.getThumb())//ex)http://localhost:8089/C:/eumcoding/lecture/imagenull
                    .price(lecture.getPrice())
                    .state(lecture.getState())
                    .badge(lecture.getBadge())
                    .build();
            lectureDTOList.add(lectureDTO);

            int studentsInLecture = getTotalStudentsByLectureId(lecture.getId());
            if (studentsInLecture >= 0) {
                totalStudent += studentsInLecture;
            } else {
                throw new IllegalStateException("결제가 완료되지 않은 상태입니다.");
            }
        }


        TeacherProfileDTO teacherProfileDTO = TeacherProfileDTO.builder()
                    .memberId(member.getId())
                    .teacherName(member.getName())
                    .teacherProfileImage(filePathMember + member.getProfile()) //ex)http://localhost:8089/C:/eumcoding/member11.png
                    .teacherId(member.getId())
                    .totalLecture(lectureDTOList.size())
                    .totalStudent(totalStudent)
                    .lectureDTOList(lectureDTOList)
                    .build();

            System.out.println(teacherProfileDTO + "teacherProfileDTO");
            return teacherProfileDTO;

    }


    //학생 정보 입력시 해당 학생이 어느 강의를 듣고 있는지 정보
    public List<MemberDTO.StudentProfileDTO> getStudentProfile(int memberId) throws IOException {
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        // 메소드 내부에서 사용
        String domain = env.getProperty("server.domain");
        String port = env.getProperty("server.port");

        String filePathBadge = domain + ":" + port + "/" + badgePath.replace("\\", "/");
        String filePathMember = domain + ":" + port + "/" + filePath.replace("\\", "/") + "/";


        if (!memberOpt.isPresent()) {
            throw new NoSuchElementException("해당 학생 정보가 없습니다.");
        }

        Member member = memberOpt.get();

        if(member.getRole() != 0) {
            throw new NoSuchElementException("해당 사용자는 학생이 아닙니다.");
        }

        List<PayLecture> payLectures = payLectureRepository.findLecturesByStudentId(memberId);
        if (payLectures.isEmpty()) {
            throw new NoSuchElementException("해당 학생이 듣는 강좌가 없습니다.");
        }

        List<MemberDTO.StudentProfileDTO> profiles = new ArrayList<>();

        for (PayLecture payLecture : payLectures) {

            Optional<LectureProgress> optionalLectureProgress = lectureProgressRepository.findByPayLecture(payLecture);


            String badgeUrl = ""; //state가0이면 ""로 출력

            if (optionalLectureProgress.isPresent()) {
                LectureProgress lectureProgress = optionalLectureProgress.get();

                if (lectureProgress.getState() == 1) { // 강의 수강이 완료된 경우
                    //뱃지는 해당강좌 다 수료할경우 그 강좌 id를 이름으로 함
                    int badgeId = lectureProgress.getPayLecture().getLecture().getId();

                    String[] extensions = {"png", "jpg"};
                    String fileExtension = "";
                    File badgeFile = null;

                    //badgePath경로에 저장된 이미지가 png,jpg둘중 어떤거여도 상관없이 그 타입에 맞게 저장됨(동적으로)
                    //c:eumCoding/lecture/badge/1.png or 1.jpg
                    for (String ext : extensions) {
                        String fileName = badgeId + "." + ext;
                        File tempFile = new File(badgePath, fileName);
                        if (tempFile.exists()) {
                            badgeFile = tempFile;
                            fileExtension = ext;
                            break;
                        }
                    }

                    if (badgeFile != null) {
                        //실제 파일의 MIME 타입이 png 또는 jpg인지 확인
                        try {
                            String mimeType = Files.probeContentType(badgeFile.toPath());
                            if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
                                //http://localhost8089/경로/lectureId.png
                                badgeUrl = filePathBadge + "/" + badgeId + "." + fileExtension; // 실제 뱃지 파일이 있으면 URL 업데이트
                            } else {
                                throw new IllegalArgumentException("뱃지 파일은 png 또는 jpg 형식이어야 합니다.");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("뱃지 파일을 읽는 중 오류가 발생했습니다.", e);
                        }
                    } else {
                        badgeUrl = "뱃지 발급 중입니다."; // 강의는 다들어서 state가1인데,뱃지 파일이 없는 경우
                    }
                }
            } else {
                badgeUrl = "아직 강의를 시작하지 않았습니다."; // 강의를 시작하지 않은 경우
            }


            MemberDTO.Badge badge = MemberDTO.Badge.builder()
                    .url(badgeUrl)
                    .lectureId(payLecture.getLecture().getId())
                    .build();

            MemberDTO.StudentProfileDTO profile = MemberDTO.StudentProfileDTO.builder()
                    .memberId(payLecture.getPayment().getMember().getId())
                    .nickname(payLecture.getPayment().getMember().getNickname())
                    .profileImage(filePathMember + payLecture.getPayment().getMember().getProfile())
                    .grade(payLecture.getLecture().getGrade())
                    .badge(Arrays.asList(badge))
                    .build();

            profiles.add(profile);
        }

        return profiles;
    }

    //강의를 결제한 학생 수 구하기
    public int getTotalStudentsByLectureId(int lectureId) {
        List<PayLecture> paymentLectures = payLectureRepository.findByLectureIdAndState(lectureId, 1);
        return paymentLectures.size();
    }


}