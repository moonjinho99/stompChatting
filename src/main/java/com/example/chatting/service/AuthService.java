package com.example.chatting.service;

import com.example.chatting.config.Authority;
import com.example.chatting.dto.JwtToken;
import com.example.chatting.dto.MemberDto;
import com.example.chatting.dto.RefreshTokenDto;
import com.example.chatting.entity.Member;
import com.example.chatting.entity.RefreshToken;
import com.example.chatting.jwt.JwtTokenProvider;
import com.example.chatting.repository.MemberRepository;
import com.example.chatting.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Ref;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private Map<String,Object> responseMap = new HashMap<>();


    @Transactional
    public Map<String,Object> signup(Map<String, Object> body){
        String email = (String)body.get("email");
        String password = (String)body.get("password");
        if(memberRepository.existsByEmail(email)){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        MemberDto memberDto = MemberDto.builder().email(email).password(passwordEncoder.encode(password)).authority(Authority.ROLE_USER).build();
        memberRepository.save(Member.toEntity(memberDto));

        responseMap.put("status","200");
        responseMap.put("message","success");

        return responseMap;
    }

    @Transactional
    public Map<String,Object> login(Map<String,Object> body){

        String email = (String)body.get("email");
        String password = (String)body.get("password");

        Member member = memberRepository.findByEmail(email).orElse(null);

        if(member == null)
        {
            responseMap.put("status","400");
            responseMap.put("message","fail");
            return responseMap;
        }

        if(!passwordEncoder.matches(password,member.getPassword())){
            responseMap.put("status","400");
            responseMap.put("message","fail");
            return responseMap;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,member.getPassword());

        System.out.println("authenticationToken : "+authenticationToken);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = tokenProvider.generateToken(authentication);

        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .key(authentication.getName())
                .value(jwtToken.getRefreshToken())
                .build();

        refreshTokenRepository.save(RefreshToken.toEntity(refreshTokenDto));

        MemberDto memberDto = MemberDto.toDto(member);
        responseMap.put("status", "200");
        responseMap.put("member", memberDto);
        responseMap.put("token", jwtToken);

        System.out.println("responseMap : "+responseMap);

        return responseMap;
    }

    @Transactional
    public Map<String,Object> reissue(Map<String,String> requestJwtData){
        try{
            if(!tokenProvider.validateToken(requestJwtData.get("refreshToken").toString())){
                throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
            }

            Authentication authentication = tokenProvider.getAuthentication(requestJwtData.get("accessToken").toString());

            RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName()).orElseThrow(()-> new RuntimeException("로그아웃 된 사용자입니다."));

            if(!refreshToken.getValue().equals(requestJwtData.get("refreshToken"))){
                throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
            }

            JwtToken jwtToken = tokenProvider.generateToken(authentication);

            RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder().key(refreshToken.getKey()).value(refreshToken.getValue()).build();

            RefreshToken newRefreshToken = RefreshToken.toEntity(refreshTokenDto.updateValue(jwtToken.getRefreshToken()));

            responseMap.put("status", "200");
            responseMap.put("token", jwtToken);
        }catch (Exception e){
            responseMap.put("status","400");
        }
        return responseMap;
    }
}
