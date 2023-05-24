package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.StatsDTO;
import com.latteis.eumcoding.service.StatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
@Api(tags = "Stats Controller", description = "강사 통계 컨트롤러")
public class StatsController {

    private final StatsService statsService;

    // 강사 통계 컨트롤러
    @PostMapping(value = "/view")
    @ApiOperation(value = "통계 보기")
    public ResponseEntity<StatsDTO.MainResponseDTO> viewStats(@ApiIgnore Authentication authentication, @RequestBody StatsDTO.DateRequestDTO dateRequestDTO) {

        try {
            StatsDTO.MainResponseDTO mainResponseDTO = statsService.viewStats(Integer.parseInt(authentication.getPrincipal().toString()), dateRequestDTO);
            return ResponseEntity.ok().body(mainResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


}
