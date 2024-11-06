package com.example.chatting.entity;

import com.example.chatting.config.Authority;
import com.example.chatting.dto.MemberDto;
import lombok.*;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    public static Member toEntity(MemberDto memberDto){
        return Member.builder()
                .id(memberDto.getId())
                .email(memberDto.getEmail())
                .password(memberDto.getPassword())
                .authority(memberDto.getAuthority())
                .build();
    }
}
