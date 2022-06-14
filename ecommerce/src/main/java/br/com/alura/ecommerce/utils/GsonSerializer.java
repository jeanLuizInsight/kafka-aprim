package br.com.alura.ecommerce.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Classe utilitária utilizada para Serialização de Objetos para o Kafka
 * Trabalhando com código Kafka (não mais arquitetura)
 * @param <T>
 */
public class GsonSerializer<T> implements Serializer<T> {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public byte[] serialize(String s, T t) {
        return gson.toJson(t).getBytes();
    }
}
