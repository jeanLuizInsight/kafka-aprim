package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.utils.Message;
import br.com.alura.ecommerce.utils.MessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

public class GsonDeserializer implements Deserializer<Message> {

    // não precisa mais dessa configuração, pois agora sempre será deserializado um objeto Message
    //public static final String TYPE_CONFIG = "br.com.alura.ecommerce.type_config";
    //private Class<T> type;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();


    // não precisa mais dessa configuração, pois agora sempre será deserializado um objeto Message
//    @Override
//    public void configure(Map<String, ?> configs, boolean isKey) {
//        try {
//            this.type = (Class<T>) Class.forName(String.valueOf(configs.get(TYPE_CONFIG)));
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("Type para deserialização não encontrado!");
//        }
//    }

    @Override
    public Message deserialize(String s, byte[] bytes) {
        return gson.fromJson(new String(bytes), Message.class);
    }
}
