package com.example.chatting.repository;

import com.example.chatting.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findByRoomIdOrderBySendDateAsc(Long roomId);
}
