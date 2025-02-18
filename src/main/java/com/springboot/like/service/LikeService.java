package com.springboot.like.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.like.entity.Like;
import com.springboot.like.repository.LikeRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private final MemberService memberService;
    private final LikeRepository likeRepository;

    public LikeService(MemberService memberService, LikeRepository likeRepository) {
        this.memberService = memberService;
        this.likeRepository = likeRepository;
    }

    public Like createLike(Like like, Authentication authentication) {
//        1. member를 확인하자
        long memberId = memberService.memberIdFormAuthentication(authentication);
        Member member = memberService.verifyFindMember(memberId);
//        2. like의 questionId를 찾자.
//        long questionId = like.getQuestion().getQuestionId();
//        3. 이미 question에 like 동일 member로 존재한다면 좋아요를 생성할 수 없다. = 좋아요 상태 변경만 가능하다.
        verifyMemberExist(like, member);
//        member가 겹치지 앟는다면 likeCount를 올리자
        likeRepository.save(like);
        return like;
    }

    public Like updateLike(Like like, Authentication authentication) {
//        삼항연산자를 사용하자.

        return null;
    }

//    검증하자. 이미 Like가 들고 있는 memebrID가 겹치면 X.
    public void verifyMemberExist(Like like, Member member) {
        if(like.getMember() == member) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_LIKED);
        }
    }
}
