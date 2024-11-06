package com.example.chatting.entity;

import com.example.chatting.dto.RefreshTokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name="rt_key")
    private String key;

    @Column(name = "rt_value")
    private String value;

    public static RefreshToken toEntity(RefreshTokenDto refreshTokenDto){
        return RefreshToken.builder()
                .key(refreshTokenDto.getKey())
                .value(refreshTokenDto.getValue())
                .build();
    }
}
