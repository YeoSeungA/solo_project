package com.springboot.answer.entity;

import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(nullable = false)
    private String answerTitle;

    @Column(nullable = false)
    private String answerContent;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void addMemberAnswer(Member member) {
        this.member = member;
        if(!member.getAnswers().contains(this)) {
            member.addAnswer(this);
        }
    }

}
