package ws.probal.kafka.kafka.controller;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController()
@RequestMapping(value = "/app/kafka")
public class KafkaHealthCheckController {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @GetMapping("/topic-details")
    public Map<String, Object> getTopicDetails(@RequestParam String topic) {

        Map<String, Object> response = new HashMap<>();

        try (AdminClient adminClient = AdminClient.create(Map.of("bootstrap.servers", bootstrapServers))) {
            DescribeTopicsResult topicsResult = adminClient.describeTopics(Collections.singleton(topic));
            Map<String, TopicDescription> topicDescriptionMap = topicsResult.allTopicNames().get();
            TopicDescription topicDescription = topicDescriptionMap.get(topic);

            if (topicDescription == null) {
                response.put("error", "Topic not found: " + topic);
                return response;
            }

            response.put("topic", topic);
            response.put("partition_count", topicDescription.partitions().size());

            Map<Integer, Map<String, Object>> partitionInfo = new HashMap<>();
            for (TopicPartitionInfo topicPartitionInfo : topicDescription.partitions()) {
                Map<String, Object> partitionDetails = new HashMap<>();
                partitionDetails.put("leader", topicPartitionInfo.leader().id());
                partitionDetails.put("replicas", topicPartitionInfo.replicas().stream().map(Node::id).toList());
                partitionDetails.put("isr", topicPartitionInfo.isr().stream().map(Node::id).toList());

                partitionInfo.put(topicPartitionInfo.partition(), partitionDetails);
            }
            response.put("partitions", partitionInfo);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
