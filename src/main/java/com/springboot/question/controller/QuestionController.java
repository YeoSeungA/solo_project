package com.springboot.question.controller;

import com.springboot.question.dto.QuestionPatchDto;
import com.springboot.question.dto.QuestionPostDto;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.entity.Question;
import com.springboot.question.mapper.QuestionMapper;
import com.springboot.question.service.QuestionService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v12/questions")
@Validated
public class QuestionController {
    private final static String QUESTION_DEFAULT_URL = "/v12/questions";
    private final QuestionMapper questionMapper;
    private final QuestionService questionService;

    public QuestionController(QuestionMapper questionMapper, QuestionService questionService) {
        this.questionMapper = questionMapper;
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity postQuestion(@Valid @RequestBody QuestionPostDto questionPostDto) {
        Question question = questionService.createQuestion(questionMapper.questionPostDtoToQuestion(questionPostDto));
        QuestionResponseDto questionResponseDto = questionMapper.questionToQuestionResponseDto(question);

        return new ResponseEntity(questionResponseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity patchQuestion(@Valid @RequestBody QuestionPatchDto questionPatchDto) {
        Question question = questionService.updateQuestion(questionMapper.questionPatchDtoToQuestion(questionPatchDto));
        QuestionResponseDto questionResponseDto = questionMapper.questionToQuestionResponseDto(question);

        return new ResponseEntity<>(questionResponseDto, HttpStatus.OK);
    }

}
