package com.springboot.question.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.service.AnswerService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.views.entity.Views;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.springboot.question.entity.Question.QuestionStatus.QUESTION_ANSWERED;
import static com.springboot.question.entity.Question.QuestionStatus.QUESTION_DELETED;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    public QuestionService(QuestionRepository questionRepository, MemberService memberService) {
        this.questionRepository = questionRepository;
        this.memberService = memberService;
    }

//    질문을 등록해보자.
    public Question createQuestion(Question question, Authentication authentication) {
//        멤버가 존재 + 글을 작성할 수 있는 활동상태인지 검증해 보자.
//        멤버가 존재하지 않거나 member의 활동상태가 ACTIVE가 아닐때 예외를 던진다.
//        getMember만 하면 id만
        memberService.checkMemberActive((String)authentication.getPrincipal());
//        emil로 member객체를 찾고 memberId를 뽑아내서 저장하자.
        Member member = memberService.findByEmailToMember((String)authentication.getPrincipal());
        question.setMember(member);
//        question.getMember().setMemberId(member.getMemberId());
//        long memberId = member.getMemberId();
//        question.getMember().setMemberId(memberId);
//        view를 만들자
        question.setViews(new Views());
//        like를 만들자

        Question saveQuestion = questionRepository.save(question);
        return saveQuestion;
    }
//    질문을 수정해보자
    public Question updateQuestion(Question question,Authentication authentication) {
//        존재하는 question인지 확인해보자.
        Question findQuestion = verifyFindQuestion(question.getQuestionId());
//        이미 답변상태라면 질문을 수정할 수 없다.
        if (findQuestion.getQuestionStatus() == QUESTION_ANSWERED) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_QUESTION);
        }
//        1. 권한을 확인하자!(작성자와 ADMIN만 접근할 수 있다.)
//        authentication으로 로그인한 사람이 글의 작성자가 맞는지 id로 비교하자.
        long memberId = memberService.memberIdFormAuthentication(authentication);
