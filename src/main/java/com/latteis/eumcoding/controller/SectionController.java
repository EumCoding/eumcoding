package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.service.SectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section")
@Api(tags = "Section Controller", description = "강의 섹션 컨트롤러")
public class SectionController {

    private final SectionService sectionService;

    // 섹션 추가
    @PostMapping(value = "/add")
    @ApiOperation(value = "섹션 추가")
    public ResponseEntity<Object> addSection(@ApiIgnore Authentication authentication, @Valid @RequestBody SectionDTO.AddRequestDTO addRequestDTO) {

        try {
            sectionService.addSection(Integer.parseInt(authentication.getPrincipal().toString()), addRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 섹션 완료 소요시간 수정
    @PostMapping(value = "/update/time_taken")
    @ApiOperation(value = "섹션 완료 소요시간 수정")
    public ResponseEntity<Object> updateTimeTaken(@ApiIgnore Authentication authentication, @Valid @RequestBody SectionDTO.TimeTakenRequestDTO timeTakenRequestDTO) {

        try {
            sectionService.updateTimeTaken(Integer.parseInt(authentication.getPrincipal().toString()), timeTakenRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 섹션 이름 수정
    @PostMapping(value = "/update/name")
    @ApiOperation(value = "섹션 이름 수정")
    public ResponseEntity<Object> updateName(@ApiIgnore Authentication authentication, @Valid @RequestBody SectionDTO.NameRequestDTO nameRequestDTO) {

        try {
            sectionService.updateName(Integer.parseInt(authentication.getPrincipal().toString()), nameRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 섹션 리스트 가져오기
    @GetMapping(value = "/unauth/list")
    @ApiOperation(value = "섹션 리스트 가져오기")
    public ResponseEntity<List<SectionDTO.ListResponseDTO>> getSectionList(@Valid LectureDTO.IdRequestDTO idRequestDTO) {

        try {
            log.info("fdfsfs");
            List<SectionDTO.ListResponseDTO> listResponseDTOList = sectionService.getSectionList(idRequestDTO);
            return ResponseEntity.ok().body(listResponseDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }
}
