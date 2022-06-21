package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceProvider;
import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class EmailService implements ConsumerService<String> {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // executando 10 serviços de e-mail em paralelo
        var serviceProvider = new ServiceProvider(EmailService::new);
        var pool = Executors.newFixedThreadPool(10);
        for (var i = 0; i < 10; i++) {
            serviceProvider.call();
        }
    }

    @Override
    public void parse(ConsumerRecord<String, Message<String>> record) {
        var message = record.value();
        System.out.println("----------------");
        System.out.println("processing send email...");
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
        System.out.println("email processed!");
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }
}
