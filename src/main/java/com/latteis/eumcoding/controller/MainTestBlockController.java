package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.service.MainTestBlockService;
import com.latteis.eumcoding.util.blockCoding.Block;
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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/test/question/block")
@Api(tags = "Main Test Block Controller", description = "메인 테스트 블록코딩 컨트롤러")
public class MainTestBlockController {

    private final MainTestBlockService mainTestBlockService;

    /*
     * 블록 리스트 변환
     */
    @PostMapping(value = "/block-convert")
    @ApiOperation(value = "블록 리스트 변환")
    public ResponseEntity<String> convertBlocks(@ApiIgnore Authentication authentication, @Valid @RequestBody List<Block> blockList) {

        try {
            String blocks = mainTestBlockService.convertBlocks(authentication, blockList);
            return ResponseEntity.ok().body(blocks);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }
}
