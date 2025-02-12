package com.springboot.member.dto;

import com.springboot.answer.entity.Answer;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private long memberId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private Member.MemberStatus memberStatus;
//    @Valid
    private List<Question> questions;
//    @Vaild
    private List<Answer> answers;

////    questionId만 알아도 question을 불러올 수 있다.
//    public Question getQuestion() {
//        Question question = new Question();
//        question.setQuestionId();
//    }
}
