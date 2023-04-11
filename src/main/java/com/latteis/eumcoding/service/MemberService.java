package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${file.path}")
    private String filePath;


    // 새 계정 생성 - 이메일 중복 검사
    public MemberDTO add(MemberDTO memberDTO){

        if(memberDTO == null || memberDTO.getEmail() == null){
            log.warn("MemberService.add() : memberEntity에 email이 없습니다.");
            throw new RuntimeException("MemberService.add() : memberEntity에 email이 없습니다.");
        }

        final String email = memberDTO.getEmail();
        if(memberRepository.existsByEmail(email)){
            log.warn("MemberService.add() : 해당 email이 이미 존재합니다.");
            throw new RuntimeException("MemberService.add() : 해당 email이 이미 존재합니다.");
        }
        // 닉네임 중복 체크
        boolean check = checkNickname(memberDTO.getNickname());
        if(!check){
            log.warn("MemberService.add() : 중복되는 닉네임입니다.");
            throw new RuntimeException("MemberService.add() : 중복되는 닉네임입니다.");
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
            if (memberDTO.checkProfileImgRequestNull()) {

                MultipartFile multipartFile = memberDTO.getProfileImgRequest().get(0);
                String current_date = null;

                if (!multipartFile.isEmpty()) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    current_date = now.format(dateTimeFormatter);


                    String absolutePath = filePath + "/member";


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
                            } else if (contentType.contains("images/png")) {
                                originalFileExtension = ".png";
                            } else {
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
            throw new RuntimeException("MemberService.add() : 에러 발생.");
        }

    }


    // 회원정보수정
    public MemberDTO editInfo(MemberDTO memberDTO, final int id){

        try {
            Member member = memberRepository.findById(id).get();


            member.setAddress(memberDTO.getAddress());
            member.setName(memberDTO.getName());

            // 이미지가 있는 경우
            if (memberDTO.checkProfileImgRequestNull()) {

                MultipartFile multipartFile = memberDTO.getProfileImgRequest().get(0);
                String current_date = null;

                if (!multipartFile.isEmpty()) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    current_date = now.format(dateTimeFormatter);


                    String absolutePath = filePath + "/member";

                    //String path = "images" + File.separator + current_date;
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
                            } else if (contentType.contains("images/png")) {
                                originalFileExtension = ".png";
                            } else {
                                break;
                            }
                        }

                        // 기존에 파일이 있는 경우 기존 파일을 제거하고 진행
                        if(member.getProfile() != null && member.getProfile() != ""){
                            String tempPath = absolutePath + File.separator + member.getProfile();
                            File delFile = new File(tempPath);
                            // 해당 파일이 존재하는지 한번 더 체크 후 삭제
                            if(delFile.isFile()){
                                delFile.delete();
                            }
                        }

                        String new_file_name = String.valueOf(id);

                        member.setProfile(new_file_name + originalFileExtension);

                        file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        file.setWritable(true);
                        file.setReadable(true);
                        break;
                    }
                }
            }
            memberRepository.save(member);

            MemberDTO responseMemberDTO = new MemberDTO(member);
            return responseMemberDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.add() : 에러 발생.");
        }

    }


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

    // 로그인 - 자격증명
    public MemberDTO getByCredentials(final String email, final String password, final PasswordEncoder encoder){
        final Member originalMember = memberRepository.findByEmail(email); // 이메일로 MemberEntity를 찾음
        log.warn(email);
        log.warn(password);
        System.out.println(originalMember);
        // 패스워드가 같은지 확인
        if(originalMember != null && encoder.matches(password, originalMember.getPassword())){
            MemberDTO memberDTO = new MemberDTO(originalMember);
            return memberDTO;
        }
        return null;
    }
}
