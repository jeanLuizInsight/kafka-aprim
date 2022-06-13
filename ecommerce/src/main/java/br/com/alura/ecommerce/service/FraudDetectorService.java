package br.com.alura.ecommerce.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Serviço para consumir o tópico de novas ordens e verificar se existe fraude.
 */
public class FraudDetectorService {

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse);
        service.run();
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("----------------");
        System.out.println("processing new order, checking for fraud...");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        // simulando chamada fraude
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Do not...
        }
        System.out.println("order processed!");
    }
}
