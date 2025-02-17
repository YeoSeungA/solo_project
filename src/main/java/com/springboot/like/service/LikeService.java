package com.springboot.like.service;

import com.springboot.like.entity.Like;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private final MemberService memberService;

    public LikeService(MemberService memberService) {
        this.memberService = memberService;
    }

    public Like createLike(Like like, Authentication authentication) {
//        1. member를 확인하자
        long memberId = memberService.memberIdFormAuthentication(authentication);
        Member member = memberService.verifyFindMember(memberId);
//        2. like의 questionId를 찾자.
        long questionId = like.getQuestion().getQuestionId();
//        3. 이미 question에 like 동일 member로 존재한다면 좋아요를 생성할 수 없다. = 좋아요 상태 변경만 가능하다.
        if(questionId ==   )
        return null;
    }
}
