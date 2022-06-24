package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Interface para provedor de um serviço consumer
 * @param <T>
 */
public interface ConsumerService<T> {
    void parse(ConsumerRecord<String, Message<T>> record) throws SQLException, ExecutionException, InterruptedException;
    String getTopic();
    String getConsumerGroup();
}
