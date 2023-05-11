package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MainNewLectureDTO;
import com.latteis.eumcoding.dto.MainPopularLectureDTO;

import com.latteis.eumcoding.service.MainService;
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

import java.util.List;
import java.util.NoSuchElementException;

@Api(tags = "MainController")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/unauth/main")
public class MainController {
    private final MainService mainService;
//
//    @GetMapping("/popular")
//    //public ResponseEntity<List<MainPopularLectureDTO>> getTop5PopularLectures()
//    //e.getMessage()를 사용하기위해 ?로 타입변경
//    //나중에 문제 생길시 메시지안받고 위에 타입 으로 바꾸면됨
//    public ResponseEntity<?> getTop5PopularLectures() {
//
//        try{
//            List<MainPopularLectureDTO> popularLectures = mainService.getPopularLectures();
//            System.out.println("\n" + popularLectures + "\n" + "인기강좌");
//            return new ResponseEntity<>(popularLectures, HttpStatus.OK);
//        }catch(NoSuchElementException e){
//            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping("/new")
//    //public ResponseEntity<List<MainNewLectureDTO>> getTop5NewLecture()
//    public ResponseEntity<?> getTop5NewLecture() {
//
//        try{
//            List<MainNewLectureDTO> newLectures = mainService.getNewLectures();
//            System.out.println("\n" + newLectures + "\n" + "날짜순");
//            return new ResponseEntity<>(newLectures, HttpStatus.OK);
//        }catch (NoSuchElementException e) {
//            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
//        }
//
//    }







}