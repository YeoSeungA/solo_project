package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final MemberService memberService;

    public AnswerService(AnswerRepository answerRepository, MemberService memberService) {
        this.answerRepository = answerRepository;
        this.memberService = memberService;
    }

    public Answer createAnswer(Answer answer) {
//        해당 answer가 없는지 확인하는 로직이 필요하다.
        return null;
    }

//    검증 로직
    public void verifyExistAnswer(Answer answer) {
//        1. member가 존재하는지 - 존재X 예외
        Member member = memberService.verifyFindMember(answer.getMember().getMemberId());
//        2. question이 존재하는지.
//        이미 등록된 답변이 있으면 answer을 할 수 없다.
//        해당 answer을 찾고 answer이 갖고 있는 question의 id가 같으면 등록 X.
    }
}
