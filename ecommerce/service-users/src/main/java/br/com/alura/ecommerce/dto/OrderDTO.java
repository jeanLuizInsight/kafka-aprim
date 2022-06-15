package br.com.alura.ecommerce.dto;

import java.math.BigDecimal;

public class OrderDTO {
    private final String userId, orderId;
    private final BigDecimal amount;
    private final String email;

    public OrderDTO(final String userId, final String orderId, final BigDecimal amount, final String email) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "userId='" + userId + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
