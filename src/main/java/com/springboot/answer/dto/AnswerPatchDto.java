package com.springboot.answer.dto;

import com.springboot.answer.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class AnswerPatchDto {
    private long answerId;

    @NotBlank
    private String content;

    private Answer.AnswerStatus answerStatus;
}
