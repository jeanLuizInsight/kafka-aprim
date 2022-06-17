package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Contrato padrão para utilizar função de consumir no tópico
 */
public interface ConsumerFunction<T> {

    void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}
