package com.latteis.eumcoding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 알림 서비스

@Service
public class NotificationService{

    // 사용자 ID와 연결된 SseEmitter를 저장할 맵입니다.
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 연결 설정
    @GetMapping("/notification")
    public SseEmitter handleSse() {
        String userId = "someUserId"; // 실제로는 세션 등을 통해 식별한 사용자의 ID를 사용하게 됩니다.
        SseEmitter emitter = new SseEmitter();

        // 사용자 ID와 SseEmitter 객체를 맵에 저장
        emitters.put(userId, emitter);

        // 연결이 끊어지면 맵에서 제거
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    // 알림 발송 메서드
    public void sendNotification(String userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                e.printStackTrace();
                emitters.remove(userId);
                emitter.complete();
            }
        }
    }
}