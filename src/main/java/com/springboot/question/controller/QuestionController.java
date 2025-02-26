package com.springboot.question.controller;

import com.springboot.question.dto.QuestionPatchDto;
import com.springboot.question.dto.QuestionPostDto;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.entity.Question;
import com.springboot.question.mapper.QuestionMapper;
import com.springboot.question.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

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
//  클라이언트가 JSON 데이터와 이미지 파일을 함께 업로드하면 이를 처리하고 받환한다.
// JSON 데이터와 파일업로드(multipart/form-data)요청을 모두 받을 수 있다.
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    RequestPart는 multipart/form-data 요청에서 특정 부분을 가져와 객체로 변환해준다.
    public ResponseEntity postQuestion(@Valid @RequestPart QuestionPostDto questionPostDto,
//                                       MultipartFile 은 Spring에서 파일 업로드를 처리하는 객체이다.
                                       @RequestPart MultipartFile image,
                                       Authentication authentication) {
////        public상태는 대문자로 변환하자.
//        questionPostDto.getQuestionPublicStatus().toString().toUpperCase();
        Question question = questionService.createQuestion(questionMapper.questionPostDtoToQuestion(questionPostDto),image,authentication);
        QuestionResponseDto questionResponseDto = questionMapper.questionToQuestionResponseDto(question);

        return new ResponseEntity<>(questionResponseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity patchQuestion(@PathVariable("question-id") @Valid long questionId,
                                        @Valid @RequestBody QuestionPatchDto questionPatchDto,
                                        Authentication authentication) {
        questionPatchDto.setQuestionId(questionId);
        Question question = questionService.updateQuestion(questionMapper.questionPatchDtoToQuestion(questionPatchDto), authentication);
        QuestionResponseDto questionResponseDto = questionMapper.questionToQuestionResponseDto(question);

        return new ResponseEntity<>(questionResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{question-id}")
    public ResponseEntity getQuestion(@Valid @PathVariable("question-id") long questionId,
                                      Authentication authentication) {
        Question question = questionService.findQuestion(questionId, authentication);
        QuestionResponseDto questionResponseDto = questionMapper.questionToQuestionResponseDto(question);

        return new ResponseEntity(questionResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getQuestions(@Positive @RequestParam int page,
                                       @Positive @RequestParam int size,
                                       @Positive @RequestParam String sort) {
        Page<Question> questionPage = questionService.findQuestions(page-1,size,sort);
        List<Question> questions = questionPage.getContent();
        List<QuestionResponseDto> questionResponseDtos = questionMapper.questionToQuestionResponseDtos(questions);
        return new ResponseEntity<>(questionResponseDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{question-id}")
    public ResponseEntity deleteQuestion(@Positive @PathVariable("question-id") long questionId,
                                         Authentication authentication) {
        questionService.deleteQuestion(questionId, authentication);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{question-id}/loves")
    public ResponseEntity postLoves(@Positive @PathVariable("question-id") long questionId,
                                    Authentication authentication) {
        questionService.toggleLike(questionId, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

}
