package br.com.alura.ecommerce.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

/**
 * Contrato padrão para utilizar função de consumir no tópico
 */
public interface ConsumerFunction<T> {

    void consume(ConsumerRecord<String, T> record) throws ExecutionException, InterruptedException;
}
