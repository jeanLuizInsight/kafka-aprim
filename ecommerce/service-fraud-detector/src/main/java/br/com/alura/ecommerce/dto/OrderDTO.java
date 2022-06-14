package br.com.alura.ecommerce.dto;

import java.math.BigDecimal;

public class OrderDTO {
    private final String userId, orderId;
    private final BigDecimal amount;

    public OrderDTO(final String userId, final String orderId, final BigDecimal amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
    }
}
