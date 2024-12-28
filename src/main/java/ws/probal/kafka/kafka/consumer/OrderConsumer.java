package ws.probal.kafka.kafka.consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderConsumer {

    @KafkaListener(topics = "order-events", groupId = "order-group", concurrency = "2", containerFactory = "kafkaListenerContainerFactory")
    public void consumeMessage(String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        System.out.println("Received message from partition " + partition + ": " + message);
    }

}
