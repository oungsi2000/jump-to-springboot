package com.ll.jumptospringboot.domain.Comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    @NotEmpty(message="id는 필수 항목입니다")
    private Integer id;
}

