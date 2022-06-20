package br.com.alura.ecommerce.dispatcher;

import br.com.alura.ecommerce.utils.Message;
import br.com.alura.ecommerce.utils.MessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Classe utilitária utilizada para Serialização de Objetos para o Kafka
 * Trabalhando com código Kafka (não mais arquitetura)
 * @param <T>
 */
public class GsonSerializer<T> implements Serializer<T> {

    // registrando um adapter personalizado para serialização/deserialização
    // pois dentro da mensagem vou querer saber qual é o tipo do payload
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    @Override
    public byte[] serialize(String s, T t) {
        return gson.toJson(t).getBytes();
    }
}
