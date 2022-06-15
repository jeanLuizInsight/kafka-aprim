package br.com.alura.ecommerce.dto;

import java.math.BigDecimal;

public class OrderDTO {
    private final String orderId;
    private final BigDecimal amount;
    private final String email;

    public OrderDTO(final String orderId, final BigDecimal amount, final String email) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }
}
