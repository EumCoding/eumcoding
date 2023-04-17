package com.latteis.eumcoding.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/eumCodingImgs")
public class ImgController {

    @Value("${file.path}")
    private String filePath;



    //detail 이미지 url
    @GetMapping(value = "/detail/{fileOriginName}")
    public ResponseEntity<Resource> getMenuImgByName(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String absolutePath = filePath + "member";
            String path = absolutePath; // 실제 이미지가 있는 위치
            FileSystemResource resource = new FileSystemResource(path+fileName);
            if(!resource.exists()){
                throw new Exception();
            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception();
        }
    }

    //detail 이미지 url
    @GetMapping(value = "/member/{fileOriginName}")
    public ResponseEntity<Resource> getMemberImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String absolutePath = filePath + "member";
            String path = absolutePath; // 실제 이미지가 있는 위치

            FileSystemResource resource = new FileSystemResource(path+fileName);
            if(!resource.exists()){
                throw new Exception();
            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception();
        }
    }

    //detail 이미지 url
    @GetMapping(value = "/review/{fileOriginName}")
    public ResponseEntity<Resource> getReviewImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String absolutePath = filePath + "member";
            String path = absolutePath; // 실제 이미지가 있는 위치

            FileSystemResource resource = new FileSystemResource(path+fileName);
            if(!resource.exists()){
                throw new Exception();
            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception();
        }
    }
}