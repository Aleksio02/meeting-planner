package ru.epta.mtplanner.meeting.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String kafkaAddress;

    @Value(value = "${spring.kafka.primary-topic-name}")
    private String primaryTopicName;

    @Value(value = "#{new Integer('${spring.kafka.partitions:1}')}")
    private int partitions;

    @Value(value = "#{new Integer('${spring.kafka.replications:1}')}")
    private int replications;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic(primaryTopicName, partitions, (short) replications);
    }
}
