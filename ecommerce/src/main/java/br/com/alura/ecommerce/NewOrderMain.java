package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dto.OrderDTO;
import br.com.alura.ecommerce.service.KafkaDispatcher;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.math.BigDecimal;
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
        try(var dispatcher = new KafkaDispatcher<OrderDTO>()) {
            try(var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 10; i++) {
                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = Math.random() * 5000 + 1;
                    var dto = new OrderDTO(userId, orderId, new BigDecimal(amount));
                    dispatcher.send("ECOMMERCE_NEW_ORDER", userId, dto);
                    var email = "Seu pedido está sendo processado.";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);
                }
            }
        }
    }
}