package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MainTestDTO;
import com.latteis.eumcoding.service.MainTestService;
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


}
