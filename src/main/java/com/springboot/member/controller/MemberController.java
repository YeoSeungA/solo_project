package com.springboot.member.controller;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v12/members")
@Validated
public class MemberController {
    private final static String MEMBER_DEFAULT_URL ="/v12/members";
    private final MemberMapper mapper;
    private final MemberService memberService;

    public MemberController(MemberMapper mapper, MemberService memberService) {
        this.mapper = mapper;
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post memberDto) {
//         service에서 member를 만들자. (dto로 받은 requestbody를 entity로 변환 -> service로 비즈니스 로직 구현)
        Member member = memberService.createMember(mapper.memberPostToMember(memberDto));
//        반환을 위해 entity를 dto로 변환
        MemberResponseDto memberResponseDto = mapper.memberToMemberResponse(member);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity patchMember(@Valid @Positive long memberId ) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
//    단일 조회
    @GetMapping
    public ResponseEntity getMember(@Valid @Positive long memberId) {

    }

    @GetMapping
    public ResponseEntity getMemebrs(@Valid @Positive int page,
                                     int size)
}
