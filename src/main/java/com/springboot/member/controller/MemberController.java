package com.springboot.member.controller;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

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

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@PathVariable("member-id") @Positive long memberId,
                                      @Valid @RequestBody MemberDto.Patch memberPathDto) {
//        해당 memebrId에 맞는 member를 가져와야 하기에
        memberPathDto.setMemberId(memberId);
//        mapper로 entity로 변환 후 service로 비즈니스 로직을 구혀한다.
        Member member = memberService.updateMember(mapper.memberPatchToMember(memberPathDto));
//        비즈니스로직을 거친 member를 entity 로 다시 변환하자.
        MemberResponseDto memberResponseDto = mapper.memberToMemberResponse(member);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//    단일 조회
    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") @Positive long memberId) {
        Member member = memberService.findMember(memberId);
//        mapper로 dto로 변화 후 반환
        MemberResponseDto memberResponseDto = mapper.memberToMemberResponse(member);
        return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getMembers(@Positive @RequestParam int page,
                                     @Positive @RequestParam int size) {
        Page<Member> pageMembers = memberService.findMembers(page, size);
        List<Member> members = pageMembers.getContent();
//        mapper로 dto가 보이게끔 해보자!
        List<MemberResponseDto> memberResponseDtos = mapper.memberToMemberResponses(members);

        return new ResponseEntity<>(memberResponseDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@Positive @PathVariable("member-id") long memberId) {
        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
