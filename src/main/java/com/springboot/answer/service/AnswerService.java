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
    public Answer createAnswer(Answer answer, Authentication authentication) {
//         멤버가 있는지 + question이 존재하는지 확인하자.
        Question question = verifyExistAnswer(answer);
//        멤버의 권한이 admin인지 확인하자.
        verifyAuthorities(authentication);
//        답변은 한 건만 등록할 수 있다.
        if(answer.getQuestion().getQuestionId() == null) {
//            question의 상태에 따른다. public 이면 public
            if(question.getQuestionPublicStatus() == Question.QuestionPublicStatus.PUBLIC) {
                answer.setAnswerStatus(Answer.AnswerStatus.PUBLIC);
//                secret이면 secret으로
            } else {answer.setAnswerStatus(Answer.AnswerStatus.SECRET);}
//            답변 등록시, 질문의 상태값이 QUESTION_ANSWERED로 변경되야 한다.
            answer.getQuestion().setQuestionStatus(Question.QuestionStatus.QUESTION_ANSWERED);
           Answer result = answerRepository.save(answer);
        } throw new BusinessLogicException(ExceptionCode.ANSWER_EXISTS);
    }

    public Answer updateAnswer(Answer answer, Authentication authentication) {
//        answer가 존재하는지 확인하자
        Answer findAnswer = verifyAnswer(answer.getAnswerId());
//        권한을 확인하자.
        verifyAuthorities(authentication);
//        관리자만이 수정할 수 있다.
        Optional.ofNullable(answer.getContent())
                .ifPresent(content -> findAnswer.setContent(content));
        Answer saveAnswer = answerRepository.save(findAnswer);
        return saveAnswer;
    }



//    검증 로직
    public Question verifyExistAnswer(Answer answer) {
//        1. member가 존재하는지 - 존재X 예외
        memberService.verifyFindMember(answer.getMember().getMemberId());
//        2. question이 존재하는지.
        Question question = questionService.verifyFindQuestion(answer.getQuestion().getQuestionId());
        return question;
    }
//    권한을 갖고 있는가
    public void verifyAuthorities(Authentication authentication) {
        //        멤버의 권한이 admin인지 확인하자.
        List<String> roles = authentication.getAuthorities().stream()
                .map(role->role.getAuthority())
                .collect(Collectors.toList());
        if(!roles.contains("ADMIN")) {
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
