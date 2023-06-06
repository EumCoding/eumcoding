package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.TeacherProfileDTO;
import com.latteis.eumcoding.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Api(tags = "Profiles")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/unauth/profile/teacher")
public class ProfileController {
    private final ProfileService profileService;

    @ApiOperation(value = "")
    @GetMapping("/{teacherId}")
    public ResponseEntity<?> getTeacherProfile(
            @ApiParam(value = "", required = true)
            @PathVariable("teacherId") int memberId) {
        try{
            TeacherProfileDTO teacherProfileDTO = profileService.getTeacherProfile(memberId);
            System.out.println(teacherProfileDTO + "선생정보");

            return new ResponseEntity<>(teacherProfileDTO, HttpStatus.OK);

        }catch(NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }

    }


    //학생 정보 누르면 해당 학생이 어떤 학년의 강의를 듣는지 출력
    @ApiOperation(value = "")
    @GetMapping("/student/{memberId}")
    public ResponseEntity<?> getStudentProfile(
            @ApiParam(value = "", required = true)
            @PathVariable("memberId") int memberId) {

        try{
            List<MemberDTO.StudentProfileDTO> studentProfileDTO = profileService.getStudentProfile(memberId);
            System.out.println(studentProfileDTO + "학생 정보");
            return new ResponseEntity<>(studentProfileDTO, HttpStatus.OK);

        }catch(NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }

    }

}