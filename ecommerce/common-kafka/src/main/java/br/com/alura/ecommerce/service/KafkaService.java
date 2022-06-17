package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.utils.GsonDeserializer;
import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Classe service bridge para realizar chamada consumer ao Kafka
 */
public class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse) {
        this(groupId, parse, Map.of());
        consumer.subscribe(Collections.singletonList(topic));
    }
    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> overrideProperties) {
        this(groupId, parse, overrideProperties);
        // escutando o tópico
        // obs.: aqui vai escutar mais de um, com regex
        this.consumer.subscribe(topic);
    }

    private KafkaService(String groupId, ConsumerFunction<T> parse, Map<String, String> overrideProperties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, overrideProperties));
    }

    public void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() + " registros.");
                records.forEach(record -> {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        // Do not...
                    }
                });
            }
        }
    }

    /**
     * Propriedades para registro do consumer.
     * @return
     */
    private Properties getProperties(final String groupId, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        // sobrescrevendo propriedades customizadas
        // ex.: para logs quero deserializar apenas String, e não Object (JSON)
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
