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
    @Mapping(target="memberId",source = "member.memberId")
    @Mapping(target="answerResponseDto.content", source = "answer.content")
    @Mapping(target="viewsCount", source = "views.viewsCount")
    QuestionResponseDto questionToQuestionResponseDto(Question question);
    List<QuestionResponseDto> questionToQuestionResponseDtos(List<Question> question);
}
