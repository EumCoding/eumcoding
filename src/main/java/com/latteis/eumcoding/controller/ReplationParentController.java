
package com.latteis.eumcoding.controller;


import com.latteis.eumcoding.dto.MyPlanListDTO;
import com.latteis.eumcoding.dto.ResponseDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.CurriculumService;
import com.latteis.eumcoding.service.KakaoMemberService;
import com.latteis.eumcoding.service.MemberService;
import com.latteis.eumcoding.service.ReplationParentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/parent")
public class ReplationParentController {

    private final ReplationParentService replationParentService;

    private final CurriculumService curriculumService;

    @PostMapping("/request")
    public ResponseEntity<?> requestChildVerification(@ApiIgnore Authentication authentication, @RequestParam String childEmail) {
        try {
            int parentId = Integer.parseInt(authentication.getPrincipal().toString());
            replationParentService.requestChildVerification(childEmail, parentId);
            return ResponseEntity.ok("자녀 인증 요청이 성공적으로 이루어졌습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyChildWithNumber(@ApiIgnore Authentication authentication, @RequestParam int verificationNumber,
                                                   @RequestParam int childId) {
        try {
            int parentId = Integer.parseInt(authentication.getPrincipal().toString());
            if(parentId != childId){
                replationParentService.verifyChildWithNumber(verificationNumber, childId,parentId);
                return ResponseEntity.ok("자녀 인증이 성공적으로 이루어졌습니다.");
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("부모 계정과 자녀 계정이 동일할 수 없습니다.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //인증이 성공적으로 되면 자녀 커리큘럼 확인가능
    @GetMapping("/children/curriculum")
    public ResponseEntity<?> getChildrenCurriculum(@ApiIgnore Authentication authentication,
                                                   @RequestParam(required = false) Integer childId) {
        try {
            int parentId = Integer.parseInt(authentication.getPrincipal().toString());

            if (childId == null) {
                return ResponseEntity.badRequest().body("자녀 ID를 제공해야 합니다.");
            }

            // 제공된 childId로 해당 자녀의 정보를 조회
            Member child = replationParentService.getChildByParent(parentId, childId);
            if (child == null) {
                return ResponseEntity.badRequest().body("해당하는 자녀가 없습니다.");
            }

            List<Member> children = Collections.singletonList(child);  // 하나의 자녀 정보만 리스트에 추가, 메모리 절약을위해 사용
            List<MyPlanListDTO> curriculumList = new ArrayList<>();
            for (Member m : children) {
                List<MyPlanListDTO> childCurriculum = curriculumService.getMyPlanList(m.getId());
                curriculumList.addAll(childCurriculum);
            }

            return ResponseEntity.ok().body(curriculumList);

        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/children/curriculum/editPermission")
    public ResponseEntity<?> getChildrenCurriculum(@ApiIgnore Authentication authentication,
                                                   @RequestParam(required = false) Integer childId,
                                                   @RequestParam(required = false) Integer edit) {
        try {
            int parentId = Integer.parseInt(authentication.getPrincipal().toString());
            replationParentService.updatechildCurriculumEditPermission(parentId,childId,edit);
            return ResponseEntity.ok("자녀 커리큘럼 수정 권한이 성공적으로 수행 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

