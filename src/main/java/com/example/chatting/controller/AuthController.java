package com.example.chatting.controller;


import com.example.chatting.entity.Member;
import com.example.chatting.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private Map<String,Object> responseMap = new HashMap<>();

    @Autowired
    AuthService authService;

    @PostMapping("sign-up")
    public @ResponseBody  Map<String,Object> signUp(@RequestBody  Map<String,Object> body)
    {
        responseMap = authService.signup(body);
        return responseMap;
    }

    @PostMapping("login")
    public @ResponseBody Map<String,Object> login(@RequestBody Map<String,Object> body){
        responseMap = authService.login(body);
        return responseMap;
    }

    @PostMapping("reissue")
    public @ResponseBody Map<String,Object> reissue(@RequestBody Map<String,String> body){
        responseMap = authService.reissue(body);
        return responseMap;
    }


}
