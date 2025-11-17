// ---------------------------------------------------------------------
// GAME BROADCAST SERVICE
// Sends updated room state over WebSocket/STOMP to subscribed clients.
// Wraps a SimpMessagingTemplate and defines a method for broadcasting RoomDTOs.
// ---------------------------------------------------------------------

package com.cluewave.service;

import com.cluewave.dto.RoomDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public GameBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastRoomUpdate(String roomCode, RoomDTO dto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, dto);
    }
}