package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.dto.OrderDTO;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import br.com.alura.ecommerce.utils.CorrelationID;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Producer para nova ordem no ecommerce
 */
public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // garantindo que qualquer exception irá fechar o recurso do producer
        try(var dispatcher = new KafkaDispatcher<OrderDTO>()) {
            try(var emailDispatcher = new KafkaDispatcher<String>()) {
                var email = Math.random() + "@email.com";
                for (var i = 0; i < 10; i++) {
                    var orderId = UUID.randomUUID().toString();
                    var amount = Math.random() * 5000 + 1;
                    var dto = new OrderDTO(orderId, new BigDecimal(amount), email);
                    dispatcher.send("ECOMMERCE_NEW_ORDER",
                            email,
                            dto,
                            new CorrelationID(NewOrderMain.class.getSimpleName()));
                    var emailMsg = "Seu pedido está sendo processado.";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL",
                            email,
                            emailMsg,
                            new CorrelationID(NewOrderMain.class.getSimpleName()));
                }
            }
        }
    }
}