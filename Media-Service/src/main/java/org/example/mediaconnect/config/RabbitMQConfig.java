package org.example.mediaconnect.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String USER_EVENTS_EXCHANGE = "user.events";

    // Queue for this service
    public static final String USER_DELETE_REQUEST_QUEUE = "user.delete.request.queue";

    // Routing keys
    public static final String USER_DELETE_KEY = "user.delete";
    public static final String USER_DELETE_RESPONSE_KEY = "user.delete.response";

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue userDeleteRequestQueue() {
        return QueueBuilder.durable(USER_DELETE_REQUEST_QUEUE).build();
    }

    @Bean
    public Binding userDeleteRequestBinding(Queue userDeleteRequestQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userDeleteRequestQueue)
                .to(userEventsExchange)
                .with(USER_DELETE_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
