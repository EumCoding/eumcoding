package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.*;

import com.latteis.eumcoding.dto.payment.PaymentLectureBadgeDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import com.latteis.eumcoding.util.MultipartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final LectureRepository lectureRepository;

    private final PasswordEncoder passwordEncoder;

    private final PortfolioRepository portfolioRepository;

    private final CurriculumRepository curriculumRepository;

    private final ReviewRepository reviewRepository;

    private final PayLectureRepository payLectureRepository;

    private final CurriculumService curriculumService;

    private final MyLectureListService myLectureListService;

    private final QuestionRepository questionRepository;

    private final PaymentRepository paymentRepository;

    @Value("${file.path}")
    private String filePath;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;

    @Value("${file.path.member.profile}")
    private String profilePath;


    public File getMemberDirectoryPath() {
        File file = new File(filePath);
        file.mkdirs();

        return file;
    }

    public File getMemberProfileDirectoryPath() {
        File file = new File(profilePath);
        file.mkdirs();

        return file;
    }

    // 로그인한 아이디로 찾은 Entity가 비어있는지 검사
    public void chkIfEntityIsEmpty(Object object) {
        if (object == null) {
            throw new RuntimeException("LectureService.chkIfEntityIsEmpty() : 로그인한 유저는 자격이 없습니다.");
        }
    }

    //프로필 확인
    public MemberDTO viewProfile(MemberDTO memberDTO) {
        try {
            //Optional<Member> member = memberRepository.findById(memberDTO.getId());
            Member member = memberRepository.findByMemberId(memberDTO.getId());

            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(member.getEmail())
                    .password(passwordEncoder.encode(member.getPassword()))
                    .name(member.getName())
                    .tel(member.getTel())
                    .nickname(member.getNickname())
                    .birthDay(member.getBirthDay())
                    .joinDay(LocalDateTime.now())
                    .gender(member.getGender())
                    .address(member.getAddress())
                    .role(member.getRole())
                    .build();

            //이미지가 있으면
            if (member.getProfile() != null) {
                responseMemberDTO.setProfile(domain + port + "/eumCodingImgs/member/" + member.getProfile());
            }


            return responseMemberDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.viewProfile() : 에러 발생.");
        }

    }


    //전화번호 업데이트
    @Transactional
    public String updateTel(final int id, final String chgTel) {
        if (id < 1) {
            log.warn("MemberService.updateTel() : Id 값이 이상해요");
            throw new RuntimeException("MemberService.updateTel() : Id 값이 이상해요");
        }
        if (chgTel == null || chgTel.equals("")) {
            log.warn("MemberService.updateTel() : 전화번호 값을 집어넣으세요");
            throw new RuntimeException("MemberService.updateTel() : 전화번호 값을 집어넣으세요");
        }
        int count = memberRepository.findByTel(chgTel); // 바꾸려는 전화번호가 이미 있는지 확인
        if (count > 0) {
            // 이미 같은 전화번호가 있으면
            log.warn("MemberService.updateTel() : 이미 같은 전화번호가 있어요");
            throw new RuntimeException("MemberService.updateTel() : 이미 같은 전화번호가 있어요");
        }

        // 같은 전화번호가 없으면 전화번호 수정
        try {
            final Member member = memberRepository.findByMemberId(id);
            member.setTel(chgTel);
            memberRepository.save(member); // 수정
            // 현재 저장되어 있는 값 가져오기
            final String tel = memberRepository.findTelByMemberId(id);
            return tel;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateContact() : 올바른 양식으로 입력해 주세요.");
        }

    }

    //닉네임 변경
    @Transactional
    public String updateNickName(final int id, final String chgNickName) {
        if (id < 1) {
            log.warn("MemberService.updateNickName() : Id 값이 이상해요");
            throw new RuntimeException("MemberService.updateNickName() : Id 값이 이상해요");
        }
        if (chgNickName == null || chgNickName.equals("")) {
            log.warn("MemberService.updateNickName() : 닉네임 값을 집어넣으세요");
            throw new RuntimeException("MemberService.updateNickName() : 닉네임값을 집어넣으세요");
        }
        int count = memberRepository.findByNickname(chgNickName);
        if (count > 0) {
            // 이미 같은 닉네임이 있으면
            log.warn("MemberService.updateNickName() : 이미 같은 닉네임이 있어요");
            throw new RuntimeException("MemberService.updateNickName() : 이미 같은 닉네임이 있어요");
        }


        try {
            final Member member = memberRepository.findByMemberId(id);
            member.setNickname(chgNickName);
            memberRepository.save(member); // 수정
            // 현재 저장되어 있는 값 가져오기
            final String NickName = memberRepository.findNickNameByMemberId(id);
            return NickName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateNickName() : 올바른 양식으로 입력해 주세요.");
        }

    }


    @Transactional
    public String updateAddress(final int id, final String chgAdd) {
        if (id < 1) {
            log.warn("MemberService.updateAdd() : Id 값이 이상해요");
            throw new RuntimeException("MemberService.updateAdd() : Id 값이 이상해요");
        }
        if (chgAdd == null || chgAdd.equals("")) {
            log.warn("MemberService.updateAdd() : 주소 값을 집어넣으세요");
            throw new RuntimeException("MemberService.updateAdd() : 주소 값을 집어넣으세요");
        }
        int count = memberRepository.findByAdd(chgAdd); //
        if (count > 0) {
            // 이미 같은 주소가 있으면
            log.warn("MemberService.updateAdd() : 이미 같은 주소가 있어요");
            throw new RuntimeException("MemberService.updateAdd() : 이미 같은 주소가 있어요");
        }


        try {
            final Member member = memberRepository.findByMemberId(id);
            member.setAddress(chgAdd);
            memberRepository.save(member); // 수정
            // 현재 저장되어 있는 값 가져오기
            final String address = memberRepository.findAddByMemberId(id);
            return address;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateAddress() : 올바른 양식으로 입력해 주세요.");
        }

    }


    // 현재 비밀번호와 변경할 비밀번호 받아서 비밀번호 변경
    @Transactional
    public boolean updatePw(final int id, final String curPw, final String chgPw, PasswordEncoder passwordEncoder) {
        if (curPw == null || curPw.equals("") || chgPw == null | chgPw.equals("")) {
            log.warn("MemberService.changePw() : 들어온 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : 들어온 값이 이상해요");
        }
        if (id < 1) {
            log.warn("MemberService.changePw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : memberId 값이 이상해요");
        }
        // 현재 비밀번호가 맞는지 검사
        String originPassword = memberRepository.findPasswordByMemberId(id); //DB에 들어가있는 PW
        if (!passwordEncoder.matches(curPw, originPassword)) {
            //비밀번호가 다르면
            log.warn("MemberService.changePw() : 비밀번호가 달라요");
            throw new RuntimeException("MemberService.changePw() : 비밀번호가 달라요");
        }
        //비밀번호가 맞으면 비밀번호 변경
        final Member member = memberRepository.findByMemberId(id);
        member.changePassword(passwordEncoder.encode(chgPw));
        memberRepository.save(member);
        return true;
    }


    //닉네임 중복 체크
    private boolean checkNickname(final String nickname) {
        if (nickname == null || nickname.equals("")) {
            log.warn("MemberService.checkNickname() : nickname 값이 이상해요");
            throw new RuntimeException("MemberService.checkNickname() : nickname 값이 이상해요");
        }

        int count = memberRepository.findByNickname(nickname);
        if (count > 0) {
            return false;
        }
        return true;
    }

    // 프로필 이미지 변경
    public MemberDTO updateProfileImg(int memberId, MemberDTO.UpdateProfile memberDTO) {
        try {


            Optional<Member> memberOptional = memberRepository.findById(memberId);
            Member member = memberOptional.get();

            // 강의 설명 이미지가 있을 경우
            if (memberDTO.checkProfileImgRequestNull()){
                // 이미지 한개만 저장
                MultipartFile multipartFile = memberDTO.getProfileImgRequest().get(0);

                // 이미지 저장
                File newFile = saveLectureImage(member.getProfile() ,member.getId(), getMemberProfileDirectoryPath(), multipartFile);

                // 이미지 파일명 DB에 저장
                member.setProfile(newFile.getName());

                memberRepository.save(member);

            } else {
                Preconditions.checkNotNull(memberDTO.getProfile(), "받아온 이미지가 없습니다.");
            }
            return MemberDTO.builder().profile(member.getProfile()).build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateContact() : 에러 발생.");
        }

    }

    // 강의 설명 이미지 저장
    private File saveLectureImage(String fileName, int lectureId, File directoryPath/* Lecture lecture*/, MultipartFile multipartFile){
        try{
            // 기존 이미지가 있다면 삭제
            if (fileName != null) {
                deleteLectureImage(fileName, directoryPath);
            }

            // 이미지 저장 (파일명 : "강의 ID.확장자")
            File newFile = MultipartUtils.saveImage(multipartFile, directoryPath, String.valueOf(lectureId));

            return newFile;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

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

    //선생님로그인 시 마이페이지
    public TeacherMyPageDTO TeacherMyPage(int memberId) {

        try {
            Member member = memberRepository.findByIdAndRole(memberId, 1);
            if (member == null) {
                throw new RuntimeException("강사가 아닙니다.");
            }

            Portfolio portfolio = portfolioRepository.findByMemberId(memberId);
            List<Lecture> lectureList = lectureRepository.findByMemberId(memberId);

            List<LectureDTO.coursesDTO> coursesList = new ArrayList<>();
            for (Lecture lecture : lectureList) {
                LectureDTO.coursesDTO dto = convertCoursesDTO(lecture);
                coursesList.add(dto);
            }

       /*     List<QuestionDTO.countQuestionDTO> questionList = new ArrayList<>();
            for (Lecture lecture : lectureList) {
                QuestionDTO.countQuestionDTO dto = converQuestionDTO(lecture);
                questionList.add(dto);
            }*/

            TeacherMyPageDTO teacherMyPageDTO = TeacherMyPageDTO.builder()
                    .memberId(member.getId())
                    .nickname(member.getNickname())
                    .profileImgRequest(member.getProfile())
                    .address(member.getAddress())
                    .tel(member.getTel())
                    .joinDay(member.getJoinDay())
                    .birthDay(member.getBirthDay())
                    .name(member.getName())
                    .email(member.getEmail())
                    .resume(portfolio.getPath())
                    .portfolioPath(portfolio.getPath())
                    .courses(coursesList)
                    .build();

            return teacherMyPageDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.TeacherMyPage : 에러 발생.");
        }

    }


    /*
    //해당 과목에 대한 질문들 총 갯수
    public int getCountQuestionList(int lectureId) {

        Optional<Integer> question = questionRepository.countQuestionId(lectureId);
        return question.orElseThrow(() -> new RuntimeException("강좌에 대한 질문이 없습니다."));
    }


질문관련 코드인데 안써서 일단 주석처리
    private QuestionDTO.countQuestionDTO converQuestionDTO(Lecture lecture){

        int countQuestionId = getCountQuestionList(lecture.getId());

        return QuestionDTO.countQuestionDTO.builder()
                .lectureId(lecture.getId())
                .count(countQuestionId)
                .build();
    }*/


    //강좌별 학생들의평균진도율 + 평균성적 + 강좌별 판매 정렬 + 판매률(횟수)+평점
    private LectureDTO.coursesDTO convertCoursesDTO(Lecture lecture){

        float avgerageScore = getAverageScoreForLecture(lecture.getId());
        float avgReviewScore = getAvgReviewScoreForLecture(lecture.getId());
        int reviewCount = getReviewCountForLecture(lecture.getId());
        int getCountPaymentLecture = getCountPaymentLecture(lecture.getId());

        return LectureDTO.coursesDTO.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getName())
                .createdDay(lecture.getCreatedDay())
                .avgScore(avgerageScore)
                .avgReviewScore(avgReviewScore)
                .reviewCount(reviewCount)
                .avgProgress(getAverageLectureProgress(lecture.getId()))         //강좌당 평균 진도율
                .saleCount(getCountPaymentLecture)           //강좌 판매율,paymentLecture테이블을 이용해서 구하자
                .build();
    }

    private int getCountPaymentLecture(int lectureId){
        Optional<Integer> count = memberRepository.getCountPaymentLecture(lectureId);
        return count.orElseThrow(() -> new RuntimeException("강좌를 구매한 회원이 없습니다."));
    }


    private float getAvgReviewScoreForLecture(int lectureId) {
        Optional<Float> result = reviewRepository.findByReviewsByLectureId(lectureId);

        if (!result.isPresent()) {
            return 0; // 리뷰가 없을 경우 0을 반환
        }

        return result.orElse(0.0f);
    }

    private int getReviewCountForLecture(int lectureId) {
        Optional<Integer> reviews = reviewRepository.findByReviewsCount(lectureId);
        return reviews.orElseThrow(() -> new RuntimeException("과목에 대한 리뷰가 없습니다."));
    }

    //과목별 평균 점수
    private float getAverageScoreForLecture(int lectureId) {

        Optional<Float> result = curriculumRepository.findByAVGLectureScore(lectureId);

        if(!result.isPresent()) return 0;

        return result.orElseThrow(() -> new RuntimeException("과목에 대한 점수가없습니다."));
    }


    //평균 진도율
    public float getAverageLectureProgress(int lectureId) {
        // 1. 해당 강의를 구매한 모든 학생들의 ID를 찾는다.
        List<PayLecture> payLectures = payLectureRepository.findByLectureIdAndState(lectureId, 1); // state 1:결제성공

        // 학생별 진도율을 저장할 리스트
        List<Integer> studentProgressList = new ArrayList<>();

        for (PayLecture payLecture : payLectures) {
            int memberId = payLecture.getPayment().getMember().getId();

            // 2. 각 학생에 대한 강의 진도율을 계산한다.
            List<MyLectureListDTO> lectureList  = myLectureListService.getMyLectureList(memberId,1,100,0);;

            for (MyLectureListDTO myLecture : lectureList) {
                if (myLecture.getLectureId() == lectureId) {
                    studentProgressList.add(myLecture.getProgress());
                }
            }
        }
        int totalProgress = 0;
        for (int progress : studentProgressList) {
            totalProgress += progress;
        }

        return studentProgressList.isEmpty() ? 0 : (float) totalProgress / studentProgressList.size();
    }

    /**
     * 결제한 강좌 배너 모음,count포함
     */
    public PaymentLectureBadgeDTO paymentLectureBadge(int memberId) {

        try {
            Member member = memberRepository.findByIdAndRole(memberId, 0);
            if (member == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            List<Object[]> payment = paymentRepository.findByPaymentLectureBadge(memberId);
            if(payment == null){
                throw new IllegalArgumentException("결제한 강좌가 없습니다.");
            }
            long count = paymentRepository.countPaymentBadge(memberId);
            List<PaymentLectureBadgeDTO.PayLectureBadgeDTO> payments = new ArrayList<>();
            for(Object[] paymentBadge : payment){
                PaymentLectureBadgeDTO.PayLectureBadgeDTO paymentLectureBadgeDTO = PaymentLectureBadgeDTO.PayLectureBadgeDTO.builder()
                        .badge(domain + port + "/eumCodingImgs/lecture/badge/" + String.valueOf(paymentBadge[0]))
                        .lecutreid(Integer.parseInt(String.valueOf(paymentBadge[1])))
                        .build();
                payments.add(paymentLectureBadgeDTO);
            }
            return PaymentLectureBadgeDTO.builder()
                    .count(count)
                    .payLectureBadgeDTO(payments)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.PaymentLectureBadge : 에러 발생.");
        }

    }


}
