package com.springboot.question.entity;

import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
//    Member 필드와 연관짓자
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

//    질문 등록될 때의 날짜 생성
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
//      질문 상태 값 질문 생성시 초기 상태값은 QUESTION_REGISTERED 이다.
    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;
//    공개 글의 여부
    @Enumerated(EnumType.STRING)
    private QuestionPublicStatus questionPublicStatus = QuestionPublicStatus.PUBLIC;
//    member의 입장에서도 연결이 필요하기에
//    member가 갖고 있는 questions(List)에 나 자신 question을 추가한다.
    public void addMemberQuestion(Member member) {
        this.member = member;
        if(!member.getQuestions().contains(this)) {
            member.addQuestion(this);
        }
    }

    public enum QuestionStatus {
        QUESTION_REGISTERED("질문 등록 상태"),
        QUESTION_ANSWERED("답변 완료 상태"),
        QUESTION_DELETED("질문 삭제 상태"),
        QUESTION_DEACTIVED("질문 비활성 상태");

        @Getter
        public String status;

        QuestionStatus(String status) {
            this.status = status;
        }
    }

    public enum QuestionPublicStatus {
        PUBLIC("공개글 상태"),
        SECRET("비밀글 상태");

        @Getter
        public String status;

        QuestionPublicStatus(String status) {
            this.status = status;
        }
    }
}
