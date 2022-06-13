package br.com.alura.ecommerce.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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
        var consumer = new KafkaConsumer<String, String>(getProperties());
        // escutando o tópico
        // OBS.: pode escutar mais de um, mas não é uma boa prática
        consumer.subscribe(Collections.singletonList("ECOMMERCE_NEW_ORDER"));
        var records = consumer.poll(Duration.ofMillis(100));
        if (records.isEmpty()) {
            System.out.println("nenhum registro encontrado.");
            return;
        }
        records.forEach(record -> {
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
        });
    }

    /**
     * Propriedades para registro do consumer.
     * @return
     */
    private static Properties getProperties() {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // consumers obrigatoriamente devem conter o groupId. Isso garante que ele vai receber todas as mensagens do tópico
        // por isso cada consumer tem seu grupo, importante ser único para conseguir saber quais mensagens esse serviço
        // consumiu
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName());
        return properties;
    }
}
