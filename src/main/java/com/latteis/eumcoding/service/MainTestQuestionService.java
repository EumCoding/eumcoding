package com.latteis.eumcoding.service;

import com.latteis.eumcoding.persistence.MainTestQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainTestQuestionService {

    private final MainTestQuestionRepository mainTestQuestionRepository;
}
