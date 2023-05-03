package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.service.LectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Teacher Profiles")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/unauth/profile/teacher")
public class lectureController {

    private final LectureService lectureService;

    @ApiOperation(value = "")
    @GetMapping("/lecture/{id}")
    public ResponseEntity<LectureDTO> getLectureById(
            @ApiParam(value = "", required = true)
            @PathVariable("id") String id) {

        try{
            int id1 = Integer.parseInt(id);
            System.out.println(id1 + "id1");
            LectureDTO lectureDTO = lectureService.getLectureById(id1);

            System.out.println(lectureDTO + "강의");

            if (lectureDTO == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(lectureDTO, HttpStatus.OK);

        }catch(Exception e)
        {

            e.printStackTrace();
            return null;
        }

    }


}
