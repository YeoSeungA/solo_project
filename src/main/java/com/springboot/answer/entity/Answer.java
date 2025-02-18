package com.springboot.answer.entity;

import com.springboot.audit.Auditable;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Entity
@Getter
@NoArgsConstructor
public class Answer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(nullable = false)
    private String content;
//영속성 전이 실행.
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
//    단방향을 하기 위해 Question 필드를 제거했다.
////    Question과 1대 1 관계 영속성 전이
    @OneToOne(mappedBy = "answer", cascade = CascadeType.PERSIST)
    private Question question;

    @Enumerated(EnumType.STRING)
    private AnswerStatus answerStatus;

//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AnswerStatus {
        PUBLIC,
        SECRET
    }

    public void addMemberAnswer(Member member) {
        this.member = member;
        if(!member.getAnswers().contains(this)) {
            member.addAnswer(this);
        }
    }

}
