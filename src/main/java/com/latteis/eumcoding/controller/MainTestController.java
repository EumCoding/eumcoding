package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MainTestDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.service.MainTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/test")
@Api(tags = "Main Test Controller", description = "메인 평가 컨트롤러")
public class MainTestController {

    private final MainTestService mainTestService;

    @PostMapping(value = "/add")
    @ApiOperation(value = "메인 평가 등록")
    public ResponseEntity<Object> addMainTest(@ApiIgnore Authentication authentication,
                                             @Valid @RequestBody MainTestDTO.AddRequestDTO addRequestDTO) {

        try {
            mainTestService.addMainTest(Integer.parseInt(authentication.getPrincipal().toString()), addRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 테스트 정보 가져오기
    @GetMapping(value = "/unauth/view")
    @ApiOperation(value = "메인 평가 정보 가져오기")
    public ResponseEntity<List<MainTestDTO.MainTestInfoRequestDTO>> getMainTest(@RequestParam int lectureId) {

        try {
            List<MainTestDTO.MainTestInfoRequestDTO> responseDTO = mainTestService.getMainTest(lectureId);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }

    }

    // main test 문제 리스트 가져오기
    @GetMapping(value = "/view/question")
    @ApiOperation(value = "메인 평가 문제 리스트 가져오기")
    public ResponseEntity<List<MainTestDTO.MainTestQuestionInfoRequestDTO>> getMainTestQuestion(@ApiIgnore Authentication authentication , @RequestParam int mainTestId) {

        try {
            List<MainTestDTO.MainTestQuestionInfoRequestDTO> responseDTO = mainTestService.getMainTestQuestion(mainTestId);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }

    }

    // 테스트 section 수정하기
    @PutMapping(value = "/updateSection")
    @ApiOperation(value = "메인 평가 섹션 수정하기")
    public ResponseEntity<Object> updateSection(@ApiIgnore Authentication authentication,
                                                @Valid @RequestBody MainTestDTO.UpdateSectionRequestDTO updateSectionRequestDTO) {

        try {
            mainTestService.updateSection(Integer.parseInt(authentication.getPrincipal().toString()), updateSectionRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 테스트 문제 삭제하기
    @PostMapping(value = "/deleteQuestion")
    @ApiOperation(value = "메인 평가 문제 삭제하기")
    public ResponseEntity<Object> deleteQuestion(@ApiIgnore Authentication authentication,
                                                 @RequestParam int questionId) {

        try {
            mainTestService.deleteMainTestQuestion(Integer.parseInt(authentication.getPrincipal().toString()), questionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /*
     * MainTest 응시 자격 확인
     */
    @PostMapping(value = "/confirmation-of-ofeligibility")
    @ApiOperation(value = "MainTest 응시 자격 확인")
    public ResponseEntity<Integer> confirmationOfEligibility(@ApiIgnore Authentication authentication, @Valid @RequestBody MainTestDTO.IdDTO idDTO) {

        try {
            int satisfy = mainTestService.confirmationOfEligibility(authentication, idDTO);
            return ResponseEntity.ok().body(satisfy);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

    /*
     * Lecture에 맞는 MainTestID 리스트 반환
     */
    @PostMapping(value = "/ids")
    @ApiOperation(value = "Lecture에 맞는 MainTestID 리스트 반환")
    public ResponseEntity<List<Integer>> getIDs(@ApiIgnore Authentication authentication, @Valid @RequestBody LectureDTO.IdRequestDTO lectureIdDTO) {

        try {
            List<Integer> ids = mainTestService.getIDs(authentication, lectureIdDTO);
            return ResponseEntity.ok().body(ids);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

}
