package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.*;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.channels.IllegalChannelGroupException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplationParentService {


    private final MemberRepository memberRepository;

    private final RelationParentRepository relationParentRepository;

    private final EmailNumberRepository emailNumberRepository;

    private final EmailNumberService emailNumberService; // 이메일 전송 서비스

    private final CurriculumService curriculumService;

    private final CurriculumRepository curriculumRepository;

    /*
    1.인증번호를 보내면 emailNumber테이블에 값이 저장
    2.인증이 성공적으로 되면 replationParents테이블에 값저장
    3.2번이 되면, 인증번호를 요청해도 이미 인증된 계쩡입니다. 라는 예외처리를 해야함
    4.단, 2번이 안되면 1번이 반복적으로 이루어져야함. 
    */
    public void requestChildVerification(int parentId, String childEmail) {
        Member parent = memberRepository.findByIdAndRole(parentId, 3);
        if (parent == null) {
            log.info("학부모 계정으로만 이용 가능합니다.");
            throw new IllegalArgumentException("학부모 계정으로만 이용 가능합니다.");
        }

        Member child = memberRepository.findByEmailAndRole(childEmail, 0);
        if (child == null) {
            log.info("해당 이메일의 학생이 존재하지 않습니다.");
            throw new IllegalArgumentException("해당 이메일의 학생이 존재하지 않습니다.");
        }
        Optional<ReplationParent> existingRelation = relationParentRepository.findByChildId(child.getId());

        if (existingRelation.isPresent()) {
            log.info("이미 인증된 계정입니다.");
            throw new IllegalArgumentException("이미 인증된 계정입니다.");
        }
        //인증번호전송
        emailNumberService.sendVerificationNumber(child.getId(), child.getEmail());
    }

    public void verifyChildWithNumber(int verificationNumber, String childEmail, int parentId) {
        Optional<EmailNumber> emailNumberOpt = emailNumberRepository.findByVerificationNumberAndMemberEmail(verificationNumber, childEmail);
        EmailNumber emailNumber = emailNumberOpt.orElseThrow(() -> {
            log.info("잘못된 인증 번호입니다.");
            return new IllegalArgumentException("잘못된 인증 번호입니다.");
        });

        if (emailNumber == null || emailNumber.getExpired() == 1) {
            throw new IllegalArgumentException("잘못된 인증 번호이거나 만료된 번호입니다.");
        }


        Member child = memberRepository.findByEmailAndRole(childEmail, 0);
        Member parent = memberRepository.findByIdAndRole(parentId, 3);

        if (parent == null) {
            log.info("학부모 아이디가 잘못되었습니다.");
            throw new IllegalArgumentException("학부모 아이디가 잘못되었습니다.");

        }

        //replationParent 테이블 memberId 는 유니크 설정했음
        Optional<ReplationParent> existingRelation = relationParentRepository.findByMemberId(child.getId());
        if (existingRelation.isPresent()) {
            log.info("이미 인증된 계정입니다.");
            throw new IllegalArgumentException("이미 인증된 계정입니다.");
        }

        ReplationParent relation = new ReplationParent();
        relation.setChild(child);
        relation.setParent(parent);
        relationParentRepository.save(relation);


        emailNumber.setNumberToUsed();
        emailNumberRepository.save(emailNumber);
    }

    //부모가 자녀 계정 연동 성공 시 자녀 커리큘럼 볼 수 있음.
    public List<MyPlanInfoDTO> getChildByParent(int parentId, int childId) {
        ReplationParent member = relationParentRepository.findByParentIdChildId(parentId, childId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 자녀가 없습니다."));
        List<MyPlanInfoDTO> curriculumList = curriculumService.getMyPlanInfo(member.getChild().getId());
        return curriculumList;
    }


    //자녀 커리큘럼 수정권한 메서드
    public void updatechildCurriculumEditPermission(int parentId, int childId, int edit) {

        // 1. 로그인한 계정이 학부모 계정인지 확인.
        if (!isParentAccount(parentId)) {
            log.info("학부모 계정만 권한이 있습니다.");
            throw new IllegalArgumentException("학부모 계정만 권한이 있습니다.");
        }

        // 2. 자녀 계정으로 edit를 요청한 경우 예외 처리
        if (parentId == childId) {
            log.info("자녀 계정으로는 권한이 없습니다.");
            throw new IllegalArgumentException("자녀 계정으로는 권한이 없습니다.");
        }

        //부모 자녀 관계 확인
        Optional<ReplationParent> relations = relationParentRepository.findByParentIdChildId(parentId, childId);
        ReplationParent replationParent = relations.orElseThrow(() -> new IllegalArgumentException("자녀가 아닙니다"));

        //자녀의 커리큘럼 조회
        List<Curriculum> curriculums = curriculumRepository.findByMemberId(childId);

        // edit 권한 체크
        if (edit != 0 && edit != 1 && isParentAccount(parentId)) {
            log.info("edit 값은 0 혹은 1만 가능합니다. 부모계정만 수정 가능합니다.");
            throw new IllegalArgumentException("edit 값은 0 혹은 1만 가능합니다. 부모계정만 수정 가능합니다.");
        }

        for (Curriculum curriculum : curriculums) {
            curriculum.setEdit(edit);
            curriculumRepository.save(curriculum);
        }
    }

    //학부모 계정(member.role=3)인지 확인하는 메서드
    private boolean isParentAccount(int parentId) {
        return relationParentRepository.findByParentId(parentId).isPresent();
    }

    //부모가 연동한 자녀
    //총 몇명 인지 memberId, email, 이름같은 간단한 정보
    public ReplationChildDTO getListChild(int parentId) {
        //관계 테이블에서 해당 부모의 id가 연결된 child 수 가져오기
        List<ReplationParent> replationParent = relationParentRepository.findChildId(parentId);
        if (replationParent == null || replationParent.isEmpty()) {
            throw new IllegalArgumentException("해당 부모계정이랑 연계된 잔 계정이 없습니다.");
        }

        int count = replationParent.size();

        List<ReplationChildDTO.ReplationChildInfo> replationChildDTOS = new ArrayList<>();
        for (ReplationParent rp : replationParent) {
            //해당 child의 정보 가져오기
            //이메일 닉네임 이름 프로필
            ReplationChildDTO.ReplationChildInfo replationChildInfo = ReplationChildDTO.ReplationChildInfo.builder()
                    .memberId(rp.getChild().getId())
                    .email(rp.getChild().getEmail())
                    .nickname(rp.getChild().getNickname())
                    .name(rp.getChild().getName())
                    .profile(rp.getChild().getProfile())
                    .build();
            replationChildDTOS.add(replationChildInfo);
        }
        ReplationChildDTO replationChildDTO = ReplationChildDTO.builder()
                .count(count)
                .rci(replationChildDTOS)
                .build();
        return replationChildDTO;
    }
}
