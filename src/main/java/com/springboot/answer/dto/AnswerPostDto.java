package com.springboot.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class AnswerPostDto {
    @NotBlank
    private String content;
}
