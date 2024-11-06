package com.example.chatting.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@ToString
public class ChatMessage {

    private MessageType type;
    private String content;
    private String sender;
    private String roomId;

    public enum MessageType{
        CHAT,
        JOIN,
        LEAVE
    }
}
