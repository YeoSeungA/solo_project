package com.springboot.question.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    public QuestionService(QuestionRepository questionRepository, MemberService memberService) {
        this.questionRepository = questionRepository;
        this.memberService = memberService;
    }

//    질문을 등록해보자.
    public Question createQuestion(Question question) {
//        멤버가 존재 + 글을 작성할 수 있는 활동상태인지 검증해 보자.
//        멤버가 존재하지 않거나 member의 활동상태가 ACTIVE가 아닐때 예외를 던진다.
        memberService.checkMemberActive(question.getMember());

        Question saveQuestion = questionRepository.save(question);
        return saveQuestion;
    }
//    질문을 수정해보자
    public Question updateQuestion(Question question) {
//        존재하는 question인지 확인해보자.
        Question findQuestion = verifyFindQuestion(question.getQuestionId());
//        제목이 수정될 수 있다.
        Optional.ofNullable(findQuestion.getTitle())
                .ifPresent(title -> findQuestion.setTitle(title));
//        내용이 수정 될 수 있다.
        Optional.ofNullable(findQuestion.getContent())
                .ifPresent(content -> findQuestion.setContent(content));
//        질문 상태가 수정될 수 있다.
        Optional.ofNullable(findQuestion.getQuestionStatus())
                .ifPresent(questionStatus -> findQuestion.setQuestionStatus(questionStatus));
//        질문의 공개여부가 수정될 수 있다.
        Optional.ofNullable(findQuestion.getQuestionPublicStatus())
                .ifPresent(questionPublicStatus -> findQuestion.setQuestionPublicStatus(questionPublicStatus));
        Question patchQuestion = questionRepository.save(findQuestion);

        return findQuestion;
    }
//    secret 모드일 때 get
    public Question getSecretQuestion(long questionId) {
//        해당 question이 있는지 검증하자.
        Question findQeustion =  verifyFindQuestion(questionId);
//        해당 member가 맞는지 검증하자.

//        해당 question이 있는지 검증하자.
        verifyFindQuestion(questionId);
//        두개의 검증이 끝났다면 보여주자.
    }

//    public 모드일 때 get



//    존재하는 question인지 검증해보자
    public Question verifyFindQuestion(long questionId) {
//        repository에서 찾자.
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        Question findQuestion = optionalQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        return findQuestion;
    }
}
