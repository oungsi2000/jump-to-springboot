package com.ll.jumptospringboot.domain.Comment;

import com.ll.jumptospringboot.domain.Question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByQuestion(Question question, Pageable pageable);
}
