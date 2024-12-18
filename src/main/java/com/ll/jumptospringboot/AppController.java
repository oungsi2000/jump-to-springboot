package com.ll.jumptospringboot;

import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ll.jumptospringboot.domain.User.UserCreateForm;

@RequiredArgsConstructor
@Controller
public class AppController {
    private final QuestionService service;
    private final UserService userService;
    private final UserCreateForm userCreateForm;

    @GetMapping("/")
    public String root(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = service.getList(page, kw);
        model.addAttribute("questionList", paging);
        model.addAttribute("kw", kw);
        return "list";
    }


    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userCreateForm", userCreateForm);
        return "signup_form";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }


    @PostMapping("/api/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            userService.create(userCreateForm.getUsername(),
            userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }
}

