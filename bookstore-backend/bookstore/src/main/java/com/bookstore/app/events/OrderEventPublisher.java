package com.bookstore.app.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderEventPublisher {
	
    private static final String TOPIC = "bookstore.order-placed";
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    
    public OrderEventPublisher(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /** Publish only after the database transaction commits. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        kafkaTemplate.send(TOPIC, event.orderNumber(), event)
                .whenComplete((result, failure) -> {
                    if (failure != null) {
                        log.error("Could not publish order-placed event for order {}", event.orderNumber(), failure);
                    }
                });
    }
}
