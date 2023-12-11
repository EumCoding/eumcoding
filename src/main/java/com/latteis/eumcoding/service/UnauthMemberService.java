package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnauthMemberService {

    private final MemberRepository memberRepository;

    private final LectureRepository lectureRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.path.member.profile}")
    private String memberPath;


    // 새 계정 생성 - 이메일 중복 검사
    //@RequestParam("profileImgRequest") MultipartFile profileImgRequest
    //swagger에서 테스트 하려면 add매개변수에 집어넣어야함 위에 값을
    public MemberDTO add(MemberDTO.Sign memberDTO){
        if(memberDTO == null || memberDTO.getEmail() == null){
            log.warn("UnauthMemberService.add() : memberEntity에 email이 없습니다.");
            throw new RuntimeException("member에 email이 없습니다.");
        }

        final String email = memberDTO.getEmail();
        if(memberRepository.existsByEmail(email)){
            log.warn("UnauthMemberService.add() : 해당 email이 이미 존재합니다.");
            throw new RuntimeException("해당 email이 이미 존재합니다.");
        }
        // 닉네임 중복 체크
        boolean check = checkNickname(memberDTO.getNickname());
        if(!check){
            log.warn("UnauthMemberService.add() : 중복되는 닉네임입니다.");
            throw new RuntimeException("중복되는 닉네임입니다.");
        }

        try {
            Member member = Member.builder()
                    .email(memberDTO.getEmail())
                    .password(passwordEncoder.encode(memberDTO.getPassword()))
                    .name(memberDTO.getName())
                    .tel(memberDTO.getTel())
                    .nickname(memberDTO.getNickname())
                    .birthDay(memberDTO.getBirthDay())
                    .joinDay(LocalDateTime.now()) // 현재 시간
                    .gender(memberDTO.getGender())
                    .address(memberDTO.getAddress())
                    .role(memberDTO.getRole())
                    .build();

            int memberId = memberRepository.save(member).getId();

            // 이미지가 있는 경우
            if (memberDTO.checkProfileImgRequestNull() && !memberDTO.getProfileImgRequest().isEmpty()) {

                MultipartFile multipartFile = memberDTO.getProfileImgRequest().get(0);


                if (!multipartFile.isEmpty()) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String current_date = now.format(dateTimeFormatter);


                    //String absolutePath = filePath +File.separator + "member";
                    String absolutePath = memberPath;
                    String path = absolutePath;
                    File file = new File(path);

                    if (!file.exists()) {
                        boolean wasSuccessful = file.mkdirs();

                        if (!wasSuccessful) {
                            log.warn("file : was not successful");
                        }
                    }
                    while (true) {
                        String originalFileExtension;
                        String contentType = multipartFile.getContentType();

                        if (ObjectUtils.isEmpty(contentType)) {
                            break;
                        } else {
                            if (contentType.contains("image/jpeg")) {
                                originalFileExtension = ".jpg";
                            } else if (contentType.contains("image/png")) {
                                originalFileExtension = ".png";
                            } else {
                                log.warn("UnauthMemberService.add() : 지원하지 않는 이미지 형식입니다.");
                                break;
                            }
                        }

                        String new_file_name = String.valueOf(memberId);
                        member.setProfile(new_file_name + originalFileExtension);
                        memberRepository.save(member);

                        file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        file.setWritable(true);
                        file.setReadable(true);
                        break;
                    }
                }
            }



            //entity -> DTO
            //MemberDTO responseMemberDTO = new MemberDTO(member);
            MemberDTO memDTO = MemberDTO.builder()
                    .id(memberId)
                    .email(member.getEmail())
                    .password(passwordEncoder.encode(member.getPassword()))
                    .name(member.getName())
                    .tel(member.getTel())
                    .nickname(member.getNickname())
                    .birthDay(member.getBirthDay())
                    .joinDay(LocalDateTime.now()) // 현재 시간
                    .gender(member.getGender())
                    .address(member.getAddress())
                    .role(member.getRole())
                    .build();
            return memDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("UnauthMemberService.add() : 에러 발생.");
        }

    }

    // 로그인 - 자격증명
    public MemberDTO getByCredentials(final String email, final String password, final PasswordEncoder encoder){
        final Member originalMember = memberRepository.findByEmail(email); // 이메일로 MemberEntity를 찾음
        // 패스워드가 같은지 확인
        if(originalMember != null && encoder.matches(password, originalMember.getPassword())){
            checkMemberState(originalMember.getId());
            MemberDTO memberDTO = new MemberDTO(originalMember);
            return memberDTO;
        }
        return null;
    }



    // 같은 이메일이 있는지 확인
    public Boolean checkEmail(final String email){
        if(email == null || email.equals("")){
            log.warn("MemberService.checkEmail() : 값을 입력하세요");
            throw new RuntimeException("MemberService.checkEmail() : email 값이 이상해요");
        }
        if(memberRepository.existsByEmail(email)){ //이메일이 이미 있으면 false리턴
            return false;
        }
        return true;
    }


    //닉네임 중복 체크
    private boolean checkNickname(final String nickname) {
        if(nickname == null || nickname.equals("")){
            log.warn("MemberService.checkNickname() : nickname 값이 이상해요");
            throw new RuntimeException("MemberService.checkNickname() : nickname 값이 이상해요");
        }

        int count = memberRepository.findByNickname(nickname);
        if(count > 0){
            return false;
        }
        return true;
    }


    // 회원의 state(이메일인증확인) 0인지 1인지
    //1이어야 모든 사이트 서비스 이용가능(게시판,리뷰,결제,강의듣기 등등)
    private void checkMemberState(int memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        if (member.getState() != 1) {
            throw new RuntimeException("이메일 인증을 해주셔야 합니다.");
        }
    }

}