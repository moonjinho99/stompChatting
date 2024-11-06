package com.example.chatting.dto;

import com.example.chatting.config.Authority;
import com.example.chatting.entity.Member;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long id;

    private String email;

    private String password;

    private Authority authority;

    public static MemberDto toDto(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .authority(member.getAuthority())
                .build();
    }
}
