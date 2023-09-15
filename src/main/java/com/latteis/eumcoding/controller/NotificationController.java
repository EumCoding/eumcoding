package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.ApiIgnore;

@Controller
public class NotificationController{

    @Autowired
    private NotificationService notificationService;

    // 사용자가 로그인할 때 호출되는 메서드
    public void onUserLogin(@ApiIgnore Authentication authentication) {
        notificationService.sendNotification(authentication.getPrincipal().toString(), "환영합니다! 로그인이 성공적으로 완료되었습니다.");
    }
}