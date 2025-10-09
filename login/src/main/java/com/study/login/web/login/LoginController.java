package com.study.login.web.login;

import com.study.login.domain.login.LoginService;
import com.study.login.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm loginForm,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }
        // 로그인 성공 처리 TODO
        return "redirect:/";
    }
}
