package com.cluewave.service;

import com.cluewave.dto.RoomDTO;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GameBroadcastService}.
 * Verifies that the service delegates correctly to {@link SimpMessagingTemplate}.
 */
class GameBroadcastServiceTest {

    @Test
    void broadcastRoomUpdate_sendsToCorrectDestination() {
        // Arrange
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        GameBroadcastService service = new GameBroadcastService(messagingTemplate);

        // On n'a pas besoin d'un vrai RoomDTO, un mock suffit
        RoomDTO dto = mock(RoomDTO.class);

        // Act
        service.broadcastRoomUpdate("ABCD", dto);

        // Assert
        verify(messagingTemplate).convertAndSend("/topic/room/ABCD", dto);
    }
}
