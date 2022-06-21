package br.com.alura.ecommerce.service;

/**
 * Interface para criação de um serviço (consumer)
 * @param <T>
 */
public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
