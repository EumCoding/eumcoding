
package com.latteis.eumcoding.controller;


import com.latteis.eumcoding.dto.MyPlanInfoDTO;
import com.latteis.eumcoding.dto.MyPlanListDTO;
import com.latteis.eumcoding.dto.ReplationChildDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            replationParentService.requestChildVerification(parentId, childEmail);
            return ResponseEntity.ok("자녀 인증 요청이 성공적으로 이루어졌습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyChildWithNumber(@ApiIgnore Authentication authentication, @RequestParam int verificationNumber,
                                                   @RequestParam String childEmail) {
        try {
            int parentId = Integer.parseInt(authentication.getPrincipal().toString());
            replationParentService.verifyChildWithNumber(verificationNumber, childEmail, parentId);
            return ResponseEntity.ok("자녀 인증이 성공적으로 이루어졌습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //인증이 성공적으로 되면 자녀 커리큘럼 확인가능
    @GetMapping("/children/curriculum")
    public ResponseEntity<?> getChildrenCurriculum(@ApiIgnore Authentication authentication,
                                                   @RequestParam(required = false) Integer childId,
                                                   @RequestParam(required = false) Integer lectureId,
                                                   @RequestParam(defaultValue = "2023-01-01T00:00:00") String startDateStr,
                                                   @RequestParam(defaultValue = "2023-09-30T23:59:59") String endDateStr) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

            int parentId = Integer.parseInt(authentication.getPrincipal().toString());
            if (childId == null) {
                return ResponseEntity.badRequest().body("자녀 ID를 제공해야 합니다.");
            }
            List<MyPlanInfoDTO> curriculumList = replationParentService.getChildByParent(parentId, childId,lectureId,startDate,endDate);

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
            replationParentService.updatechildCurriculumEditPermission(parentId, childId, edit);
            return ResponseEntity.ok("자녀 커리큘럼 수정 권한이 성공적으로 수행 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/children/list")
    public ResponseEntity<?> getChildList(@ApiIgnore Authentication authentication) {
        try {
            int parentId = Integer.parseInt(authentication.getPrincipal().toString());
            ReplationChildDTO rp = replationParentService.getListChild(parentId);
            return ResponseEntity.ok().body(rp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

