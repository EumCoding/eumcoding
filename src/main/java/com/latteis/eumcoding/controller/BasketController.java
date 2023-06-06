package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BasketDTO;
import com.latteis.eumcoding.dto.MainNewLectureDTO;
import com.latteis.eumcoding.dto.MainPopularLectureDTO;
import com.latteis.eumcoding.service.BasketService;
import com.latteis.eumcoding.service.MainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.NoSuchElementException;

@Api(tags = "BasketController")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/basket")
public class BasketController {
    private final BasketService basketService;

    @ApiOperation(value = "장바구니 추가", notes = "장바구니 추가")
    @PostMapping("/add")
    public ResponseEntity<?> addBasket(@ApiIgnore Authentication authentication,
                                       @RequestBody BasketDTO.BasketAddDTO basketAddDTO) {
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        try {
            basketService.addBasket(memberId, basketAddDTO);
            return new ResponseEntity<>("장바구니에 담겼습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "장바구니 제거", notes = "장바구니 제거")
    @PostMapping("/delete")
    public ResponseEntity<?> removeBasket(@ApiIgnore Authentication authentication,
                                                  @RequestParam int basketId) {
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        try {
            basketService.removeBasket(memberId, basketId);
            return new ResponseEntity<>("장바구니에서 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/list")
    public ResponseEntity<?> getMyBasketList(@ApiIgnore Authentication authentication,
                                             @RequestParam int page) {
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        List<BasketDTO> basketDTOs = basketService.getBasketlist(memberId, page);
        return new ResponseEntity<>(basketDTOs, HttpStatus.OK);
    }

}