package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.utils.CorrelationID;
import br.com.alura.ecommerce.utils.GsonSerializer;
import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Classe service Bridge para produzir mensagens no kafka
 */
public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

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
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    @Override
    public void close() {
        producer.close();
    }

    private ProducerRecord<String, Message<T>> getProducerRecord(String topico, String key, T payload, CorrelationID correlationId) {
        var value = new Message<T>(correlationId, payload);
        // registro: topico, chave, valor
        return new ProducerRecord<String, Message<T>>(topico, key, value);
    }

    private Callback getCallback() {
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("mensagem enviada com sucesso: " + data.topic() +
                    "::partition " + data.partition() +
                    "/ offset " + data.offset());
        };
        return callback;
    }

    public void send(String topico, String key, T payload, CorrelationID correlationId) throws ExecutionException, InterruptedException {
        var record = this.getProducerRecord(topico, key, payload, correlationId);
        var callback = this.getCallback();
        producer.send(record, callback).get();
    }
    public Future<RecordMetadata> sendAssync(String topico, String key, T payload, CorrelationID correlationId) throws ExecutionException, InterruptedException {
        var record = this.getProducerRecord(topico, key, payload, correlationId);
        var callback = this.getCallback();
        return producer.send(record, callback);
    }



}
