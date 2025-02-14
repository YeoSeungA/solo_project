package com.springboot.question.repository;

import com.springboot.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {
//    메서드 쿼리문을 작성해보자.
    Page<Question> findByQuestionStatusNot(Question.QuestionStatus status, Pageable pageable);
}
