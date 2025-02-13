package com.springboot.question.dto;

import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class QuestionPostDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Question.QuestionStatus questionStatus;

}
