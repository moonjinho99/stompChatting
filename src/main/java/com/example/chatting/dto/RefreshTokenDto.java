package com.example.chatting.dto;


import com.example.chatting.entity.RefreshToken;
import lombok.*;

import javax.persistence.Column;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    private String key;

    private String value;

    public RefreshTokenDto updateValue(String token){
        this.value = token;
        return this;
    }

    public static RefreshTokenDto toDto(RefreshToken entity){
        return RefreshTokenDto.builder()
                .key(entity.getKey())
                .value(entity.getValue())
                .build();
    }
}
