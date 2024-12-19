package com.ll.jumptospringboot.domain.Question;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.User.SiteUser;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, fetch=FetchType.EAGER)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @Column(nullable = false)
    private Integer voter = 0;

    @ManyToMany
    private Set<SiteUser> voterInfo;
}
