package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.*;
import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.CurriculumService;
import com.latteis.eumcoding.service.MemberService;
import com.latteis.eumcoding.service.MyLectureListService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final CurriculumService curriculumService;

    private final MyLectureListService myLectureListService;



    // 로그인한 정보
    @PostMapping("/info")
    public ResponseEntity<?> viewProfile(@ApiIgnore Authentication authentication) {

        try {
            MemberDTO temp = MemberDTO.builder().id(Integer.parseInt(authentication.getPrincipal().toString())).build();
            MemberDTO responseMemberDTO = memberService.viewProfile(temp);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    //핸드폰번호변경
    @PostMapping("/updatetel")
    public ResponseEntity<?> updatetel(@ApiIgnore Authentication authentication, @RequestBody MemberDTO.UpdateTel updateTel) {

        try {
            String tel = memberService.updateTel(Integer.parseInt(authentication.getPrincipal().toString()), updateTel.getTel());
            if (tel != null || !tel.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().tel(tel).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 주소 변경
    @PostMapping("/updateaddress")
    public ResponseEntity<?> updateAdd(@ApiIgnore Authentication authentication,@RequestBody MemberDTO.UpdateAddress updateAddress) {

        try {
            String address = memberService.updateAddress(Integer.parseInt(authentication.getPrincipal().toString()), updateAddress.getAddress());
            if (address != null || !address.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().address(address).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 닉네임 변경
    @PostMapping("/updatenickname")
    public ResponseEntity<?> updateNickName(@ApiIgnore Authentication authentication,@RequestBody MemberDTO.UpdateNickName updateNickName) {

        try {
            String nickname = memberService.updateNickName(Integer.parseInt(authentication.getPrincipal().toString()), updateNickName.getNickname());
            if (nickname != null || !nickname.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().nickname(nickname).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


    // 비밀번호 변경하기 - 기존 비밀번호 체크 후 원하는 비밀번호로 변경하기
    @PostMapping("/updatepw")
    public ResponseEntity<?> chgPw(@ApiIgnore Authentication authentication, @RequestBody MemberDTO.UpdatePw updatePw) {
        try {
            if (memberService.updatePw(Integer.parseInt(authentication.getPrincipal().toString()), updatePw.getCurPw(),updatePw.getChgPw(), passwordEncoder)) {
                ResponseDTO responseDTO = ResponseDTO.builder().error("성공").build();
                return ResponseEntity.ok().body(responseDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("실패").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }



    // 프로필 이미지 변경
   @PostMapping("/updateProfileImg")
    @ApiOperation(value = "", notes = "")
    public ResponseEntity<?> updateProfileImg(@ApiIgnore Authentication authentication,
                                              @ApiParam(value = "updateProfile", required = true)
                                              @RequestPart(value = "updateProfile", required = false) MemberDTO.UpdateProfile updateProfile,
                                              @RequestPart(value = "profileImgRequest", required = false) MultipartFile[] files) {
        try {
            if (files != null) {
                List<MultipartFile> fileList = Arrays.asList(files);
                updateProfile.setProfileImgRequest(fileList);
            }
            MemberDTO responseMemberDTO = memberService.updateProfileImg(
                    Integer.parseInt(authentication.getPrincipal().toString()),
                    updateProfile);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 프로필 이미지 변경
/*    @PostMapping("/updateProfileImg")
    public ResponseEntity<?> updateProfileImg(@ApiIgnore Authentication authentication, @RequestBody MemberDTO.UpdateProfile memberDTO) {

        try {
            MemberDTO responseMemberDTO = memberService.updateProfileImg(Integer.parseInt(authentication.getPrincipal().toString()),memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }*/


    //내 커리큘럼 목록 확인하기
    @GetMapping("/myplan/list")
    public ResponseEntity<?> getMyPlanList(@ApiIgnore Authentication authentication) {
        try{
            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            List<MyPlanListDTO> myPlanList = curriculumService.getMyPlanList(memberId);
            //return new ResponseEntity<>(myPlanList, HttpStatus.OK);
            return ResponseEntity.ok().body(myPlanList);

        }catch(Exception e)
        {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //내 커리큘럼 timetaken 업데이트 컨트롤러
    @PostMapping("/myplan/update/{curriculumId}")
    public ResponseEntity<?> myPlanUpdate(@ApiIgnore Authentication authentication
                                        ,@PathVariable int curriculumId,@RequestParam int newTimeTaken) {

        if(authentication == null || !authentication.isAuthenticated()){
            return new ResponseEntity<>("로그인을 해주세요",HttpStatus.UNAUTHORIZED);
        }
        try{
            Curriculum updateCurriculum = curriculumService.updateTimeTaken(curriculumId,newTimeTaken);
            return new ResponseEntity<>("timeTaken 변경에 성공했습니다.",HttpStatus.OK);

        }catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/mylecture/list")
    public ResponseEntity<?> getMyLectureList(@ApiIgnore Authentication authentication, @RequestParam int page,@RequestParam int size,@RequestParam int sort) {
        try {
            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            List<MyLectureListDTO> myLectureList = myLectureListService.getMyLectureList(memberId, page, size, sort);
            return ResponseEntity.ok().body(myLectureList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    @PostMapping("/search")
    public List<SearchMylectureDTO> searchMyLecture(@ApiIgnore Authentication authentication,
                                                    @RequestParam int page,
                                                    @RequestParam int sort,
                                                    @RequestParam int size,
                                                    @RequestParam String keyword) {
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        return myLectureListService.getSearchMyLecture(memberId, page, sort, size, keyword);
    }

}