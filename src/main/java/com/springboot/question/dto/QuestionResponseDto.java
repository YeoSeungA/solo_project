package com.springboot.question.dto;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entity.Answer;
import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuestionResponseDto {
    private long questionId;

    private long memberId;

    private Question.QuestionRecentStatus questionRecentStatus;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Question.QuestionStatus questionStatus;

    private Question.QuestionPublicStatus questionPublicStatus;

    private String questionImageName;

    private LocalDateTime createdAt;

    private AnswerResponseDto answerResponseDto;

    private long viewsCount;

    private long likeCount;


}
