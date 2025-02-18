package com.springboot.question.entity;

import com.springboot.answer.entity.Answer;
import com.springboot.audit.Auditable;
import com.springboot.like.entity.Like;
import com.springboot.member.entity.Member;
import com.springboot.views.entity.Views;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Question extends Auditable {
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ANSWER_ID")
    private Answer answer;
// Views와 다대 일 관계, 영속성 전이는 X. view의 count만 사용하면 되기에.. 필요하면 영속성 전이를 넣어야 한다.
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Views> viewsList = new ArrayList<>();

    @Column(nullable = false)
    private int viewsCount;
//   Like와 일대일관계로 맺자.
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Like> likeList = new ArrayList<>();

//    @Column(nullable = false)
//    private int likeCount;
//      질문 상태 값 질문 생성시 초기 상태값은 QUESTION_REGISTERED 이다.
    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;
//    공개 글의 여부
    @Enumerated(EnumType.STRING)
    private QuestionPublicStatus questionPublicStatus;

    @Enumerated(EnumType.STRING)
    private QuestionRecentStatus questionRecentStatus = QuestionRecentStatus.NEW;

//    member의 입장에서도 연결이 필요하기에
//    member가 갖고 있는 questions(List)에 나 자신 question을 추가한다.
    public void setMember (Member member) {
        this.member = member;
//        순환참조 방지
        if(!member.getQuestions().contains(this)) {
            member.setQuestion(this);
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

    public enum QuestionRecentStatus {
        NEW,
        NOMAL
    }

    public void addAnswer(Answer answer) {
        this.answer = answer;
    }
//    view에 대한 set 함수를 만들어 보자.
    public void setViews(Views views) {
        this.viewsList.add(views);
        if(views.getQuestion() != this) {
            views.setQuestion(this);
        }
    }
//    Like에 대한 set 함수를 만들어보자
    public void setLikes(Like like) {
        this.likeList.add(like);
        if(like.getQuestion() != this) {
            like.setQuestion(this);
        }
    }
}
