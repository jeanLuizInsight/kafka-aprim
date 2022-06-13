package br.com.alura.ecommerce.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Contrato padrão para utilizar função de consumir no tópico
 */
public interface ConsumerFunction {

    void consume(ConsumerRecord<String, String> record);
}
