package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionService questionService;
    private final MemberService memberService;

    public AnswerService(AnswerRepository answerRepository,
                         QuestionService questionService,
                         MemberService memberService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
        this.memberService = memberService;
    }

    @Transactional
    public Answer createAnswer(long questionId, Answer answer, Authentication authentication) {
//         멤버를 구하자!
        long memberId = memberService.memberIdFormAuthentication(authentication);
        Member member = memberService.verifyFindMember(memberId);
        answer.setMember(member);
//        질문을 구하자!
        Question question = verifyExistAnswer(answer, questionId);
//        answer.setQuestion(question);

//        멤버의 권한이 admin인지 확인하자.
        verifyAuthorities(answer.getMember());
//        답변은 한 건만 등록할 수 있다.
        if(question.getQuestionStatus() == Question.QuestionStatus.QUESTION_REGISTERED) {
//            question의 상태에 따른다. public 이면 public
            if(question.getQuestionPublicStatus() == Question.QuestionPublicStatus.PUBLIC) {
                answer.setAnswerStatus(Answer.AnswerStatus.PUBLIC);
//                secret이면 secret으로
            } else {answer.setAnswerStatus(Answer.AnswerStatus.SECRET);}
//            답변 등록시, 질문의 상태값이 QUESTION_ANSWERED로 변경되야 한다.
            question.setQuestionStatus(Question.QuestionStatus.QUESTION_ANSWERED);
           Answer result = answerRepository.save(answer);
           question.setAnswer(result);
           return result;
        } throw new BusinessLogicException(ExceptionCode.ANSWER_EXISTS);
    }

    public Answer updateAnswer(long answerId, Answer answer, Authentication authentication) {
        answer.setAnswerId(answerId);
//        Member를 뽑자.
        long memberId = memberService.memberIdFormAuthentication(authentication);
        Member member = memberService.findMember(memberId);
        answer.setMember(member);
//        answer가 존재하는지 확인하자
        Answer findAnswer = verifyAnswer(answer.getAnswerId());
//        권한을 확인하자.
        verifyAuthorities(answer.getMember());
//        관리자만이 수정할 수 있다.
        Optional.ofNullable(answer.getContent())
                .ifPresent(content -> findAnswer.setContent(content));
        Answer saveAnswer = answerRepository.save(findAnswer);
        return saveAnswer;
    }

    public void deleteAnswer(long answerId, Authentication authentication) {
//        권한이 있는지 확인해보자
        long memberId = memberService.memberIdFormAuthentication(authentication);
        Member member = memberService.verifyFindMember(memberId);
        if(!member.getRoles().contains("ADMIN")) {
            throw new BusinessLogicException(ExceptionCode.ADMIN_ONLY_ACCESS);
        }
        Answer answer = verifyAnswer(answerId);
//        Question과의 연결 끊기!
        if(answer.getQuestion() != null) {
            answer.getQuestion().setAnswer(null);
            answer.getQuestion().setQuestionStatus(Question.QuestionStatus.QUESTION_REGISTERED);

        }
        answerRepository.delete(answer);
    }
//    검증 로직
    public Question verifyExistAnswer(Answer answer, long questionId) {
//        1. member가 존재하는지 - 존재X 예외
        memberService.verifyFindMember(answer.getMember().getMemberId());
//        2. question이 존재하는지.
        Question question = questionService.verifyFindQuestion(questionId);
        return question;
    }
//    권한을 갖고 있는가
    public void verifyAuthorities(Member member) {
        //        멤버의 권한이 admin인지 확인하자.
        if(!member.getRoles().contains("ADMIN")) {
            throw new BusinessLogicException(ExceptionCode.ADMIN_ONLY_ACCESS);
        }
    }
//    answer가 존재하는가
    public Answer verifyAnswer(long answerId) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        Answer findAnswer = optionalAnswer.orElseThrow(()->
                new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
        return findAnswer;
    }

}
