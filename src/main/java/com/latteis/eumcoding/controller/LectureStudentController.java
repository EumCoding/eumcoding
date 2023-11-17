package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureStudentDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.service.LectureStudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/student")
@Api(tags = "Lecture Student Controller", description = "강의 학생관리 컨트롤러")
public class LectureStudentController {

    private final LectureStudentService lectureStudentService;

    // 학생 목록 가져오기
    @PostMapping(value = "/list")
    @ApiOperation(value = "학생 목록 가져오기")
    public ResponseEntity<List<LectureStudentDTO.ListResponseDTO>> getStudentList(@ApiIgnore Authentication authentication,
                                                                                  @Valid @RequestBody LectureStudentDTO.ListRequestDTO listRequestDTO,
                                                                                  @PageableDefault(size = 10) Pageable pageable) {

        List<LectureStudentDTO.ListResponseDTO> listResponseDTOS = lectureStudentService.getStudentList(Integer.parseInt(authentication.getPrincipal().toString()), listRequestDTO, pageable);
        return ResponseEntity.ok().body(listResponseDTOS);

    }

    /*
    * 학생 정보 가져오기
    */
    @PostMapping(value = "/list/info")
    @ApiOperation(value = "학생 정보 가져오기")
    public ResponseEntity<LectureStudentDTO.InfoResponseDTO> getStudentInfo(@ApiIgnore Authentication authentication,
                                                                            @Valid @RequestBody LectureStudentDTO.InfoRequestDTO infoRequestDTO) {

        LectureStudentDTO.InfoResponseDTO infoResponseDTO = lectureStudentService.getStudentInfo(Integer.parseInt(authentication.getPrincipal().toString()), infoRequestDTO);
        return ResponseEntity.ok().body(infoResponseDTO);

    }

    /*
    * 해당 비디오에 대한 문제, 답, 학생 답안 가져오기
    */
    @PostMapping(value = "/list/info/video")
    @ApiOperation(value = "학생 비디오 정보 가져오기")
    public ResponseEntity<List<LectureStudentDTO.StudentVideoInfoDTO>> getStudentVideoInfo(@ApiIgnore Authentication authentication,
                                                                                           @Valid @RequestBody LectureStudentDTO.StudentVideoInfoRequestDTO requestDTO) {

        try {
            List<LectureStudentDTO.StudentVideoInfoDTO> responseDTOList = lectureStudentService.getStudentVideoInfo(authentication, requestDTO);
            return ResponseEntity.ok().body(responseDTOList);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }
    }

}
