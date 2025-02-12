package com.springboot.member.controller;

import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v12/members")
@Validated
public class MemberController {
    private final static String MEMBER_DEFAULT_URI
    private final MemberMapper mapper;
    private final MemberService memberService;
}
