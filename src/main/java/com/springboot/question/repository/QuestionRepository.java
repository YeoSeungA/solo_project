package com.springboot.question.repository;

import com.springboot.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
////    Secret상태인 question를 찾아보자.
//    Optional<Question> findByQuestionPublicStatus(Question.QuestionPublicStatus status);
    //    메서드 쿼리문을 작성해보자.
    Page<Question> findByQuestionStatusNot(Question.QuestionStatus status, Pageable pageable);
}
