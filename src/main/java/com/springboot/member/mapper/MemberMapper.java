package com.springboot.member.mapper;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberResponseDto;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
    public interface MemberMapper {
//    Post Dto로 들어오면 Memebr로 바꾸자
    Member memberPostToMember(MemberDto.Post requestBody);
//    Patch Dto로 들어오면 Memeber로 바꾸자
    Member memberPatchToMember(MemberDto.Patch requestBody);
//    Member로 들어오면 memberResponseDto로 바꾸자
//    @Mapping(target = "questionId", source = "question.questionId")
    MemberResponseDto memberToMemberResponse(Member member);
//    전체 조회하기 위해 List<Member>를 List<MemberResponseDto>로 바꾸자.
    List<MemberResponseDto> memberToMemberResponses(List<Member> members);

//    post mapper는 수동구현해서 memberId와 email, memberStatus만 보이게 하자.

}
