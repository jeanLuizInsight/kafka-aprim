package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.utils.GsonSerializer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Classe service Bridge para produzir mensagens no kafka
 */
public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, T> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(getProperties());
    }

    /**
     * Propriedades para registro do producer.
     * @return
     */
    private static Properties getProperties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        return properties;
    }

    public void send(String topico, String key, T value) throws ExecutionException, InterruptedException {
        // registro: topico, chave, valor
        var record = new ProducerRecord<>(topico, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("mensagem enviada com sucesso: " + data.topic() +
                    "::partition " + data.partition() +
                    "/ offset " + data.offset());
        };
        producer.send(record, callback).get();
    }

    @Override
    public void close() {
        producer.close();
    }
}
