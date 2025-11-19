package org.example.authservice.service;

import org.example.authservice.event.UserDeletedEvent;
import org.example.authservice.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDeletionService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserRepository userRepository;

    private static final String USER_EVENTS_EXCHANGE = "user.events";
    private static final String USER_DELETE_KEY = "user.delete";

    public void initiateUserDeletion(String userId) {
        try {
            log.info("Initiating user deletion for userId: {}", userId);

            UserDeletedEvent event = new UserDeletedEvent();
            event.setUserId(userId);
            event.setStatus("INITIATED");

            // Send message to Media Service
            rabbitTemplate.convertAndSend(USER_EVENTS_EXCHANGE, USER_DELETE_KEY, event);

            log.info("User deletion event sent to Media Service for userId: {}", userId);
        } catch (Exception e) {
            log.error("Error initiating user deletion for userId: {}", userId, e);
            throw new RuntimeException("Failed to initiate user deletion", e);
        }
    }

    @Transactional
    @RabbitListener(queues = "user.delete.response.queue")
    public void handleMediaDeletionComplete(UserDeletedEvent event) {
        log.info("Received response from Media Service for userId: {}", event.getUserId());

        try {
            if ("COMPLETED".equals(event.getStatus())) {
                String userIdStr = event.getUserId();
                java.util.UUID userId = java.util.UUID.fromString(userIdStr);

                // Delete user by userId (UUID)
                userRepository.deleteByUserId(userId);
                log.info("User account deleted successfully: {}", userIdStr);
            } else if ("FAILED".equals(event.getStatus())) {
                log.error("Media deletion failed for userId: {}. Error: {}",
                        event.getUserId(), event.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Error handling media deletion response for userId: {}", event.getUserId(), e);
        }
    }
}