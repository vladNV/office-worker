package com.org.worker.controller;

import com.org.worker.repository.PlainTextRepository;
import com.org.worker.util.Crypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PlainTextRepository plainTextRepository;

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String auth(@RequestParam("username") @NotBlank String login,
                       @RequestParam("password") @NotBlank String password,
                       @NotNull final HttpSession session) {
        String encrypted = plainTextRepository.authenticate(login, password);
        session.setAttribute("token", Crypto.encrypt(login + "_" + encrypted));
        return "redirect:/document";
    }

    @PostMapping("/logout")
    public String logout(@NotNull final HttpSession session) {
        session.invalidate();
        return "redirect:/users";
    }


}
