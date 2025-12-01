package org.example.mediaconnect.service;

import org.example.mediaconnect.event.UserDeletedEvent;
import org.example.mediaconnect.repository.UserShowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaDeletionServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private UserShowRepository userShowRepository;

    @InjectMocks
    private MediaDeletionService mediaDeletionService;

    @Test
    void testHandleUserDeletion_Success() {
        String userId = "user123";
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);

        when(userShowRepository.deleteByUserId(userId)).thenReturn(5);

        mediaDeletionService.handleUserDeletion(event);

        verify(userShowRepository, times(1)).deleteByUserId(userId);

        ArgumentCaptor<UserDeletedEvent> eventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                eventCaptor.capture()
        );

        UserDeletedEvent sentEvent = eventCaptor.getValue();
        assertEquals(userId, sentEvent.getUserId());
        assertEquals("COMPLETED", sentEvent.getStatus());
    }

    @Test
    void testHandleUserDeletion_NoShowsDeleted() {
        String userId = "user456";
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);

        when(userShowRepository.deleteByUserId(userId)).thenReturn(0);

        mediaDeletionService.handleUserDeletion(event);

        verify(userShowRepository, times(1)).deleteByUserId(userId);

        ArgumentCaptor<UserDeletedEvent> eventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                eventCaptor.capture()
        );

        UserDeletedEvent sentEvent = eventCaptor.getValue();
        assertEquals("COMPLETED", sentEvent.getStatus());
    }

    @Test
    void testHandleUserDeletion_Exception() {
        String userId = "user789";
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);

        when(userShowRepository.deleteByUserId(userId))
                .thenThrow(new RuntimeException("Database connection failed"));

        mediaDeletionService.handleUserDeletion(event);

        verify(userShowRepository, times(1)).deleteByUserId(userId);

        ArgumentCaptor<UserDeletedEvent> eventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                eventCaptor.capture()
        );

        UserDeletedEvent sentEvent = eventCaptor.getValue();
        assertEquals(userId, sentEvent.getUserId());
        assertEquals("FAILED", sentEvent.getStatus());
        assertNotNull(sentEvent.getErrorMessage());
        assertEquals("Database connection failed", sentEvent.getErrorMessage());
    }
}