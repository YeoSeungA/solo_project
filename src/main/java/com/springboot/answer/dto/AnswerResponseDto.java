package com.springboot.answer.dto;

import com.springboot.answer.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AnswerResponseDto {
    private long answerId;

    @NotBlank
    private String content;

    private Answer.AnswerStatus answerStatus;

    private LocalDateTime createdAt;
}
