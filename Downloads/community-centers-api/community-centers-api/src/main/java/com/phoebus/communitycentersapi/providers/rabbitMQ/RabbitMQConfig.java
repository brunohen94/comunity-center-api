package com.phoebus.communitycentersapi.providers.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RabbitMQConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String QUEUE_NAME = "community_center_capacity_queue";
    public static final String EXCHANGE_NAME = "community_center_exchange";
    public static final String ROUTING_KEY = "center.capacity.full";

    @Bean
    public Queue queue() {
        logger.info("Creating queue: {}", QUEUE_NAME);
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange exchange() {
        logger.info("Creating exchange: {}", EXCHANGE_NAME);
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        logger.info("Binding queue {} to exchange {} with routing key {}", QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}