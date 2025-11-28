package org.example.authservice.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.example.authservice.event.UserDeletedEvent;
import org.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDeletionService Tests")
class UserDeletionServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDeletionService userDeletionService;

    private static final String USER_EVENTS_EXCHANGE = "user.events";
    private static final String USER_DELETE_KEY = "user.delete";
    private UUID testUserId;
    private String testUserIdStr;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserIdStr = testUserId.toString();
    }

    @Test
    @DisplayName("Should initiate user deletion and send event to RabbitMQ")
    void testInitiateUserDeletion_Success() {
        // Arrange
        var eventCaptor = org.mockito.ArgumentCaptor.forClass(UserDeletedEvent.class);

        // Act
        userDeletionService.initiateUserDeletion(testUserIdStr);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(USER_EVENTS_EXCHANGE),
                eq(USER_DELETE_KEY),
                eventCaptor.capture()
        );
        UserDeletedEvent sentEvent = eventCaptor.getValue();
        assert sentEvent.getUserId().equals(testUserIdStr);
        assert "INITIATED".equals(sentEvent.getStatus());
    }

    @Test
    @DisplayName("Should throw RuntimeException when RabbitTemplate fails")
    void testInitiateUserDeletion_RabbitTemplateThrowsException() {
        // Arrange
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate)
                .convertAndSend(eq(USER_EVENTS_EXCHANGE), eq(USER_DELETE_KEY), any(UserDeletedEvent.class));

        // Act & Assert
        try {
            userDeletionService.initiateUserDeletion(testUserIdStr);
            throw new AssertionError("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assert e.getMessage().contains("Failed to initiate user deletion");
            verify(rabbitTemplate).convertAndSend(
                    eq(USER_EVENTS_EXCHANGE),
                    eq(USER_DELETE_KEY),
                    any(UserDeletedEvent.class)
            );
        }
    }

    @Test
    @DisplayName("Should delete user when media deletion is completed")
    void testHandleMediaDeletionComplete_Success() {
        // Arrange
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(testUserIdStr);
        event.setStatus("COMPLETED");

        // Act
        userDeletionService.handleMediaDeletionComplete(event);

        // Assert
        verify(userRepository, times(1)).deleteByUserId(testUserId);
    }

    @Test
    @DisplayName("Should log error when media deletion fails")
    void testHandleMediaDeletionComplete_MediaDeletionFailed() {
        // Arrange
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(testUserIdStr);
        event.setStatus("FAILED");
        event.setErrorMessage("Media files not found");

        // Act
        userDeletionService.handleMediaDeletionComplete(event);

        // Assert
        verify(userRepository, never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("Should not delete user when status is neither COMPLETED nor FAILED")
    void testHandleMediaDeletionComplete_UnknownStatus() {
        // Arrange
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(testUserIdStr);
        event.setStatus("PENDING");

        // Act
        userDeletionService.handleMediaDeletionComplete(event);

        // Assert
        verify(userRepository, never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("Should handle invalid UUID format gracefully")
    void testHandleMediaDeletionComplete_InvalidUUID() {
        // Arrange
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId("invalid-uuid");
        event.setStatus("COMPLETED");

        // Act
        userDeletionService.handleMediaDeletionComplete(event);

        // Assert
        verify(userRepository, never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("Should handle repository exception during user deletion")
    void testHandleMediaDeletionComplete_RepositoryThrowsException() {
        // Arrange
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(testUserIdStr);
        event.setStatus("COMPLETED");

        doThrow(new RuntimeException("Database error"))
                .when(userRepository)
                .deleteByUserId(testUserId);

        // Act - should not throw exception
        userDeletionService.handleMediaDeletionComplete(event);

        // Assert
        verify(userRepository, times(1)).deleteByUserId(testUserId);
    }

    @Test
    @DisplayName("Should set correct event properties on initiation")
    void testInitiateUserDeletion_EventProperties() {
        // Arrange
        var captor = org.mockito.ArgumentCaptor.forClass(UserDeletedEvent.class);

        // Act
        userDeletionService.initiateUserDeletion(testUserIdStr);

        // Assert
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), captor.capture());
        UserDeletedEvent capturedEvent = captor.getValue();
        assert capturedEvent.getUserId().equals(testUserIdStr);
        assert "INITIATED".equals(capturedEvent.getStatus());
    }

    @Test
    @DisplayName("Should handle null error message in failed status")
    void testHandleMediaDeletionComplete_FailedWithNullErrorMessage() {
        // Arrange
        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(testUserIdStr);
        event.setStatus("FAILED");
        event.setErrorMessage(null);

        // Act - should not throw exception
        userDeletionService.handleMediaDeletionComplete(event);

        // Assert
        verify(userRepository, never()).deleteByUserId(any());
    }
}