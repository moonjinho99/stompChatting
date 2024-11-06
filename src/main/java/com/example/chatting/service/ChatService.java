package com.example.chatting.service;

import com.example.chatting.dto.ChatMessage;
import com.example.chatting.entity.Chat;
import com.example.chatting.entity.ChatRoom;
import com.example.chatting.repository.ChatRepository;
import com.example.chatting.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    private Map<String, Object> responseMap = new HashMap<>();

    public List<ChatRoom> selectChatRoom()
    {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();

        return chatRooms;
    }

    public Map<String, Object> makeChatRoom(Map<String, Object> body)
    {
        ChatRoom chatRoom = ChatRoom.builder().name(body.get("name").toString()).build();

        chatRoomRepository.save(chatRoom);

        responseMap.put("status","200");
        responseMap.put("message","success");

        return responseMap;
    }

    public void insertChat(ChatMessage chatMessage)
    {
        Chat chat = Chat.builder()
                .sender(chatMessage.getSender())
                .message(chatMessage.getContent())
                .room(ChatRoom.builder().id(Long.parseLong(chatMessage.getRoomId())).build())
                .build();

        chatRepository.save(chat);
    }

    public List<Chat> selectChatContent(Long id)
    {
        return chatRepository.findByRoomIdOrderBySendDateAsc(id);
    }
}
