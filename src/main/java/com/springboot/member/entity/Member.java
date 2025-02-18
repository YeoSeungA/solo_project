package com.springboot.member.entity;

import com.springboot.answer.entity.Answer;
import com.springboot.like.entity.Like;
import com.springboot.question.entity.Question;
import com.springboot.views.entity.Views;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
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
//    @Column(nullable = false)
    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Answer> answers = new ArrayList<>();
////    view와 다대일 관계 . 영속성 전이는 X. count만 알면 되기에
    @OneToMany(mappedBy = "member")
    private List<Views> views = new ArrayList<>();
////    like와 다대 일 관계, 영속성 전이는 X. count만 알면 되기에
    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Like> likes = new ArrayList<>();
// role을 추가한다. (권한)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

//  question의 입장에서도 연결이 필요하기에 question이 갖고 있는 member에
//  나 자신인 member를 추가한다.
    public void setQuestion(Question question) {
        questions.add(question);
//        순환참조 방지
        if(question.getMember() != this) {
            question.setMember(this);
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

    public void setLike(Like like) {
        this.likes.add(like);
        if(like.getMember() != this) {
            like.setMember(this);
        }
    }

}
