package com.springboot.like.entity;

import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Entity
@Getter
@NoArgsConstructor
@Table(name = "Love")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

//    @Column(nullable = false)
//    private int likeCount;
    @Enumerated
    private LikeStatus likeStatus = LikeStatus.NONE;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void setQuestion(Question question) {
        this.question = question;
        if(!question.getLikeList().contains(this)) {
            question.setLikes(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
        if(!member.getLikes().contains(this)) {
            member.setLike(this);
        }
    }

    public enum LikeStatus {
        LIKE,
        NONE
    }
}
