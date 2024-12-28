package ws.probal.kafka.kafka.producer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static String TOPIC = "order-events";

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
