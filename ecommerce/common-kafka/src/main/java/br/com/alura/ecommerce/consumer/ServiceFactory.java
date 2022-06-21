package br.com.alura.ecommerce.consumer;

import java.sql.SQLException;

/**
 * Interface para criação de um serviço (consumer)
 * @param <T>
 */
public interface ServiceFactory<T> {
    ConsumerService<T> create() throws SQLException;
}
