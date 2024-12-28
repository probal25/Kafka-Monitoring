package ws.probal.kafka.kafka.config;


import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaTopicManager {


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private final KafkaTopicProperties kafkaTopicProperties;
    private final KafkaAdmin kafkaAdmin;

    public KafkaTopicManager(KafkaTopicProperties kafkaTopicProperties, KafkaAdmin kafkaAdmin) {
        this.kafkaTopicProperties = kafkaTopicProperties;
        this.kafkaAdmin = kafkaAdmin;
    }

    @PostConstruct
    public void initializeTopics() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            for (KafkaTopicProperties.TopicConfig topicConfig : kafkaTopicProperties.getTopics()) {
                updateTopicPartitions(adminClient, topicConfig);
            }
        }
    }

    private void updateTopicPartitions(AdminClient adminClient, KafkaTopicProperties.TopicConfig topicConfig) {
        try {
            Map<String, TopicDescription> topicDescriptionMap = adminClient
                    .describeTopics(Collections.singletonList(topicConfig.getName()))
                    .allTopicNames()
                    .get();

            TopicDescription existingTopicDescription = topicDescriptionMap.get(topicConfig.getName());

            if (existingTopicDescription != null) {
                int currentPartitions = existingTopicDescription.partitions().size();
                int desiredPartitions = topicConfig.getPartitions();

                if (desiredPartitions > currentPartitions) {
                    Map<String, NewPartitions> newPartitionsMap = new HashMap<>();
                    newPartitionsMap.put(topicConfig.getName(), NewPartitions.increaseTo(desiredPartitions));

                    adminClient.createPartitions(newPartitionsMap).all().get();

                    System.out.println("Successfully increased partitions for topic " + topicConfig.getName()
                            + " from " + currentPartitions + " to " + desiredPartitions);
                } else if (desiredPartitions < currentPartitions) {
                    System.out.println("Warning: Kafka does not support decreasing the number of partitions. "
                            + "Topic " + topicConfig.getName() + " will remain at " + currentPartitions + " partitions.");
                }
            } else {
                NewTopic newTopic = new NewTopic(
                        topicConfig.getName(),
                        topicConfig.getPartitions(),
                        topicConfig.getReplicationFactor()
                );

                CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
                result.all().get();
                System.out.println("Successfully created topic " + topicConfig.getName());
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to update topic " + topicConfig.getName(), e);
        }
    }
}
