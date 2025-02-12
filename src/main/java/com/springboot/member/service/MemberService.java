package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.security.utils.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    }

//    검증 메서드 create시 존재하지 않는 member여야 create이 가능하다.
    public void verifyExistMember(Member member) {
       Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());
       if(optionalMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
       }
    }
}
