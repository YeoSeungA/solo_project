package com.springboot.question.dto;

import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuestionResponseDto {
    private long questionId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Question.QuestionStatus questionStatus;

    private Question.QuestionPublicStatus questionPublicStatus;

}
