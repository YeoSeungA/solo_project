package com.springboot.answer.controller;

import com.springboot.answer.dto.AnswerPostDto;
import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entity.Answer;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.answer.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v12/questions")
@Validated
public class AnswerController {
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;

    public AnswerController(AnswerService answerService, AnswerMapper answerMapper) {
        this.answerService = answerService;
        this.answerMapper = answerMapper;
    }

    @PostMapping("/{question-id}/answers")
    public ResponseEntity postAnswer(@PathVariable("question-id") long questionId,
                                     @Valid @RequestBody AnswerPostDto answerPostDto,
                                     Authentication authentication) {
        Answer answer = answerService.createAnswer(questionId, answerMapper.answerPostToAnswer(answerPostDto), authentication);
        AnswerResponseDto answerResponseDto = answerMapper.answerToAnswerResponseDto(answer);
        return new ResponseEntity<>(answerResponseDto, HttpStatus.OK);
    }
}
