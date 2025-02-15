package com.springboot.question.mapper;

import com.springboot.question.dto.QuestionPatchDto;
import com.springboot.question.dto.QuestionPostDto;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    Question questionPostDtoToQuestion(QuestionPostDto postDto);
    Question questionPatchDtoToQuestion(QuestionPatchDto patchDto);
    QuestionResponseDto questionToQuestionResponseDto(Question question);
    @Mapping(target="memberId",source = "member.memberId")
    List<QuestionResponseDto> questionToQuestionResponseDtos(List<Question> question);
}
