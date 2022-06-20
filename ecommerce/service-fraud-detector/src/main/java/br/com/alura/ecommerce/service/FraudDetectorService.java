package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.dto.OrderDTO;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * Serviço para consumir o tópico de novas ordens e verificar se existe fraude.
 */
public class FraudDetectorService {

    private final KafkaDispatcher<OrderDTO> orderDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        var fraudService = new FraudDetectorService();
        try(var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<OrderDTO>> record) throws ExecutionException, InterruptedException {
        var message = record.value();
        System.out.println("----------------");
        System.out.println("processing new order, checking for fraud...");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        // simulando chamada fraude
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Do not...
        }
        var order = message.getPayload();
        if (this.isFraud(order)) {
            System.out.println("Ordem é uma fraude!");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED",
                    order.getEmail(),
                    order,
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
        } else {
            System.out.println("Ordem aprovada!");
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED",
                    order.getEmail(),
                    order,
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
        }
    }

    private boolean isFraud(final OrderDTO order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
