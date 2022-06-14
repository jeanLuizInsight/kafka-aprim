package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.dto.OrderDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Serviço para consumir o tópico de novas ordens e verificar se existe fraude.
 */
public class FraudDetectorService {

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try(var service = new KafkaService<OrderDTO>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                OrderDTO.class)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, OrderDTO> record) {
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
        System.out.println("order processed!");
    }
}
