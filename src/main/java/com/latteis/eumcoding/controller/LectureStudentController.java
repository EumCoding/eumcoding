package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardCommentDTO;
import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.LectureStudentDTO;
import com.latteis.eumcoding.service.LectureStudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
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
//    @PostMapping(value = "/list")
//    @ApiOperation(value = "학생 목록 가져오기")
//    public ResponseEntity<List<LectureStudentDTO.ListResponseDTO>> getStudentList(@ApiIgnore Authentication authentication,
//                                                                                  @Valid LectureStudentDTO.ListRequestDTO listRequestDTO,
//                                                                                  @PageableDefault(size = 10) Pageable pageable) {
//
//        try {
//            List<LectureStudentDTO.ListResponseDTO> listResponseDTOS = lectureStudentService.getStudentList(Integer.parseInt(authentication.getPrincipal().toString()), listRequestDTO, pageable);
//            return ResponseEntity.ok().body(listResponseDTOS);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//
//    }

}
