package br.com.alura.ecommerce.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Classe service bridge para realizar chamada consumer ao Kafka
 */
public class KafkaService implements Closeable {

    private final KafkaConsumer<String, String> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, String topic, ConsumerFunction parse) {
        this(groupId, parse);
        consumer.subscribe(Collections.singletonList(topic));
    }
    public KafkaService(String groupId, Pattern topic, ConsumerFunction parse) {
        this(groupId, parse);
        // escutando o tópico
        // obs.: aqui vai escutar mais de um, com regex
        this.consumer.subscribe(topic);
    }

    private KafkaService(String groupId, ConsumerFunction parse) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId));
    }

    public void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() + " registros.");
                records.forEach(record -> parse.consume(record));
            }
        }
    }

    /**
     * Propriedades para registro do consumer.
     * @return
     */
    private static Properties getProperties(final String groupId) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
