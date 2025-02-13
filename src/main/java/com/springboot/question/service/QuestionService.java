package com.springboot.question.service;

import com.springboot.answer.entity.Answer;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.security.core.Authentication;
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
//        getMember만 하면 id만
        memberService.checkMemberActive(question.getMember().getMemberId());

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
//    secret 모드일 때 get Controller에서 Authentication.principle로 memeber를 보내주자.
    public Question getSecretQuestion(long questionId, Authentication authentication) {
//        해당 question이 존재하는지 + 삭제 상태인지 검증해보자. question이 없거나 삭제상태이면 예외를 던진다.
        Question findQeustion =  checkQuestionState(questionId);
//        quesion 작성자와 로그인한 사람이 같지 않거나 권한이 ADMIN이 아닐때 예외를 던지자.
        Member member = (Member) authentication.getPrincipal();
        if(!findQeustion.getMember().getEmail().equals(member.getEmail()) || !member.getRoles().contains("ADMIN")) {
            throw new BusinessLogicException(ExceptionCode.AUTHOR_ONLY_ACCESS);
        }
//         검증이 끝났다면 보여주자.
        return findQeustion;
    }

//    public 모드일 때 get
    public Question getPublicQuestion(long questionId) {
//        해당 question이 있는지 + 삭제 상태인지 검증
        Question findQuestion = checkQuestionState(questionId);
        return findQuestion;
    }



//    존재하는 question인지 검증해보자
    public Question verifyFindQuestion(long questionId) {
//        repository에서 찾자.
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        Question findQuestion = optionalQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        return findQuestion;
    }
//    해당 질문에 대한 답변이 존재한다면 답변도 함께 조회해 보자. Optional로 null 처리를 안전하게 해보자. 개선이 필요하긴 하다.
    public Optional<Answer> checkAnswerToQuestion(long questionId) {
//        Id로 question을 찾자
       Question findQuestion = verifyFindQuestion(questionId);
//       question에 answer이 있다면 반환하고 없다면 그냥 반환하지 말자.
       return Optional.ofNullable(findQuestion.getAnswer());
    }
//    이미 삭제 상태인 질문은 조회할 수 없다.
    public Question checkQuestionState(long questionId) {
        Question question = verifyFindQuestion(questionId);
        if(question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND);
        }
        return question;
    }
}
