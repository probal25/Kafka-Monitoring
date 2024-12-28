package ws.probal.kafka.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaTopicProperties {
    private List<TopicConfig> topicConfigs;


    public List<TopicConfig> getTopics() {
        return topicConfigs;
    }

    public void setTopics(List<TopicConfig> topicConfigs) {
        this.topicConfigs = topicConfigs;
    }

    public static class TopicConfig {
        private String name;
        private int partitions;
        private short replicationFactor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPartitions() {
            return partitions;
        }

        public void setPartitions(int partitions) {
            this.partitions = partitions;
        }

        public short getReplicationFactor() {
            return replicationFactor;
        }

        public void setReplicationFactor(short replicationFactor) {
            this.replicationFactor = replicationFactor;
        }
    }
}