//        question 작성자의 id
        long questionMemberId = findQuestion.getMember().getMemberId();
        if (memberId == questionMemberId){
//            3-1. 답변
//            질문의 내용과 제목은 질문을 등록한 사람만 수정할 수 있다.
//        제목이 수정될 수 있다.
                Optional.ofNullable(question.getTitle())
                        .ifPresent(title -> findQuestion.setTitle(title));
//        내용이 수정 될 수 있다.
                Optional.ofNullable(question.getContent())
                        .ifPresent(content -> findQuestion.setContent(content));
//                Optional.ofNullable(findQuestion.getQuestionStatus())
//                        .ifPresent(questionStatus -> findQuestion.setQuestionStatus(questionStatus));
//        질문의 공개여부가 수정될 수 있다.
                Optional.ofNullable(question.getQuestionPublicStatus())
                        .ifPresent(questionPublicStatus -> findQuestion.setQuestionPublicStatus(questionPublicStatus));
////                quesntion이 비밀글이면 answer도 비밀글로, question이 공개글이면 question도 공개글로 바뀌어야 한다.
//            if(findQuestion.getAnswer() != null) {
////                question의 answer을 가져왔다.
//                Answer answer = questionToAnswer(question);
//                findQuestion.setAnswer(answer);
//                findQuestion.getAnswer().setAnswerStatus(question.getAnswer().getAnswerStatus());
//            }
                Question patchQuestion = questionRepository.save(findQuestion);
                return patchQuestion;
            } else {
            throw new BusinessLogicException(ExceptionCode.AUTHOR_ONLY_ACCESS);
        }
    }

    @Transactional
    public Question findQuestion(long questionId, Authentication authentication) {
//        삭제된 질문이거나 없는 질문이면 예외를 던진다.
        Question question = checkQuestionState(questionId);
//        만약 question이 secret이라면 getSecret으로 조회하자.
        if(question.getQuestionPublicStatus() == Question.QuestionPublicStatus.SECRET) {
            return getSecretQuestion(questionId, authentication);
        }
//        view count를 올리자
//        question.setViews(new Views());
        Views views = question.getViews();
        int initViewsCount = views.getViewsCount();
        question.getViews().setViewsCount(initViewsCount + 1);
        question.setViews(views);
        questionRepository.save(question);
//        question이 public이라면 로그인한 회원과 관리자 모두 조회 가능하다.
        return question;
    }

    public Page<Question> findQuestions(int page, int size, String sort) {
//        답변도 함께 조회할 수 있다.--> 나중에 구현해보자 구현 ok.
//        정렬해서 조회할수 있어야 한다. --> 다른것을 구현해보고 실행해보자
//        삭제상태가 아닌 질문만 조회할 수 있다...??? -- findByStatusNot 메서드 쿼리 기능을 사용해보자 ok.
        return questionRepository.findByQuestionStatusNot(QUESTION_DELETED,
                (PageRequest.of(page, size,Sort.by("questionId").descending())));

    }

    public void deleteQuestion(long questionId, Authentication authentication) {
//        존재하는 질문인지 확인 + 삭제된 질문은 삭제할 수 없다  검증.
        Question question = checkQuestionState(questionId);
//        1. 질문삭제는 질문을 등록한 회원만 가능하다. - 다른 메서드로 따로 빼도록 하자
//        1-1. authenticaion으로 email 을 불러오자
        String username = authentication.getPrincipal().toString();
//        1-2. memberEmail를 통해 memeber를 구하자.
        Member member = memberService.findByEmailToMember(username);
//        1-3 로그인한 memberId를 구하자.
        long loginMemberId = member.getMemberId();
//        1-4. question의 memberId를 구하자
        long questionMemberId = question.getMember().getMemberId();
//        1-5. 현재 로그인한 사람과 question을 작성한 사람의 email을 비교하자.
//        같을 때만 delete가능하다. 다르다면 예외를 던지자.
        if(loginMemberId != questionMemberId) {
            throw new BusinessLogicException(ExceptionCode.AUTHOR_ONLY_ACCESS);
        }
//        질문삭제시, 테이블에서 row가 삭제되는게 아닌 질문 상태값이 바뀐다.
        question.setQuestionStatus(QUESTION_DELETED);
        questionRepository.save(question);
    }


//    secret 모드일 때 get Controller에서 Authentication.principle로 memeber를 보내주자.
    public Question getSecretQuestion(long questionId, Authentication authentication) {
//        해당 question이 존재하는지 + 삭제 상태인지 검증해보자. question이 없거나 삭제상태이면 예외를 던진다.
        Question findQuestion =  checkQuestionState(questionId);
        String username = authentication.getPrincipal().toString();
//        username = email로 member 객체를 불러오자.
        Member member = memberService.findByEmailToMember(username);
//        question 작성자와 로그인한 사람이 같지 않고 권한이 ADMIN이 아닐때 예외를 던지자.
        if(!findQuestion.getMember().getEmail().equals(username) && !member.getRoles().contains("ADMIN")) {
            throw new BusinessLogicException(ExceptionCode.AUTHOR_ONLY_ACCESS);
        }
//         검증이 끝났다면 보여주자.
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
    public List<Answer> checkAnswerToQuestion(long questionId) {
        List<Answer> answers = new ArrayList<>();
//        Id로 question을 찾자
       Question findQuestion = verifyFindQuestion(questionId);
//       Question이 answer를 갖고 있다면 즉 answer이 null이라면
        if(findQuestion.getAnswer().getAnswerId() != null) {
//            List<Answer>에 질문을 추가하고 반환하자.
            answers.add(findQuestion.getAnswer());
            return answers;
//       question에 answer이 없다면 빈배열을 반환하자.
        } return answers;
    }
//    이미 삭제 상태인 질문은 조회할 수 없다.
    public Question checkQuestionState(long questionId) {
        Question question = verifyFindQuestion(questionId);
        if(question.getQuestionStatus() == QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND);
        }
        return question;
    }
////    해당 question의 answer객체를 뽑아보자.
//    public Answer questionToAnswer (Question question) {
//        long answerId = question.getAnswer().getAnswerId();
//        return answer;
//    }

}
