package br.com.alura.ecommerce.utils;

import java.util.UUID;

public class CorrelationID {

    private final String id;

    public CorrelationID(String title) {
        this.id = title + "(" +UUID.randomUUID().toString() + ")";
    }

    @Override
    public String toString() {
        return "CorrelationID{" +
                "id='" + id + '\'' +
                '}';
    }

    public CorrelationID continueWith(String title) {
        return new CorrelationID(this.id + "-" + title);
    }
}
