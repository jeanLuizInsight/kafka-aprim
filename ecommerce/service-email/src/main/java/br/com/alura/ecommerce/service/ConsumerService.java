package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Interface para provedor de um serviço consumer
 * @param <T>
 */
public interface ConsumerService<T> {
    void parse(ConsumerRecord<String, Message<T>> record);
    String getTopic();
    String getConsumerGroup();
}
