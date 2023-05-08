package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.service.SectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

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

    // 섹션 추가
//    @PostMapping(value = "/time_taken")
//    @ApiOperation(value = "섹션 완료 소요시간 수정")
//    public ResponseEntity<Object> updateTimeTaken(@ApiIgnore Authentication authentication, @Valid @RequestBody SectionDTO.UpdateTimeTakenRequestDTO requestDTO) {
//
//        try {
//            sectionService.updateTimeTaken(Integer.parseInt(authentication.getPrincipal().toString()), requestDTO);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//
//    }
}
