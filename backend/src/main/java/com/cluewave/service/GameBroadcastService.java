package com.cluewave.service;

import com.cluewave.dto.RoomDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Helper service to broadcast room updates over the STOMP message broker.  The
 * SimpMessagingTemplate is provided by Spring and handles serialization and
 * delivery to connected clients who have subscribed to the destination.
 */
@Service
public class GameBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public GameBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcasts an updated RoomDTO to all clients subscribed to the room's
     * topic.  Clients should subscribe to `/topic/room/{code}` to receive
     * these updates.
     *
     * @param roomCode the code of the room to broadcast
     * @param dto      the updated room state
     */
    public void broadcastRoomUpdate(String roomCode, RoomDTO dto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, dto);
    }
}