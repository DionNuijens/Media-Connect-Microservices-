package org.example.mediaconnect.service;

import org.example.mediaconnect.event.UserDeletedEvent;
import org.example.mediaconnect.repository.UserShowRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MediaDeletionService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserShowRepository userShowRepository;

    private static final String USER_EVENTS_EXCHANGE = "user.events";
    private static final String USER_DELETE_RESPONSE_KEY = "user.delete.response";

    @RabbitListener(queues = "user.delete.request.queue")
    public void handleUserDeletion(UserDeletedEvent event) {
        String userId = event.getUserId();
        log.info("Received user deletion request for userId: {}", userId);

        UserDeletedEvent responseEvent = new UserDeletedEvent();
        responseEvent.setUserId(userId);

        try {
            // Delete all user shows from the joined table
            long deletedCount = userShowRepository.deleteByUserId(userId);
            log.info("Deleted {} shows for userId: {}", deletedCount, userId);

            // Send success response back to Account Service
            responseEvent.setStatus("COMPLETED");
            log.info("Sending COMPLETED response for userId: {}", userId);
            rabbitTemplate.convertAndSend(USER_EVENTS_EXCHANGE, USER_DELETE_RESPONSE_KEY, responseEvent);

            log.info("User deletion completed for userId: {}", userId);
        } catch (Exception e) {
            log.error("Error deleting user shows for userId: {}", userId, e);

            // Send failure response
            responseEvent.setStatus("FAILED");
            responseEvent.setErrorMessage(e.getMessage());
            log.info("Sending FAILED response for userId: {}", userId);
            rabbitTemplate.convertAndSend(USER_EVENTS_EXCHANGE, USER_DELETE_RESPONSE_KEY, responseEvent);
        }
    }
}