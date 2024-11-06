package com.example.chatting.controller;

import com.example.chatting.dto.ChatMessage;
import com.example.chatting.entity.Chat;
import com.example.chatting.entity.ChatRoom;
import com.example.chatting.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    ChatService chatService;

    private Map<String,Object> responseMap = new HashMap<>();

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate)
    {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage)
    {
        chatService.insertChat(chatMessage);
        messagingTemplate.convertAndSend("/topic/public/" + chatMessage.getRoomId(), chatMessage);
    }

    @MessageMapping("chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username",chatMessage.getSender());
        return chatMessage;
    }

    @GetMapping("selectChatRoom")
    public @ResponseBody List<ChatRoom> selectChatRoom()
    {
        return chatService.selectChatRoom();
    }

    @PostMapping("/makeChatRoom")
    public @ResponseBody Map<String,Object> makeChatRoom(@RequestBody Map<String,Object> body)
    {
        return chatService.makeChatRoom(body);
    }

    @GetMapping("/selectChatContent/{roomId}")
    public @ResponseBody List<Chat> selectChatContent(@PathVariable Long roomId){
        return chatService.selectChatContent(roomId);
    }
}
