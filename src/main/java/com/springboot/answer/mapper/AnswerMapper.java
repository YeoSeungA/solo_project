package com.springboot.answer.mapper;

import com.springboot.answer.dto.AnswerPatchDto;
import com.springboot.answer.dto.AnswerPostDto;
import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entity.Answer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    Answer answerPostToAnswer(AnswerPostDto answerPostDto);
    Answer answerPatchToAnswer(AnswerPatchDto answerPatchDto);
    AnswerResponseDto answerToAnswerResponseDto(Answer answer);
    List<AnswerResponseDto> answerToAnswerResponseDtos(List<Answer> answers);
}
