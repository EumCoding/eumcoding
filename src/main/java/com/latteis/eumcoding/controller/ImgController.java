package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.service.*;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/eumCodingImgs")
public class ImgController {



    private final LectureService lectureService;

    private final VideoService videoService;

    private final MemberService memberService;
    private final SearchService searchService;
    private final ProfileService profileService;
    private final MyLectureListService myLectureListService;
    private final MainService mainService;
    private final BasketService basketService;
    private final PaymentService paymentService;


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
  /*  @GetMapping(value = "/member/{fileOriginName}")
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
    }*/

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

    // 강의 썸넬 이미지 url
    @GetMapping(value = "/lecture/thumb/{fileOriginName}")
    public ResponseEntity<Resource> getLectureThumbImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = lectureService.getThumbDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
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

    // 강의 설명 이미지 url
    @GetMapping(value = "/lecture/image/{fileOriginName}")
    public ResponseEntity<Resource> getLectureDescriptionImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = lectureService.getImageDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
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

    // 강의 뱃지 이미지 url
    @GetMapping(value = "/lecture/badge/{fileOriginName}")
    public ResponseEntity<Resource> getLectureBadgeImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = lectureService.getBadgeDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
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

    /*
    * 동영상 가져오기
    */
    @GetMapping(value = "/lecture/video/file/{fileOriginName}")
    public ResponseEntity<Resource> getVideoFile(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = videoService.getVideoFileDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
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

    /*
    * 동영상 썸네일 가져오기
    */
    @GetMapping(value = "/lecture/video/thumb/{fileOriginName}")
    public ResponseEntity<Resource> getVideoThumb(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = videoService.getVideoThumbDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
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

    /* memberService -lsh 서비스*/

    @GetMapping(value = "/member/{fileOriginName}")
    public ResponseEntity<Resource> getProfileImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = memberService.getMemberProfileDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }


    /*searchservice*/
    @GetMapping(value = "/search/member/{fileOriginName}")
    public ResponseEntity<Resource> getSearchMemberImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = searchService.getMemberDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    @GetMapping(value = "/search/lecture/{fileOriginName}")
    public ResponseEntity<Resource> getSearchlectureImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = searchService.getlectureDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    /*profileService*/
    @GetMapping(value = "/profile/member/{fileOriginName}")
    public ResponseEntity<Resource> getProfileMemImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = profileService.getMemberDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    @GetMapping(value = "/profile/lecture/{fileOriginName}")
    public ResponseEntity<Resource> getProfilelectureImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = profileService.getLectureDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    @GetMapping(value = "/profile/badge/{fileOriginName}")
    public ResponseEntity<Resource> getProfilelectureBadgeImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = profileService.getLectureBadgeDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    /*MyLectureListService*/
    @GetMapping(value = "/myLecture/lecture/{fileOriginName}")
    public ResponseEntity<Resource> getMylectureImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = myLectureListService.getLectureDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    /*MainService*/
    @GetMapping(value = "/main/lecture/{fileOriginName}")
    public ResponseEntity<Resource> getMainLectureImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = mainService.getLectureDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    @GetMapping(value = "/main/member/{fileOriginName}")
    public ResponseEntity<Resource> getMainMemberImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = mainService.getMemberDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

    /*basket 이미지 */
    @GetMapping(value = "/basket/{fileOriginName}")
    public ResponseEntity<Resource> getBasketLectureImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = basketService.getLectureDirectoryPath().getPath();
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            System.out.println("fileName: " + fileName);
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }


    /*payment 이미지 */
    @GetMapping(value = "/payment/{fileOriginName}")
    public ResponseEntity<Resource> getPaymentLectureImg(@PathVariable("fileOriginName") String fileName) throws Exception{
        String path = paymentService.getLectureDirectoryPath().getPath();
        return common(path,fileName);
    }

    //공용 이미지 메서드 9월26일 생성
    public ResponseEntity<Resource> common(String path,String fileName) throws Exception{
        try{
            FileSystemResource resource = new FileSystemResource(path + "\\" +fileName);
            if(!resource.exists()){
                throw new Exception("File not found: " + path + "\\" + fileName);

            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception(e.getMessage() + "메세지좀");
        }
    }

}