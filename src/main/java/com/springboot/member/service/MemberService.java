package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.question.entity.Question;
import com.springboot.security.utils.AuthorityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
// 트랜잭션 적용?
@Service
public class MemberService {
//    memberRepository, passwordEncoder와 authorityUtils 권한부여하는 class 들을 DI 받자.
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityUtils authorityUtils;
// 생성자 주입 받았다.
    public MemberService(MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder,
                         AuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
    }

    public Member createMember(Member member) {
//        1. member가 DB에 없는 member인지 확인
//        존재하는 member인지 확인해 보자 -email로. 없어야 member를 생성할 수 있다.
        verifyExistMember(member);
//        2. 암호화된 비밀번호로 바꾸자
//         비밀번호를 암호화 하자! (DB에는 비밀번호를 평문화해서 저장 X. 암호화 해서 저장하자.) 변수명 : 암호화된 패스워드
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
//        member의 비밀번호를 암호화 한 비밀번호로 바꾸자.
        member.setPassword(encryptedPassword);
//        3. 권한정보를 생성하자.
        List<String> roles = authorityUtils.createRoles(member.getEmail());
//        권한정보를 저장하자.
        member.setRoles(roles);
//        4. memberRepository에 저장하자.
        Member saveMember = memberRepository.save(member);
        return saveMember;
    }

    public Member updateMember(Member member) {
//        존재하는 member인지 확인하자. member가 존재해야 한다! 그래야 수정이 가능.
        Member findMember = verifyFindMember(member.getMemberId());
//        비밀번호와 회원 상태 수정이 가능하다. 수정이 있다면 수정된 값을, 없다면 기존 것을 유지.
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(status -> findMember.setMemberStatus(status));
        Optional.ofNullable(member.getPassword())
                .ifPresent(password -> findMember.setPassword(password));
//        비밀번호는 평문으로 저장하지 않기 때문데 인코딩하자.
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        Member patchMember = memberRepository.save(member);
        return patchMember;
    }
//    특정 회원 조회
    public Member findMember(long memberId) {
//    존재하는 회원인지 검증하고 존재한다면 반환하자.
       return verifyFindMember(memberId);
    }
//    전체 회원 조회
    public Page<Member> findMembers(int page, int size) {
        return memberRepository.findAll(PageRequest.of(page, size, Sort.by("memberId").descending()));
    }
//    회원 삭제 member 삭제와 question의 상태 변화는 중간에 끊기면 안되기에 transactional로 관리하자.
    @Transactional //
    public void deleteMember(long memberId) {
//        존재하는 member인지 검증하자.
        Member findMember = verifyFindMember(memberId);
//        member의 상태를 휴면 중이라고 바꾸자.
        findMember.setMemberStatus(Member.MemberStatus.MEMBER_QUIT);
//        휴면상태로 member을 저장. (DB에 실제 삭제X. 상태만 바뀐째로 저장된다.)
        memberRepository.save(findMember);
//        question의 상태를 바꾸자. memberStatus가 탈퇴상태라면
        if(findMember.getMemberStatus() == Member.MemberStatus.MEMBER_QUIT) {
//            해당 member의
            findMember.getQuestions().stream()
                    .forEach(question -> question.setQuestionStatus(Question.QuestionStatus.QUESTION_DEACTIVED));
        }
    }

//    검증 메서드 create시 존재하지 않는 member여야 create이 가능하다.
    public void verifyExistMember(Member member) {
       Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());
       if(optionalMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
       }
    }
//    검증 메서드로 존재하는 member인지 확인하자. 존재해야한다.(Patch, Get, Delete)
    public Member verifyFindMember(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member findMemebr = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMemebr;
    }
//    member의 상태가 Member_ACTIVE 인지 확인 . 아니라면 예외를 던지자.
    public void checkMemberActive(Member member) {
//        멤버가 존재하는지 검증
        Member existMember = verifyFindMember(member.getMemberId());
//        존재하는 회원의 상태가 ACTIVE가 아닐때 예외를 던지자.
        if (existMember.getMemberStatus() != Member.MemberStatus.MEMBER_ACTIVE) {
            throw new BusinessLogicException(ExceptionCode.INACTIVE_MEMBER_FORBIDDEN);
        }
    }


}
