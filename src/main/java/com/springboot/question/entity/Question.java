package com.springboot.question.entity;

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

//    질문 등록될 때의 날짜 생성
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
//      질문 상태 값 질문 생성시 초기 상태값은 QUESTION_REGISTERED 이다.
    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;
//    공개 글의 여부
    @Enumerated(EnumType.STRING)
    private QuestionPublicStatus questionPublicStatus = QuestionPublicStatus.PUBLIC;

    public enum QuestionStatus {
        QUESTION_REGISTERED("질문 등록 상태"),
        QUESTION_ANSWERED("답변 완료 상태"),
        QUESTION_DELETED("질문 삭제 상태"),
        QUESTION_DEACTIVED("질문 비솰성 상태");

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
