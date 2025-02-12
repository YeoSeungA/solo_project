package com.springboot.member.entity;

import com.springboot.answer.entity.Answer;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
//    Question 리스트
    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Answer> answers = new ArrayList<>();
// role을 추가한다. (권한)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.MEMBER_SLEEP;

//  question의 입장에서도 연결이 필요하기에 question이 갖고 있는 member에
//  나 자신인 member를 추가한다.
    public void addQuestion(Question question) {
        questions.add(question);
        if(question.getMember() != this) {
            question.addMemberQuestion(this);
        }
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
        if(answer.getMember() != this) {
            answer.addMemberAnswer(this);
        }
    }

    public enum MemberStatus {
        MEMBER_ACTIVE("활동 중"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("탈퇴 상태");

        @Getter
        private String status;

        MemberStatus(String status) {
            this.status = status;
        }
    }
}
