package com.springboot.like.repository;

import com.springboot.like.entity.Like;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByQuestionAndMember(Question question, Member member);
}
