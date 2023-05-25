package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.ResponseDTO;
import com.latteis.eumcoding.dto.SearchDTO;
import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.EmailTokenService;
import com.latteis.eumcoding.service.SearchService;
import com.latteis.eumcoding.service.UnauthMemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/unauth/search")
public class SearchController {
    private final SearchService searchService;



    //강좌 이름으로 검색했을경우
    @GetMapping("/lecture")
    public ResponseEntity<?> searchLectures(
            @RequestParam("searchKeyword") String searchKeyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<SearchDTO> searchLectures;
        try{
            searchLectures = searchService.searchLectures(searchKeyword, pageable);
        }catch(IllegalArgumentException | NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity<>(searchLectures, HttpStatus.OK);
    }


    //선생님 이름으로 검색햇을 경우, 해당 선생님이 올린 강좌 여러개 출력
    @GetMapping("/teacher")
    public ResponseEntity<?> searchTeacherLectures(
            @RequestParam("searchKeyword") String searchKeyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<SearchDTO> searchTeacherLectures;

        try{
            searchTeacherLectures = searchService.searchTeacher(searchKeyword,pageable);
        }catch(IllegalArgumentException | NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
       return new ResponseEntity<>(searchTeacherLectures, HttpStatus.OK);
    }



    //
    @GetMapping("/grade")
    public ResponseEntity<?> searchGradeLectures(
            @RequestParam("searchKeyword") int searchKeyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<SearchDTO.SearchGradeDTO> searchGradeLectures;


        try{
            searchGradeLectures = searchService.searchGrade(searchKeyword, pageable);
        }catch(IllegalArgumentException | NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(searchGradeLectures, HttpStatus.OK);

    }

}
