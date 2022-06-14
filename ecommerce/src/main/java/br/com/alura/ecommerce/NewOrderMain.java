package br.com.alura.ecommerce;

import br.com.alura.ecommerce.service.KafkaDispatcher;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.lang.System.getProperties;

/**
 * Producer para nova ordem no ecommerce
 */
public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // garantindo que qualquer exception irá fechar o recurso do producer
        try(var dispatcher = new KafkaDispatcher()) {
            for (var i = 0; i < 10; i++) {
                var key = UUID.randomUUID().toString();
                var value = "132123,67523,345654647";
                dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);
                var email = "Seu pedido está sendo processado.";
                dispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
            }
        }
    }
}