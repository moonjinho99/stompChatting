package com.example.chatting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RequiredArgsConstructor
public class HomeController {

    @RequestMapping("/")
    public String index()
    {
        return "redirect:/login-page";
    }


    @GetMapping("/login-page")
    public String loginPage()
    {
        return "login";
    }

    @GetMapping("/sign-up-page")
    public String signUpPage()
    {
        return "signup";
    }

    @GetMapping("/main")
    public String mainPage()
    {
        return "main";
    }
}
