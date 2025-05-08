package com.pizzeria.orderservice.utils.enums;

public enum OrderStatus {
    PENDING,
    ORDER_RECEIVED,
    PAYMENT_PROCESSING,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,
    PREPARING,
    PREPARED,
    OUT_FOR_DELIVERY,
    COMPLETED,
    CANCELLED
}
