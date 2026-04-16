package ru.epta.notification.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper.TypePrecedence;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.epta.mtplanner.commons.model.notification.InviteNotification;
import ru.epta.mtplanner.commons.model.notification.MeetingNotification;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    @Value(value = "${spring.kafka.group-id}")
    private String groupId;
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String kafkaAddress;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {

        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public RecordMessageConverter recordMessageConverter() {
        StringJsonMessageConverter converter = new StringJsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("*");
        Map<String, Class<?>> mappings = new HashMap<>();

        mappings.put("send_invite", InviteNotification.class);
        mappings.put("meeting", MeetingNotification.class);
        typeMapper.setIdClassMapping(mappings);
        converter.setTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordMessageConverter(recordMessageConverter());
        return factory;
    }

}
