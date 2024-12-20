package com.ll.jumptospringboot.domain.Comment;

import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    private SiteUser author;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Answer answer;
}
