package ws.probal.kafka.kafka.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.probal.kafka.kafka.producer.OrderProducer;

@RestController()
@RequestMapping(value = "/app/order")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping("/publish")
    public String publishMessage(@RequestBody String message) {
        orderProducer.sendMessage(message);
        return "Published!!!";
    }
}
