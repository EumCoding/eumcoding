package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Value("${file.path}")
    private String filePath;


    //프로필 확인
    public MemberDTO viewProfile(MemberDTO memberDTO){
        try{
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
                responseMemberDTO.setProfile(filePath + member.getProfile());
            }


            return responseMemberDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.viewProfile() : 에러 발생.");
        }

    }



    //전화번호 업데이트
    @Transactional
    public String updateTel(final int id, final String chgTel){
        if(id < 1){
            log.warn("MemberService.updateTel() : Id 값이 이상해요");
            throw new RuntimeException("MemberService.updateTel() : Id 값이 이상해요");
        }
        if(chgTel == null || chgTel.equals("")){
            log.warn("MemberService.updateTel() : 전화번호 값을 집어넣으세요");
            throw new RuntimeException("MemberService.updateTel() : 전화번호 값을 집어넣으세요");
        }
        int count = memberRepository.findByTel(chgTel); // 바꾸려는 전화번호가 이미 있는지 확인
        if(count > 0){
            // 이미 같은 전화번호가 있으면
            log.warn("MemberService.updateTel() : 이미 같은 전화번호가 있어요");
            throw new RuntimeException("MemberService.updateTel() : 이미 같은 전화번호가 있어요");
        }

        // 같은 전화번호가 없으면 전화번호 수정
        try{
            final Member member = memberRepository.findByMemberId(id);
            member.setTel(chgTel);
            memberRepository.save(member); // 수정
            // 현재 저장되어 있는 값 가져오기
            final String tel = memberRepository.findTelByMemberId(id);
            return tel;

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateContact() : 올바른 양식으로 입력해 주세요.");
        }

    }
    //닉네임 변경
    @Transactional
    public String updateNickName(final int id, final String chgNickName){
        if(id < 1){
            log.warn("MemberService.updateNickName() : Id 값이 이상해요");
            throw new RuntimeException("MemberService.updateNickName() : Id 값이 이상해요");
        }
        if(chgNickName == null || chgNickName.equals("")){
            log.warn("MemberService.updateNickName() : 닉네임 값을 집어넣으세요");
            throw new RuntimeException("MemberService.updateNickName() : 닉네임값을 집어넣으세요");
        }
        int count = memberRepository.findByNickname(chgNickName);
        if(count > 0){
            // 이미 같은 닉네임이 있으면
            log.warn("MemberService.updateNickName() : 이미 같은 닉네임이 있어요");
            throw new RuntimeException("MemberService.updateNickName() : 이미 같은 닉네임이 있어요");
        }


        try{
            final Member member = memberRepository.findByMemberId(id);
            member.setNickname(chgNickName);
            memberRepository.save(member); // 수정
            // 현재 저장되어 있는 값 가져오기
            final String NickName = memberRepository.findNickNameByMemberId(id);
            return NickName;

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateNickName() : 올바른 양식으로 입력해 주세요.");
        }

    }


    @Transactional
    public String updateAddress(final int id, final String chgAdd){
        if(id < 1){
            log.warn("MemberService.updateAdd() : Id 값이 이상해요");
            throw new RuntimeException("MemberService.updateAdd() : Id 값이 이상해요");
        }
        if(chgAdd == null || chgAdd.equals("")){
            log.warn("MemberService.updateAdd() : 주소 값을 집어넣으세요");
            throw new RuntimeException("MemberService.updateAdd() : 주소 값을 집어넣으세요");
        }
        int count = memberRepository.findByAdd(chgAdd); //
        if(count > 0){
            // 이미 같은 주소가 있으면
            log.warn("MemberService.updateAdd() : 이미 같은 주소가 있어요");
            throw new RuntimeException("MemberService.updateAdd() : 이미 같은 주소가 있어요");
        }


        try{
            final Member member = memberRepository.findByMemberId(id);
            member.setAddress(chgAdd);
            memberRepository.save(member); // 수정
            // 현재 저장되어 있는 값 가져오기
            final String address = memberRepository.findAddByMemberId(id);
            return address;

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateAddress() : 올바른 양식으로 입력해 주세요.");
        }

    }


    // 현재 비밀번호와 변경할 비밀번호 받아서 비밀번호 변경
    @Transactional
    public boolean updatePw(final int id, final String curPw, final String chgPw, PasswordEncoder passwordEncoder){
        if(curPw == null || curPw.equals("") || chgPw == null | chgPw.equals("")){
            log.warn("MemberService.changePw() : 들어온 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : 들어온 값이 이상해요");
        }
        if(id < 1){
            log.warn("MemberService.changePw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : memberId 값이 이상해요");
        }
        // 현재 비밀번호가 맞는지 검사
        String originPassword = memberRepository.findPasswordByMemberId(id); //DB에 들어가있는 PW
        if(!passwordEncoder.matches(curPw, originPassword)){
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



    // 프로필 이미지 변경
    public MemberDTO updateProfileImg(int memberId, MemberDTO.UpdateProfile memberDTO) {


        try {

            // 이미지가 있는 경우
            if (memberDTO.checkProfileImgRequestNull()) {

                Member member = memberRepository.findByMemberId(memberId);

                // 기존 이미지 삭제
                if (member.getProfile() != null) {
                    String tempPath = "C:" + File.separator + "eum" + File.separator + "member" + File.separator + member.getProfile();
                    File delFile = new File(tempPath);
                    // 해당 파일이 존재하는지 한번 더 체크 후 삭제
                    if(delFile.isFile()){
                        delFile.delete();
                    }
                }

                MultipartFile multipartFile = memberDTO.getProfileImgRequest().get(0);
                String current_date = null;

                if (!multipartFile.isEmpty()) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    current_date = now.format(dateTimeFormatter);

                    //            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
                    String absolutePath = "C:" + File.separator + "eum" + File.separator + "member";

                    //            String path = "images" + File.separator + current_date;
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

                        file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        file.setWritable(true);
                        file.setReadable(true);
                        break;
                    }
                }
                memberRepository.save(member);
                return MemberDTO.builder().profileImg(member.getProfile()).build();
            } else {
                log.warn("MemberService.updateProfileImg() : 사진이 없습니다.");
                throw new RuntimeException("MemberService.updateProfileImg() : 사진이 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.updateContact() : 에러 발생.");
        }
    }
}