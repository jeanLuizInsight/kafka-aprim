package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.consumer.KafkaService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> implements Callable<Void> {

    private final ServiceFactory<T> factory;

    public ServiceProvider(final ServiceFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Void call() throws ExecutionException, InterruptedException {
        var emailService = factory.create();
        try(var service = new KafkaService<>(emailService.getConsumerGroup(),
                emailService.getTopic(),
                emailService::parse)) {
            service.run();
        }
        return null;
    }
}
