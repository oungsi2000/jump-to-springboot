package com.ll.jumptospringboot.domain.Question;

import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Answer.AnswerForm;
import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.domain.Comment.CommentForm;
import com.ll.jumptospringboot.domain.Comment.CommentService;
import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class QuestionController {
    private final QuestionService service;
    private final UserService userService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @GetMapping(value = "/question/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         AnswerForm answerForm,
                         CommentForm commentForm,
                         @RequestParam(value="index", defaultValue="0") int idx,
                         @RequestParam(value="sortBy", defaultValue="mostVoted") String sortBy) {
        Question question = service.getQuestion(id);
        Page<Answer> answerList = answerService.getList(question, idx, sortBy);
        List<Comment> questionComments = commentService.getQuestionComments(question);
        Map<Integer, List<Comment>> answerComments = commentService.getAnswerComments(question);

        model.addAttribute("questionComments", questionComments);
        model.addAttribute("answerComments", answerComments);
        model.addAttribute("question", question);
        model.addAttribute("answerList", answerList);
        return "question_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.service.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getTitle());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = this.service.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.service.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.service.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.service.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.service.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.service.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.service.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/"; // 질문 저장후 질문목록으로 이동
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/create/comment")
    public String questionComment(@Valid CommentForm commentForm,
                                  BindingResult bindingResult,
                                  Principal principal,
                                  HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        if (bindingResult.hasErrors()) {
            if (referer != null) {
                return "redirect:" + referer;
            } else {
                return "redirect:/";
            }
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        commentService.createQuestionComment(siteUser, commentForm.getContent(), commentForm.getId());
        if (referer != null) {
            return "redirect:" + referer;
        } else {
            return "redirect:/";
        }
        // 질문 저장후 질문목록으로 이동
    }
}

