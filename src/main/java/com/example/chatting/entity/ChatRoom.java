package com.example.chatting.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoom_id")
    private Long id;
    private String name;

    @Builder
    public ChatRoom(Long id,String name){
        this.id = id;
        this.name = name;
    }

    public static ChatRoom createRoom(String name){
        return ChatRoom.builder()
                .name(name)
                .build();
    }

}
